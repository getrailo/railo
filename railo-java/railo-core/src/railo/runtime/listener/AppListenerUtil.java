package railo.runtime.listener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;

import railo.commons.io.res.Resource;
import railo.commons.lang.ClassException;
import railo.commons.lang.StringUtil;
import railo.commons.lang.types.RefBoolean;
import railo.runtime.Mapping;
import railo.runtime.MappingImpl;
import railo.runtime.PageContext;
import railo.runtime.PageContextImpl;
import railo.runtime.PageSource;
import railo.runtime.config.Config;
import railo.runtime.config.ConfigImpl;
import railo.runtime.config.ConfigWeb;
import railo.runtime.config.ConfigWebImpl;
import railo.runtime.config.Constants;
import railo.runtime.db.ApplicationDataSource;
import railo.runtime.db.DBUtil;
import railo.runtime.db.DBUtil.DataSourceDefintion;
import railo.runtime.db.DataSource;
import railo.runtime.db.DataSourceImpl;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageException;
import railo.runtime.net.s3.Properties;
import railo.runtime.net.s3.PropertiesImpl;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.orm.ORMConfigurationImpl;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.Collection;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.scope.Scope;
import railo.runtime.type.scope.Undefined;
import railo.runtime.type.util.KeyConstants;
import railo.runtime.type.util.ListUtil;

public final class AppListenerUtil {
	public static final Collection.Key ACCESS_KEY_ID = KeyImpl.intern("accessKeyId");
	public static final Collection.Key AWS_SECRET_KEY = KeyImpl.intern("awsSecretKey");
	public static final Collection.Key DEFAULT_LOCATION = KeyImpl.intern("defaultLocation");
	public static final Collection.Key CONNECTION_STRING = KeyImpl.intern("connectionString");
	
	public static final Collection.Key BLOB = KeyImpl.intern("blob");
	public static final Collection.Key CLOB = KeyImpl.intern("clob");
	public static final Collection.Key CONNECTION_LIMIT = KeyImpl.intern("connectionLimit");
	public static final Collection.Key CONNECTION_TIMEOUT = KeyImpl.intern("connectionTimeout");
	public static final Collection.Key META_CACHE_TIMEOUT = KeyImpl.intern("metaCacheTimeout");
	public static final Collection.Key TIMEZONE = KeyImpl.intern("timezone");
	public static final Collection.Key ALLOW = KeyImpl.intern("allow");
	public static final Collection.Key STORAGE = KeyImpl.intern("storage");
	public static final Collection.Key READ_ONLY = KeyImpl.intern("readOnly");
	public static final Collection.Key DATABASE = KeyConstants._database;
	
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
		PageSource ps = ((PageContextImpl)pc).getPageSourceExisting("/".concat(filename));
		if(ps!=null) return ps;
		return null;
	}
	
	public static PageSource getApplicationPageSourceCurr2Root(PageContext pc,PageSource requestedPage, String filename) {
		PageSource ps=requestedPage.getRealPage(filename);
	    if(ps.exists()) { 
			return ps;
		}
	    Array arr=railo.runtime.type.util.ListUtil.listToArrayRemoveEmpty(requestedPage.getFullRealpath(),"/");
	    //Config config = pc.getConfig();
		for(int i=arr.size()-1;i>0;i--) {
		    StringBuffer sb=new StringBuffer("/");
			for(int y=1;y<i;y++) {
			    sb.append((String)arr.get(y,""));
			    sb.append('/');
			}
			sb.append(filename);
			ps = ((PageContextImpl)pc).getPageSourceExisting(sb.toString());
			if(ps!=null) {
				return ps;
			}
		}
		return null;
	}
	
	

	public static PageSource getApplicationPageSource(PageContext pc,PageSource requestedPage, int mode, RefBoolean isCFC) {
		if(mode==ApplicationListener.MODE_CURRENT2ROOT)
			return getApplicationPageSourceCurr2Root(pc, requestedPage, isCFC);
		if(mode==ApplicationListener.MODE_CURRENT)
			return getApplicationPageSourceCurrent(requestedPage, isCFC);
		return getApplicationPageSourceRoot(pc, isCFC);
	}
	
	public static PageSource getApplicationPageSourceCurrent(PageSource requestedPage, RefBoolean isCFC) {
		PageSource res=requestedPage.getRealPage(Constants.APP_CFC);
	    if(res.exists()) {
	    	isCFC.setValue(true);
	    	return res;
	    }
	    res=requestedPage.getRealPage(Constants.APP_CFM);
	    if(res.exists()) return res;
		return null;
	}
	
	public static PageSource getApplicationPageSourceRoot(PageContext pc, RefBoolean isCFC) {
		PageSource ps = ((PageContextImpl)pc).getPageSourceExisting("/"+Constants.APP_CFC);
		if(ps!=null) {
			isCFC.setValue(true);
	    	return ps;
		}
		ps = ((PageContextImpl)pc).getPageSourceExisting("/"+Constants.APP_CFM);
		if(ps!=null) return ps;
		return null;
	}
	

	public static PageSource getApplicationPageSourceCurr2Root(PageContext pc,PageSource requestedPage, RefBoolean isCFC) {
	    PageSource res=requestedPage.getRealPage(Constants.APP_CFC);
	    if(res.exists()) {
	    	isCFC.setValue(true);
	    	return res;
	    }
	    res=requestedPage.getRealPage(Constants.APP_CFM);
	    if(res.exists()) return res;
	    
	    Array arr=railo.runtime.type.util.ListUtil.listToArrayRemoveEmpty(requestedPage.getFullRealpath(),"/");
		//Config config = pc.getConfig();
		String path;
		for(int i=arr.size()-1;i>0;i--) {
		    StringBuilder sb=new StringBuilder("/");
			for(int y=1;y<i;y++) {
			    sb.append((String)arr.get(y,""));
			    sb.append('/');
			}
			path=sb.toString();
			res = ((PageContextImpl)pc).getPageSourceExisting(path.concat(Constants.APP_CFC));
			if(res!=null) {
				isCFC.setValue(true);
				return res;
			}
			res = ((PageContextImpl)pc).getPageSourceExisting(path.concat(Constants.APP_CFM));
			if(res!=null) return res;
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
	
	public static DataSource[] toDataSources(Object o,DataSource[] defaultValue) {
		try {
			return toDataSources(o);
		} catch (Throwable t) {t.printStackTrace();
			return defaultValue;
		}
	}

	public static DataSource[] toDataSources(Object o) throws PageException, ClassException {
		Struct sct = Caster.toStruct(o);
		Iterator<Entry<Key, Object>> it = sct.entryIterator();
		Entry<Key, Object> e;
		java.util.List<DataSource> dataSources=new ArrayList<DataSource>();
		while(it.hasNext()) {
			e = it.next();
			dataSources.add(toDataSource(e.getKey().getString().trim(), Caster.toStruct(e.getValue())));
		}
		return dataSources.toArray(new DataSource[dataSources.size()]);
	}

	public static DataSource toDataSource(String name,Struct data) throws PageException, ClassException {
			String user = Caster.toString(data.get(KeyConstants._username,null),null);
			String pass = Caster.toString(data.get(KeyConstants._password,""),"");
			if(StringUtil.isEmpty(user)) {
				user=null;
				pass=null;
			}
			else {
				user=user.trim();
				pass=pass.trim();
			}
			
			// first check for {class:... , connectionString:...}
			Object oConnStr=data.get(CONNECTION_STRING,null);
			if(oConnStr!=null)
				return ApplicationDataSource.getInstance(
					name, 
					Caster.toString(data.get(KeyConstants._class)), 
					Caster.toString(oConnStr), 
					user, pass,
					Caster.toBooleanValue(data.get(BLOB,null),false),
					Caster.toBooleanValue(data.get(CLOB,null),false), 
					Caster.toIntValue(data.get(CONNECTION_LIMIT,null),-1), 
					Caster.toIntValue(data.get(CONNECTION_TIMEOUT,null),1), 
					Caster.toLongValue(data.get(META_CACHE_TIMEOUT,null),60000L), 
					Caster.toTimeZone(data.get(TIMEZONE,null),null), 
					Caster.toIntValue(data.get(ALLOW,null),DataSource.ALLOW_ALL),
					Caster.toBooleanValue(data.get(STORAGE,null),false),
					Caster.toBooleanValue(data.get(READ_ONLY,null),false));
			
			// then for {type:... , host:... , ...}
			String type=Caster.toString(data.get(KeyConstants._type));
			DataSourceDefintion dbt = DBUtil.getDataSourceDefintionForType(type, null);
			if(dbt==null) throw new ApplicationException("no datasource type ["+type+"] found");
			DataSourceImpl ds = new DataSourceImpl(
					name, 
					dbt.className, 
					Caster.toString(data.get(KeyConstants._host)), 
					dbt.connectionString,
					Caster.toString(data.get(DATABASE)), 
					Caster.toIntValue(data.get(KeyConstants._port,null),-1), 
					user,pass, 
					Caster.toIntValue(data.get(CONNECTION_LIMIT,null),-1), 
					Caster.toIntValue(data.get(CONNECTION_TIMEOUT,null),1), 
					Caster.toLongValue(data.get(META_CACHE_TIMEOUT,null),60000L), 
					Caster.toBooleanValue(data.get(BLOB,null),false), 
					Caster.toBooleanValue(data.get(CLOB,null),false), 
					DataSource.ALLOW_ALL, 
					Caster.toStruct(data.get(KeyConstants._custom,null),null,false), 
					Caster.toBooleanValue(data.get(READ_ONLY,null),false), 
					true, 
					Caster.toBooleanValue(data.get(STORAGE,null),false), 
					Caster.toTimeZone(data.get(TIMEZONE,null),null),
					""
			);

			return ds;
		
	}

	public static Mapping[] toMappings(ConfigWeb cw,Object o,Mapping[] defaultValue, Resource source) { 
		try {
			return toMappings(cw, o,source);
		} catch (Throwable t) {
			return defaultValue;
		}
	}

	public static Mapping[] toMappings(ConfigWeb cw,Object o, Resource source) throws PageException {
		Struct sct = Caster.toStruct(o);
		Iterator<Entry<Key, Object>> it = sct.entryIterator();
		Entry<Key, Object> e;
		java.util.List<Mapping> mappings=new ArrayList<Mapping>();
		ConfigWebImpl config=(ConfigWebImpl) cw;
		String virtual,physical;
		while(it.hasNext()) {
			e = it.next();
			virtual=translateMappingVirtual(e.getKey().getString());
			physical=translateMappingPhysical(Caster.toString(e.getValue()),source);
			mappings.add(config.getApplicationMapping(virtual,physical));
			
		}
		return mappings.toArray(new Mapping[mappings.size()]);
	}
	

	private static String translateMappingPhysical(String path, Resource source) {
		source=source.getParentResource().getRealResource(path);
		if(source.exists()) return source.getAbsolutePath();
		return path;
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
			array=ListUtil.listToArrayRemoveEmpty(Caster.toString(o),',');
		}
		else if(o instanceof Struct){
			array=new ArrayImpl();
			Struct sct=(Struct) o;
			Iterator<Object> it = sct.valueIterator();
			while(it.hasNext()) {
				array.append(it.next());
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


	public static String toLocalMode(int mode, String defaultValue) {
		if(Undefined.MODE_LOCAL_OR_ARGUMENTS_ALWAYS==mode) return "modern";
		if(Undefined.MODE_LOCAL_OR_ARGUMENTS_ONLY_WHEN_EXISTS==mode)return "classic";
		return defaultValue;
	}
	
	public static int toLocalMode(Object oMode, int defaultValue) {
		if(oMode==null) return defaultValue;
		
		if(Decision.isBoolean(oMode)) {
			if(Caster.toBooleanValue(oMode, false))
				return Undefined.MODE_LOCAL_OR_ARGUMENTS_ALWAYS;
			return Undefined.MODE_LOCAL_OR_ARGUMENTS_ONLY_WHEN_EXISTS;
		}
		String strMode=Caster.toString(oMode,null);
		if("always".equalsIgnoreCase(strMode) || "modern".equalsIgnoreCase(strMode)) 
			return Undefined.MODE_LOCAL_OR_ARGUMENTS_ALWAYS;
		if("update".equalsIgnoreCase(strMode) || "classic".equalsIgnoreCase(strMode)) 
			return Undefined.MODE_LOCAL_OR_ARGUMENTS_ONLY_WHEN_EXISTS;
		return defaultValue;
	}
	
	public static int toLocalMode(String strMode) throws ApplicationException {
		int lm = toLocalMode(strMode, -1);
		if(lm!=-1) return lm;
		throw new ApplicationException("invalid localMode definition ["+strMode+"] for tag "+Constants.CFAPP_NAME+"/"+Constants.APP_CFC+", valid values are [classic,modern,true,false]");
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
		throw new ApplicationException("invalid sessionType definition ["+str+"] for tag "+Constants.CFAPP_NAME+"/"+Constants.APP_CFC+", valid values are [cfml,j2ee]");
	}
	
	public static Properties toS3(Struct sct) {
		String host=Caster.toString(sct.get(KeyConstants._host,null),null);
		if(StringUtil.isEmpty(host))host=Caster.toString(sct.get(KeyConstants._server,null),null);
		
		return toS3(
				Caster.toString(sct.get(ACCESS_KEY_ID,null),null),
				Caster.toString(sct.get(AWS_SECRET_KEY,null),null),
				Caster.toString(sct.get(DEFAULT_LOCATION,null),null),
				host
			);
	}

	public static Properties toS3(String accessKeyId, String awsSecretKey, String defaultLocation, String host) {
		PropertiesImpl s3 = new PropertiesImpl();
		if(!StringUtil.isEmpty(accessKeyId))s3.setAccessKeyId(accessKeyId);
		if(!StringUtil.isEmpty(awsSecretKey))s3.setSecretAccessKey(awsSecretKey);
		if(!StringUtil.isEmpty(defaultLocation))s3.setDefaultLocation(defaultLocation);
		if(!StringUtil.isEmpty(host))s3.setHost(host);
		return s3;
	}

	public static void setORMConfiguration(PageContext pc, ApplicationContext ac,Struct sct) throws PageException {
		if(sct==null)sct=new StructImpl();
		Resource res=pc.getCurrentTemplatePageSource().getResourceTranslated(pc).getParentResource();
		ConfigImpl config=(ConfigImpl) pc.getConfig();
		ac.setORMConfiguration(ORMConfigurationImpl.load(config,ac,sct,res,config.getORMConfig()));
		
		// datasource
		Object o = sct.get(KeyConstants._datasource,null);
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
			return ListUtil.arrayToList(arr, ",");
		} catch (PageException e) {
			return "none";
		} 
	}
	

	/**
	 * translate string definition of script protect to int definition
	 * @param strScriptProtect
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
		
		String[] arr = ListUtil.listToStringArray(strScriptProtect, ',');
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


