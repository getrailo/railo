package railo.runtime.type.scope;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.converter.ClientScopeConverter;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.dump.DumpTable;
import railo.runtime.op.Caster;
import railo.runtime.type.Collection;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.dt.DateTimeImpl;

public final class ClientCookieSimple extends ClientSupport {

	private static final long serialVersionUID = 6997060591476742923L;
	
	private Cookie cookie;
	private String cookieName;
	
	
	static {
		ignoreSet.add("cfid");
		ignoreSet.add("cftoken");
		ignoreSet.add("urltoken");
		ignoreSet.add("lastvisit");
		ignoreSet.add("hitcount");
		ignoreSet.add("timecreated");
	}
	

	
	/**
	 * Constructor of the class
	 * @param pc
	 * @param name
	 * @param sct
	 */
	private ClientCookieSimple(PageContext pc,String cookieName,Struct sct) {
		super(
				sct,
				doNowIfNull(pc,Caster.toDate(sct.get(TIMECREATED,null),false,pc.getTimeZone(),null)),
				doNowIfNull(pc,Caster.toDate(sct.get(LASTVISIT,null),false,pc.getTimeZone(),null)),
				-1,
				Caster.toIntValue(sct.get(HITCOUNT,"1"),1));
		this.cookie=pc.cookieScope();
		this.cookieName=cookieName;	
		
	}

	/**
	 * Constructor of the class, clone existing
	 * @param other
	 */
	private ClientCookieSimple(ClientCookieSimple other,boolean deepCopy) {
		super(other,deepCopy);
		cookie=other.cookie;
		cookieName=other.cookieName;
	}
	
	private static DateTime doNowIfNull(PageContext pc,DateTime dt) {
		if(dt==null)return new DateTimeImpl(pc.getConfig());
		return dt;
	}
	
	
	public static Client getInstance(String name, PageContext pc) {
		if(!StringUtil.isEmpty(name))
			name=StringUtil.toUpperCase(StringUtil.toVariableName(name));
		String cookieName="CF_CLIENT_"+name;
		
		
		String data = (String)pc.cookieScope().get(cookieName,null);
		if(data!=null) {			
			try {
				Struct sct = decode(data);
				long l;
				String str;
				
				// last visit
				str = (String)pc.cookieScope().get(cookieName+"_LV",null);
				if(!StringUtil.isEmpty(str)) {
					l=Caster.toLongValue(str,0);
					if(l>0)sct.setEL(LASTVISIT, new DateTimeImpl(pc,l,true));
				}
				// time created
				str = (String)pc.cookieScope().get(cookieName+"_TC",null);
				if(!StringUtil.isEmpty(str)) {
					l=Caster.toLongValue(str,0);
					if(l>0)sct.setEL(TIMECREATED, new DateTimeImpl(pc,l,true));
				}
				
				// hit count
				 str= (String)pc.cookieScope().get(cookieName+"_HC",null);
				if(!StringUtil.isEmpty(str)) sct.setEL(HITCOUNT, Caster.toDouble(str,null));

								
				return new ClientCookieSimple(pc,cookieName,sct);
			} 
			catch (Exception e) {
				
			}
		}
		return new ClientCookieSimple(pc,cookieName, new StructImpl());
	}

	public void release(PageContext pc) {
		release();
	}
	public void release() {
		boolean _isInit=isinit;
		super.release();
		if(!_isInit || !super.hasContent()) return;
		
		try {
			String ser=ClientScopeConverter.serialize(sct, ignoreSet);
			if(hasChanges())cookie.setCookie(cookieName, ser, 90*24*60*60, false, "/", null);
			cookie.setCookie(cookieName+"_LV", Caster.toString(_lastvisit.getTime()), 90*24*60*60, false, "/", null);
			cookie.setCookie(cookieName+"_HC", Caster.toString(sct.get(HITCOUNT,"")), 90*24*60*60, false, "/", null);
			cookie.setCookie(cookieName+"_TC", Caster.toString(timecreated.getTime()), 90*24*60*60, false, "/", null);
			
		} 
		catch (Exception e) {}
	}

	/**
	 * @see railo.runtime.dump.Dumpable#toDumpData(railo.runtime.PageContext, int)
	 */
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
		DumpTable table = super.toDumpTable(pageContext, maxlevel,dp);
		table.setTitle("Scope Client (Cookie)");
		return table;
	}
	
	/**
	 *
	 * @see railo.runtime.type.Collection#duplicate(boolean)
	 */
	public Collection duplicate(boolean deepCopy) {
    	return new ClientCookieSimple(this,deepCopy);
	}
	
	private static Struct decode(String data)  {
		return ClientScopeConverter.unserialize(data);
	}
}
