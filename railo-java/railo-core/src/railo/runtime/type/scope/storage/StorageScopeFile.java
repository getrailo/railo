package railo.runtime.type.scope.storage;

import railo.commons.io.IOUtil;
import railo.commons.io.log.Log;
import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.config.Config;
import railo.runtime.config.ConfigImpl;
import railo.runtime.config.ConfigWeb;
import railo.runtime.converter.ScriptConverter;
import railo.runtime.interpreter.CFMLExpressionInterpreter;
import railo.runtime.op.Caster;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.dt.DateTimeImpl;
import railo.runtime.type.scope.ScopeContext;

/**
 * client scope that store it's data in a resource
 */
public abstract class StorageScopeFile extends StorageScopeImpl {

	private static final long serialVersionUID = -7519591903822909934L;

	public static final String STORAGE_TYPE = "File"; 
	
	private static ScriptConverter serializer=new ScriptConverter();
	protected static CFMLExpressionInterpreter evaluator=new CFMLExpressionInterpreter();
	
	private Resource res;

	/**
	 * Constructor of the class
	 * @param pc
	 * @param name
	 * @param sct
	 */
	protected StorageScopeFile(PageContext pc,Resource res,String strType,int type,Struct sct) {
		super(
				sct==null?(sct=new StructImpl()):sct,
				doNowIfNull(pc,Caster.toDate(sct.get(TIMECREATED,null),false,pc.getTimeZone(),null)),
				doNowIfNull(pc,Caster.toDate(sct.get(LASTVISIT,null),false,pc.getTimeZone(),null)),
				-1,
				type==SCOPE_CLIENT?Caster.toIntValue(sct.get(HITCOUNT,"1"),1):0,
				strType,type);
		
		this.res =res;// pc.getConfig().getClientScopeDir().getRealResource(name+"-"+pc.getCFID()+".script");
		
	}
	private static DateTime doNowIfNull(PageContext pc,DateTime dt) {
		if(dt==null)return new DateTimeImpl(pc.getConfig());
		return dt;
	}

	/**
	 * Constructor of the class, clone existing
	 * @param other
	 */
	protected StorageScopeFile(StorageScopeFile other,boolean deepCopy) {
		super(other,deepCopy);
		this.res=other.res;
	}

	@Override
	public void touchBeforeRequest(PageContext pc) {
		setTimeSpan(pc);
		super.touchBeforeRequest(pc);
	}

	@Override
	public void touchAfterRequest(PageContext pc) {
		setTimeSpan(pc);
		super.touchAfterRequest(pc);
		store(pc.getConfig());
	}
	
	
	
	@Override
	public void store(Config config) {
		//if(!super.hasContent()) return;
		try {
			if(!res.exists())ResourceUtil.createFileEL(res, true);
			IOUtil.write(res, (getTimeSpan()+System.currentTimeMillis())+":"+serializer.serializeStruct(sct, ignoreSet), "UTF-8", false);
		} 
		catch (Throwable t) {}
	}
	
	protected static Struct _loadData(PageContext pc,Resource res, Log log) {
		if(res.exists()) {
			try {
				String str=IOUtil.toString(res,"UTF-8");
				int index=str.indexOf(':');
				if(index!=-1){
					long expires=Caster.toLongValue(str.substring(0,index),-1L);
					// check is for backward compatibility, old files have no expires date inside. they do ot expire
					if(expires!=-1) {
						str=str.substring(index+1);
						/*if(checkExpires && expires<System.currentTimeMillis()){
							print.o("expired("+new Date(expires)+"):"+res);
							return null;
						}
						else {
							str=str.substring(index+1);
							print.o("not expired("+new Date(expires)+"):"+res);
							print.o(str);
						}*/
					}
				}
				Struct s = (Struct) evaluator.interpret(pc,str);
				ScopeContext.info(log,"load existing file storage ["+res+"]");
				return s;
			} 
			catch (Throwable t) {
				ScopeContext.error(log, t);
			}
		}
		ScopeContext.info(log,"create new file storage ["+res+"]");
		return null;
	}
	
	
	
	public void unstore(Config config) {
		try {
			if(!res.exists())return;
			res.remove(true);
		} 
		catch (Throwable t) {}
	}
	
	protected static Resource _loadResource(ConfigWeb config, int type,String name, String cfid) {
		ConfigImpl ci = (ConfigImpl)config;
		Resource dir= type==SCOPE_CLIENT?ci.getClientScopeDir():ci.getSessionScopeDir();
		return   dir.getRealResource(getFolderName(name,cfid,true));
	}
	

	
	/**
	 * return a folder name that match given input
	 * @param name
	 * @param cfid
	 * @param addExtension
	 * @return
	 */
	public static String getFolderName(String name, String cfid,boolean addExtension) {
		if(addExtension) return getFolderName(name, cfid, false)+".scpt";
		if(!StringUtil.isEmpty(name))
			name=encode(name);//StringUtil.toVariableName(StringUtil.toLowerCase(name));
		else 
			name="__empty__";
		return name+"/"+cfid.substring(0,2)+"/"+cfid.substring(2);
	}
	
	
	@Override
	public String getStorageType() {
		return STORAGE_TYPE;
	}
	
	
}
