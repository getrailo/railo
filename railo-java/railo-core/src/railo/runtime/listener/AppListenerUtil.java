package railo.runtime.listener;

import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.StringUtil;
import railo.commons.lang.types.RefBoolean;
import railo.runtime.Mapping;
import railo.runtime.MappingImpl;
import railo.runtime.PageContext;
import railo.runtime.PageSource;
import railo.runtime.config.Config;
import railo.runtime.config.ConfigImpl;
import railo.runtime.config.ConfigWeb;
import railo.runtime.config.ConfigWebImpl;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageException;
import railo.runtime.net.s3.Properties;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.orm.ORMConfiguration;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.Collection;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.List;
import railo.runtime.type.Scope;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.scope.Undefined;
import railo.runtime.util.ApplicationContext;

public final class AppListenerUtil {
	private static final Collection.Key ACCESS_KEY_ID = KeyImpl.getInstance("accessKeyId");
	private static final Collection.Key AWS_SECRET_KEY = KeyImpl.getInstance("awsSecretKey");
	private static final Collection.Key DEFAULT_LOCATION = KeyImpl.getInstance("defaultLocation");
	private static final Collection.Key HOST = KeyImpl.getInstance("host");
	private static final Collection.Key SERVER = KeyImpl.getInstance("server");
	private static final Collection.Key DATA_SOURCE = KeyImpl.getInstance("datasource");
	
	
	public static PageSource getApplicationPageSource(PageContext pc,PageSource requestedPage, String filename, int mode) {
		if(mode==ApplicationListener.MODE_CURRENT)return getApplicationPageSourceCurrent(requestedPage, filename);
		if(mode==ApplicationListener.MODE_ROOT)return getApplicationPageSourceRoot(pc, filename);
		return getApplicationPageSourceCurr2Root(pc, requestedPage, filename);
	}
	
	public static PageSource getApplicationPageSourceCurrent(PageSource requestedPage, String filename) {
		PageSource res=requestedPage.getRealPage(filename);
	    if(res.exists()) return res;
		return null;
	}
	
	public static PageSource getApplicationPageSourceRoot(PageContext pc, String filename) {
		PageSource res = pc.getPageSource("/".concat(filename));
		if(res.exists()) return res;
		return null;
	}
	
	public static PageSource getApplicationPageSourceCurr2Root(PageContext pc,PageSource requestedPage, String filename) {
		PageSource res=requestedPage.getRealPage(filename);
	    if(res.exists()) { 
			return res;
		}
	    Array arr=railo.runtime.type.List.listToArrayRemoveEmpty(requestedPage.getFullRealpath(),"/");
	    //Config config = pc.getConfig();
		for(int i=arr.size()-1;i>0;i--) {
		    StringBuffer sb=new StringBuffer("/");
			for(int y=1;y<i;y++) {
			    sb.append((String)arr.get(y,""));
			    sb.append('/');
			}
			sb.append(filename);
			res = pc.getPageSource(sb.toString());
			if(res.exists()) {
				return res;
			}
		}
		return null;
	}
	
	

	public static PageSource getApplicationPageSource(PageContext pc,PageSource requestedPage, int mode, RefBoolean isCFC) {
		if(mode==ApplicationListener.MODE_CURRENT)return getApplicationPageSourceCurrent(requestedPage, isCFC);
		if(mode==ApplicationListener.MODE_ROOT)return getApplicationPageSourceRoot(pc, isCFC);
		return getApplicationPageSourceCurr2Root(pc, requestedPage, isCFC);
	}
	
	public static PageSource getApplicationPageSourceCurrent(PageSource requestedPage, RefBoolean isCFC) {
		PageSource res=requestedPage.getRealPage("Application.cfc");
	    if(res.exists()) {
	    	isCFC.setValue(true);
	    	return res;
	    }
	    res=requestedPage.getRealPage("Application.cfm");
	    if(res.exists()) return res;
		return null;
	}
	
	public static PageSource getApplicationPageSourceRoot(PageContext pc, RefBoolean isCFC) {
		PageSource res = pc.getPageSource("/Application.cfc");
		if(res.exists()) {
			isCFC.setValue(true);
	    	return res;
		}
		res = pc.getPageSource("/Application.cfm");
		if(res.exists()) return res;
		return null;
	}
	

	public static PageSource getApplicationPageSourceCurr2Root(PageContext pc,PageSource requestedPage, RefBoolean isCFC) {
	    PageSource res=requestedPage.getRealPage("Application.cfc");
	    if(res.exists()) {
	    	isCFC.setValue(true);
	    	return res;
	    }
	    res=requestedPage.getRealPage("Application.cfm");
	    if(res.exists()) return res;
	    
	    Array arr=railo.runtime.type.List.listToArrayRemoveEmpty(requestedPage.getFullRealpath(),"/");
		//Config config = pc.getConfig();
		String path;
		for(int i=arr.size()-1;i>0;i--) {
		    StringBuilder sb=new StringBuilder("/");
			for(int y=1;y<i;y++) {
			    sb.append((String)arr.get(y,""));
			    sb.append('/');
			}
			path=sb.toString();
			res = pc.getPageSource(path.concat("Application.cfc"));
			if(res.exists()) {
				isCFC.setValue(true);
				return res;
			}

			res = pc.getPageSource(path.concat("Application.cfm"));
			if(res.exists()) return res;
		}
		return null;
	}
	
	public static String toStringMode(int mode) {
		if(mode==ApplicationListener.MODE_CURRENT)	return "curr";
		if(mode==ApplicationListener.MODE_ROOT)		return "root";
		return "curr2root";
	}

	public static String toStringType(ApplicationListener listener) {
		if(listener instanceof NoneAppListener)			return "none";
		else if(listener instanceof MixedAppListener)	return "mixed";
		else if(listener instanceof ClassicAppListener)	return "classic";
		else if(listener instanceof ModernAppListener)	return "modern";
		return "";
	}
	

	public static Mapping[] toMappings(ConfigWeb cw,Object o,Mapping[] defaultValue) {
		try {
			return toMappings(cw, o);
		} catch (Throwable t) {
			return defaultValue;
		}
	}

	public static Mapping[] toMappings(ConfigWeb cw,Object o) throws PageException {
		Struct sct = Caster.toStruct(o);
		Key[] keys = sct.keys();
		Mapping[] mappings=new Mapping[keys.length];
		ConfigWebImpl config=(ConfigWebImpl) cw;
		String virtual,physical;
		for(int i=0;i<keys.length;i++) {
			virtual=translateMappingVirtual(keys[i].getString());
			physical=Caster.toString(sct.get(keys[i]));
			mappings[i]=config.getApplicationMapping(virtual,physical);
			
		}
		return mappings;
	}
	

	private static String translateMappingVirtual(String virtual) {
		virtual=virtual.replace('\\', '/');
		if(!StringUtil.startsWith(virtual,'/'))virtual="/".concat(virtual);
		return virtual;
	}
	

	public static Mapping[] toCustomTagMappings(ConfigWeb cw, Object o,Mapping[] defaultValue) {
		try {
			return toCustomTagMappings(cw, o);
		} catch (Throwable t) {
			return defaultValue;
		}
	}

	public static Mapping[] toCustomTagMappings(ConfigWeb cw, Object o) throws PageException {
		Array array;
		if(o instanceof String){
			array=List.listToArrayRemoveEmpty(Caster.toString(o),',');
		}
		else if(o instanceof Struct){
			array=new ArrayImpl();
			Struct sct=(Struct) o;
			Key[] keys = sct.keys();
			for(int i=0;i<keys.length;i++) {
				array.append(sct.get(keys[i]));
			}
		}
		else {
			array=Caster.toArray(o);
		}
		MappingImpl[] mappings=new MappingImpl[array.size()];
		ConfigWebImpl config=(ConfigWebImpl) cw;
		for(int i=0;i<mappings.length;i++) {
			
			mappings[i]=(MappingImpl) config.createCustomTagAppMappings("/"+i,Caster.toString(array.getE(i+1)).trim());
			/*mappings[i]=new MappingImpl(
					config,"/"+i,
					Caster.toString(array.getE(i+1)).trim(),
					null,false,true,false,false,false
					);*/
		}
		return mappings;
	}

	public static int toLocalMode(Object oMode, int defaultValue) {
		if(oMode==null) return defaultValue;
		
		if(Decision.isBoolean(oMode)) {
			if(Caster.toBooleanValue(oMode, false))
				return Undefined.MODE_LOCAL_OR_ARGUMENTS_ALWAYS;
			return Undefined.MODE_LOCAL_OR_ARGUMENTS_ONLY_WHEN_EXISTS;
		}
		String strMode=Caster.toString(oMode,null);
		if("always".equalsIgnoreCase(strMode)) return Undefined.MODE_LOCAL_OR_ARGUMENTS_ALWAYS;
		if("update".equalsIgnoreCase(strMode)) return Undefined.MODE_LOCAL_OR_ARGUMENTS_ONLY_WHEN_EXISTS;
		return defaultValue;
	}
	
	public static int toLocalMode(String strMode) throws ApplicationException {
		int lm = toLocalMode(strMode, -1);
		if(lm!=-1) return lm;
		throw new ApplicationException("invalid localMode definition ["+strMode+"] for tag application/application.cfc, valid values are [always,update]");
	}

	public static short toSessionType(String str, short defaultValue) {
		if(!StringUtil.isEmpty(str,true)){
			str=str.trim().toLowerCase();
			if("cfml".equals(str)) return Config.SESSION_TYPE_CFML;
			if("j2ee".equals(str)) return Config.SESSION_TYPE_J2EE;
			if("cfm".equals(str)) return Config.SESSION_TYPE_CFML;
			if("jee".equals(str)) return Config.SESSION_TYPE_J2EE;
			if("j".equals(str)) return Config.SESSION_TYPE_J2EE;
			if("c".equals(str)) return Config.SESSION_TYPE_J2EE;
		}
		return defaultValue;
	}

	public static short toSessionType(String str) throws ApplicationException {
		if(!StringUtil.isEmpty(str,true)){
			str=str.trim().toLowerCase();
			if("cfml".equals(str)) return Config.SESSION_TYPE_CFML;
			if("j2ee".equals(str)) return Config.SESSION_TYPE_J2EE;
			if("cfm".equals(str)) return Config.SESSION_TYPE_CFML;
			if("jee".equals(str)) return Config.SESSION_TYPE_J2EE;
			if("j".equals(str)) return Config.SESSION_TYPE_J2EE;
			if("c".equals(str)) return Config.SESSION_TYPE_J2EE;
		}
		throw new ApplicationException("invalid sessionType definition ["+str+"] for tag application/application.cfc, valid values are [cfml,j2ee]");
	}
	
	public static Properties toS3(Struct sct) {
		String host=Caster.toString(sct.get(HOST,null),null);
		if(StringUtil.isEmpty(host))host=Caster.toString(sct.get(SERVER,null),null);
		
		return toS3(
				Caster.toString(sct.get(ACCESS_KEY_ID,null),null),
				Caster.toString(sct.get(AWS_SECRET_KEY,null),null),
				Caster.toString(sct.get(DEFAULT_LOCATION,null),null),
				host
			);
	}

	public static Properties toS3(String accessKeyId, String awsSecretKey, String defaultLocation, String host) {
		Properties s3 = new Properties();
		if(!StringUtil.isEmpty(accessKeyId))s3.setAccessKeyId(accessKeyId);
		if(!StringUtil.isEmpty(awsSecretKey))s3.setSecretAccessKey(awsSecretKey);
		if(!StringUtil.isEmpty(defaultLocation))s3.setDefaultLocation(defaultLocation);
		if(!StringUtil.isEmpty(host))s3.setHost(host);
		return s3;
	}

	public static void setORMConfiguration(PageContext pc, ApplicationContextPro ac,Struct sct) throws PageException {
		if(sct==null)sct=new StructImpl();
		Resource res=ResourceUtil.getResource(pc, pc.getCurrentTemplatePageSource()).getParentResource();
		ConfigImpl config=(ConfigImpl) pc.getConfig();
		ac.setORMConfiguration(ORMConfiguration.load(config,sct,res,config.getORMConfig()));
		
		// datasource
		Object o = sct.get(DATA_SOURCE,null);
		if(o!=null) ac.setORMDatasource(Caster.toString(o));
	}
	
	
	/**
	 * translate int definition of script protect to string definition
	 * @param scriptProtect
	 * @return
	 */
	public static String translateScriptProtect(int scriptProtect) {
		if(scriptProtect==ApplicationContext.SCRIPT_PROTECT_NONE) return "none";
		if(scriptProtect==ApplicationContext.SCRIPT_PROTECT_ALL) return "all";
		
		Array arr=new ArrayImpl();
		if((scriptProtect&ApplicationContext.SCRIPT_PROTECT_CGI)>0) arr.appendEL("cgi");
		if((scriptProtect&ApplicationContext.SCRIPT_PROTECT_COOKIE)>0) arr.appendEL("cookie");
		if((scriptProtect&ApplicationContext.SCRIPT_PROTECT_FORM)>0) arr.appendEL("form");
		if((scriptProtect&ApplicationContext.SCRIPT_PROTECT_URL)>0) arr.appendEL("url");
		
		
		
		try {
			return List.arrayToList(arr, ",");
		} catch (PageException e) {
			return "none";
		} 
	}
	

	/**
	 * translate string definition of script protect to int definition
	 * @param scriptProtect
	 * @return
	 */
	public static int translateScriptProtect(String strScriptProtect) {
		strScriptProtect=strScriptProtect.toLowerCase().trim();
		
		if("none".equals(strScriptProtect)) return ApplicationContext.SCRIPT_PROTECT_NONE;
		if("no".equals(strScriptProtect)) return ApplicationContext.SCRIPT_PROTECT_NONE;
		if("false".equals(strScriptProtect)) return ApplicationContext.SCRIPT_PROTECT_NONE;
		
		if("all".equals(strScriptProtect)) return ApplicationContext.SCRIPT_PROTECT_ALL;
		if("true".equals(strScriptProtect)) return ApplicationContext.SCRIPT_PROTECT_ALL;
		if("yes".equals(strScriptProtect)) return ApplicationContext.SCRIPT_PROTECT_ALL;
		
		String[] arr = List.listToStringArray(strScriptProtect, ',');
		String item;
		int scriptProtect=0;
		for(int i=0;i<arr.length;i++) {
			item=arr[i].trim();
			if("cgi".equals(item) && (scriptProtect&ApplicationContext.SCRIPT_PROTECT_CGI)==0)
				scriptProtect+=ApplicationContext.SCRIPT_PROTECT_CGI;
			else if("cookie".equals(item) && (scriptProtect&ApplicationContext.SCRIPT_PROTECT_COOKIE)==0)
				scriptProtect+=ApplicationContext.SCRIPT_PROTECT_COOKIE;
			else if("form".equals(item) && (scriptProtect&ApplicationContext.SCRIPT_PROTECT_FORM)==0)
				scriptProtect+=ApplicationContext.SCRIPT_PROTECT_FORM;
			else if("url".equals(item) && (scriptProtect&ApplicationContext.SCRIPT_PROTECT_URL)==0)
				scriptProtect+=ApplicationContext.SCRIPT_PROTECT_URL;
			
		}
		
		return scriptProtect;
	}
	

	public static String translateLoginStorage(int loginStorage) {
		if(loginStorage==Scope.SCOPE_SESSION) return "session";
		return "cookie";
	}
	

	public static int translateLoginStorage(String strLoginStorage, int defaultValue) {
		strLoginStorage=strLoginStorage.toLowerCase().trim();
	    if(strLoginStorage.equals("session"))return Scope.SCOPE_SESSION;
	    if(strLoginStorage.equals("cookie"))return Scope.SCOPE_COOKIE;
	    return defaultValue;
	}
	

	public static int translateLoginStorage(String strLoginStorage) throws ApplicationException {
		int ls=translateLoginStorage(strLoginStorage, -1);
		if(ls!=-1) return ls;
	    throw new ApplicationException("invalid loginStorage definition ["+strLoginStorage+"], valid values are [session,cookie]");
	}
}


