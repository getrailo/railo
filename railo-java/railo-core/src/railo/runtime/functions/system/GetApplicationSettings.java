package railo.runtime.functions.system;

import java.util.Iterator;

import railo.commons.io.res.Resource;
import railo.runtime.Component;
import railo.runtime.ComponentWrap;
import railo.runtime.Mapping;
import railo.runtime.PageContext;
import railo.runtime.PageContextImpl;
import railo.runtime.config.Config;
import railo.runtime.config.ConfigImpl;
import railo.runtime.exp.PageException;
import railo.runtime.listener.AppListenerUtil;
import railo.runtime.listener.ApplicationContext;
import railo.runtime.listener.JavaSettings;
import railo.runtime.listener.ModernApplicationContext;
import railo.runtime.net.s3.Properties;
import railo.runtime.op.Caster;
import railo.runtime.orm.ORMConfiguration;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.Collection;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.List;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.UDF;
import railo.runtime.type.scope.Scope;
import railo.runtime.type.scope.Undefined;
import railo.runtime.type.util.KeyConstants;

public class GetApplicationSettings {
	public static Struct call(PageContext pc) {
		return call(pc, false);
	}
	
	public static Struct call(PageContext pc, boolean suppressFunctions) {
		ApplicationContext ac = pc.getApplicationContext();
		Component cfc = null;
		if(ac instanceof ModernApplicationContext)cfc= ((ModernApplicationContext)ac).getComponent();
		
		Struct sct=new StructImpl();
		sct.setEL("applicationtimeout", ac.getApplicationTimeout());
		sct.setEL("clientmanagement", Caster.toBoolean(ac.isSetClientManagement()));
		sct.setEL("clientstorage", ac.getClientstorage());
		sct.setEL("sessionstorage", ac.getSessionstorage());
		sct.setEL("customtagpaths", toArray(ac.getCustomTagMappings()));
		sct.setEL(KeyConstants._datasource, ac.getDefaultDataSource());
		sct.setEL("loginstorage", AppListenerUtil.translateLoginStorage(ac.getLoginStorage()));
		sct.setEL("mappings", toStruct(ac.getMappings()));
		sct.setEL(KeyConstants._name, ac.getName());
		sct.setEL("scriptprotect", AppListenerUtil.translateScriptProtect(ac.getScriptProtect()));
		sct.setEL("securejson", Caster.toBoolean(ac.getSecureJson()));
		sct.setEL("securejsonprefix", ac.getSecureJsonPrefix());
		sct.setEL("sessionmanagement", Caster.toBoolean(ac.isSetSessionManagement()));
		sct.setEL("sessiontimeout", ac.getSessionTimeout());
		sct.setEL("clienttimeout", ac.getClientTimeout());
		sct.setEL("setclientcookies", Caster.toBoolean(ac.isSetClientCookies()));
		sct.setEL("setdomaincookies", Caster.toBoolean(ac.isSetDomainCookies()));
		sct.setEL(KeyConstants._name, ac.getName());
		sct.setEL("localmode", ac.getLocalMode()==Undefined.MODE_LOCAL_OR_ARGUMENTS_ALWAYS?"always":"update");
		sct.setEL("sessiontype", ((PageContextImpl) pc).getSessionType()==ConfigImpl.SESSION_TYPE_CFML?"cfml":"j2ee");
		sct.setEL("serverSideFormValidation", Boolean.FALSE); // TODO impl

		sct.setEL("clientCluster", Caster.toBoolean(ac.getClientCluster()));
		sct.setEL("sessionCluster", Caster.toBoolean(ac.getSessionCluster()));
		sct.setEL("sessionClusterKey", Caster.toString(ac.getSessionClusterKey()));
		

		sct.setEL("invokeImplicitAccessor", Caster.toBoolean(ac.getTriggerComponentDataMember()));
		sct.setEL("triggerDataMember", Caster.toBoolean(ac.getTriggerComponentDataMember()));
		sct.setEL("sameformfieldsasarray", Caster.toBoolean(ac.getSameFieldAsArray(Scope.SCOPE_FORM)));
		sct.setEL("sameurlfieldsasarray", Caster.toBoolean(ac.getSameFieldAsArray(Scope.SCOPE_URL)));
		
		
		Resource src = ac.getSource();
		if(src!=null)sct.setEL(KeyConstants._source,src.getAbsolutePath());
		
		// orm
		if(ac.isORMEnabled()){
			ORMConfiguration conf = ac.getORMConfiguration();
			if(conf!=null)sct.setEL("orm", conf.toStruct());
		}
		// s3
		Properties props = ac.getS3();
		if(props!=null) {
			sct.setEL("s3", props.toStruct());
		}
		
		//cache
		String func = ac.getDefaultCacheName(Config.CACHE_DEFAULT_FUNCTION);
		String obj = ac.getDefaultCacheName(Config.CACHE_DEFAULT_OBJECT);
		String qry = ac.getDefaultCacheName(Config.CACHE_DEFAULT_QUERY);
		String res = ac.getDefaultCacheName(Config.CACHE_DEFAULT_RESOURCE);
		String tmp = ac.getDefaultCacheName(Config.CACHE_DEFAULT_TEMPLATE);
		if(func!=null || obj!=null || qry!=null || res!=null || tmp!=null) {
			Struct cache=new StructImpl();
			sct.setEL(KeyConstants._cache, cache);
			if(func!=null)cache.setEL(KeyConstants._function, func);
			if(obj!=null)cache.setEL(KeyConstants._object, obj);
			if(qry!=null)cache.setEL(KeyConstants._query, qry);
			if(res!=null)cache.setEL(KeyConstants._resource, res);
			if(tmp!=null)cache.setEL(KeyConstants._template, tmp);
		}
		
		// java settings
		JavaSettings js = ac.getJavaSettings();
		StructImpl jsSct = new StructImpl();
		jsSct.put("loadCFMLClassPath",js.loadCFMLClassPath());
		jsSct.put("reloadOnChange",js.reloadOnChange());
		jsSct.put("watchInterval",new Double(js.watchInterval()));
		jsSct.put("watchExtensions",List.arrayToList(js.watchedExtensions(),","));
		Resource[] reses = js.getResources();
		StringBuilder sb=new StringBuilder();
		for(int i=0;i<reses.length;i++){
			if(i>0)sb.append(',');
			sb.append(reses[i].getAbsolutePath());
		}
		jsSct.put("loadCFMLClassPath",sb.toString());
		sct.put("javaSettings",jsSct);
		// REST Settings
		// MUST
		
		if(cfc!=null){
			sct.setEL(KeyConstants._component, cfc.getPageSource().getDisplayPath());
			
			try {
				ComponentWrap cw=ComponentWrap.toComponentWrap(Component.ACCESS_PRIVATE, cfc);
				Iterator<Key> it = cw.keyIterator();
				Collection.Key key;
				Object value;
		        while(it.hasNext()) {
		            key=it.next();
		            value=cw.get(key);
		            if(suppressFunctions && value instanceof UDF) continue;
		            if(!sct.containsKey(key))sct.setEL(key, value);
				}
			} 
			catch (PageException e) {e.printStackTrace();}
		}
		return sct;
	}

	

	private static Array toArray(Mapping[] mappings) {
		Array arr=new ArrayImpl();
		if(mappings!=null)for(int i=0;i<mappings.length;i++){
			arr.appendEL(mappings[i].getStrPhysical());
		}
		return arr;
	}

	private static Struct toStruct(Mapping[] mappings) {
		Struct sct=new StructImpl();
		if(mappings!=null)for(int i=0;i<mappings.length;i++){
			sct.setEL(KeyImpl.init(mappings[i].getVirtual()), mappings[i].getStrPhysical());
		}
		return sct;
	}
}
