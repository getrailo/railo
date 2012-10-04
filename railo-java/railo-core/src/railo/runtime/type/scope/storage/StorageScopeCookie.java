package railo.runtime.type.scope.storage;

import java.util.Date;

import railo.commons.io.log.Log;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.converter.ScriptConverter;
import railo.runtime.interpreter.CFMLExpressionInterpreter;
import railo.runtime.listener.ApplicationContext;
import railo.runtime.op.Caster;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.dt.DateTimeImpl;
import railo.runtime.type.dt.TimeSpan;
import railo.runtime.type.scope.Cookie;
import railo.runtime.type.scope.ScopeContext;
import railo.runtime.type.util.KeyConstants;

/**
 * client scope that store it's data in the cookie of the client
 */
public abstract class StorageScopeCookie extends StorageScopeImpl {

	private static final long serialVersionUID = -3509170569488448183L;
	
	private static ScriptConverter serializer=new ScriptConverter();
	protected static CFMLExpressionInterpreter evaluator=new CFMLExpressionInterpreter();
	//private Cookie cookie;
	private String cookieName;

	//private TimeSpan timeout;
	
	
	static {
		ignoreSet.add(KeyConstants._cfid);
		ignoreSet.add(KeyConstants._cftoken);
		ignoreSet.add(KeyConstants._urltoken);
		ignoreSet.add(KeyConstants._lastvisit);
		ignoreSet.add(KeyConstants._hitcount);
		ignoreSet.add(KeyConstants._timecreated);
	}
	

	
	/**
	 * Constructor of the class
	 * @param pc
	 * @param name
	 * @param sct
	 */
	protected StorageScopeCookie(PageContext pc,String cookieName,String strType,int type,Struct sct) {
		super(
				sct,
				doNowIfNull(pc,Caster.toDate(sct.get(TIMECREATED,null),false,pc.getTimeZone(),null)),
				doNowIfNull(pc,Caster.toDate(sct.get(LASTVISIT,null),false,pc.getTimeZone(),null)),
				-1,
				type==SCOPE_CLIENT?Caster.toIntValue(sct.get(HITCOUNT,"1"),1):0,
				strType,type);
		this.cookieName=cookieName;	
	}

	/**
	 * Constructor of the class, clone existing
	 * @param other
	 */
	protected StorageScopeCookie(StorageScopeCookie other,boolean deepCopy) {
		super(other,deepCopy);
		cookieName=other.cookieName;
	}
	
	private static DateTime doNowIfNull(PageContext pc,DateTime dt) {
		if(dt==null)return new DateTimeImpl(pc.getConfig());
		return dt;
	}
	
	public void touchAfterRequest(PageContext pc) {
		boolean _isInit=isinit;
		super.touchAfterRequest(pc);
		if(!_isInit) return;
		
		ApplicationContext ac=pc.getApplicationContext();
		TimeSpan timespan=(getType()==SCOPE_CLIENT)?ac.getClientTimeout():ac.getSessionTimeout();
		Cookie cookie = pc.cookieScope();
		
		
		Date exp = new DateTimeImpl(pc,System.currentTimeMillis()+timespan.getMillis(),true);
		try {
			String ser=serializer.serializeStruct(sct, ignoreSet);
			if(hasChanges()){
				cookie.setCookie(KeyImpl.init(cookieName), ser,exp, false, "/", null);
			}
			cookie.setCookie(KeyImpl.init(cookieName+"_LV"), Caster.toString(_lastvisit.getTime()), exp, false, "/", null);
			
			if(getType()==SCOPE_CLIENT){
				cookie.setCookie(KeyImpl.init(cookieName+"_TC"), Caster.toString(timecreated.getTime()),exp, false, "/", null);
				cookie.setCookie(KeyImpl.init(cookieName+"_HC"), Caster.toString(sct.get(HITCOUNT,"")), exp, false, "/", null);
			}
			
		} 
		catch (Throwable t) {}
	}
	
	/**
	 * @see railo.runtime.type.scope.storage.StorageScope#getStorageType()
	 */
	public String getStorageType() {
		return "Cookie";
	}


	protected static Struct _loadData(PageContext pc, String cookieName, int type,String strType, Log log) {
		String data = (String)pc.cookieScope().get(cookieName,null);
		if(data!=null) {			
			try {
				Struct sct = (Struct) evaluator.interpret(pc,data);
				long l;
				String str;
				
				// last visit
				str = (String)pc.cookieScope().get(cookieName+"_LV",null);
				if(!StringUtil.isEmpty(str)) {
					l=Caster.toLongValue(str,0);
					if(l>0)sct.setEL(LASTVISIT, new DateTimeImpl(pc,l,true));
				}
				
				
				if(type==SCOPE_CLIENT){
					// hit count
					str= (String)pc.cookieScope().get(cookieName+"_HC",null);
						if(!StringUtil.isEmpty(str)) sct.setEL(HITCOUNT, Caster.toDouble(str,null));
					
					// time created
					str = (String)pc.cookieScope().get(cookieName+"_TC",null);
					if(!StringUtil.isEmpty(str)) {
						l=Caster.toLongValue(str,0);
						if(l>0)sct.setEL(TIMECREATED, new DateTimeImpl(pc,l,true));
					}
				}
								
				ScopeContext.info(log,"load data from cookie for "+strType+" scope for "+pc.getApplicationContext().getName()+"/"+pc.getCFID());
				return sct;
			} 
			catch (Exception e) {
				
			}
		}
		ScopeContext.info(log,"create new "+strType+" scope for "+pc.getApplicationContext().getName()+"/"+pc.getCFID());
		
		return new StructImpl();
	}
	
	protected static boolean has(PageContext pc, String cookieName, int type,String strType) {
		// TODO better impl
		String data = (String)pc.cookieScope().get(cookieName,null);
		return data!=null;
		
	}
}
