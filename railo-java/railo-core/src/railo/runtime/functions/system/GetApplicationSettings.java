package railo.runtime.functions.system;

import java.util.Iterator;

import railo.commons.date.TimeZoneUtil;
import railo.commons.io.res.Resource;
import railo.runtime.Component;
import railo.runtime.ComponentWrap;
import railo.runtime.Mapping;
import railo.runtime.PageContext;
import railo.runtime.PageContextImpl;
import railo.runtime.config.Config;
import railo.runtime.config.ConfigImpl;
import railo.runtime.db.DataSource;
import railo.runtime.exp.PageException;
import railo.runtime.i18n.LocaleFactory;
import railo.runtime.listener.AppListenerUtil;
import railo.runtime.listener.ApplicationContextPro;
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
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.UDF;
import railo.runtime.type.scope.Scope;
import railo.runtime.type.scope.Undefined;
import railo.runtime.type.util.ArrayUtil;
import railo.runtime.type.util.KeyConstants;
import railo.runtime.type.util.ListUtil;

public class GetApplicationSettings {
	public static Struct call(PageContext pc) {
		return call(pc, false);
	}
	
	public static Struct call(PageContext pc, boolean suppressFunctions) {
		ApplicationContextPro ac = (ApplicationContextPro) pc.getApplicationContext();
		Component cfc = null;
		if(ac instanceof ModernApplicationContext)cfc= ((ModernApplicationContext)ac).getComponent();
		
		Struct sct=new StructImpl();
		sct.setEL("applicationTimeout", ac.getApplicationTimeout());
		sct.setEL("clientManagement", Caster.toBoolean(ac.isSetClientManagement()));
		sct.setEL("clientStorage", ac.getClientstorage());
		sct.setEL("sessionStorage", ac.getSessionstorage());
		sct.setEL("customTagPaths", toArray(ac.getCustomTagMappings()));
		sct.setEL("loginStorage", AppListenerUtil.translateLoginStorage(ac.getLoginStorage()));
		sct.setEL(KeyConstants._mappings, toStruct(ac.getMappings()));
		sct.setEL(KeyConstants._name, ac.getName());
		sct.setEL("scriptProtect", AppListenerUtil.translateScriptProtect(ac.getScriptProtect()));
		sct.setEL("secureJson", Caster.toBoolean(ac.getSecureJson()));
		sct.setEL("secureJsonPrefix", ac.getSecureJsonPrefix());
		sct.setEL("sessionManagement", Caster.toBoolean(ac.isSetSessionManagement()));
		sct.setEL("sessionTimeout", ac.getSessionTimeout());
		sct.setEL("clientTimeout", ac.getClientTimeout());
		sct.setEL("setClientCookies", Caster.toBoolean(ac.isSetClientCookies()));
		sct.setEL("setDomainCookies", Caster.toBoolean(ac.isSetDomainCookies()));
		sct.setEL(KeyConstants._name, ac.getName());
		sct.setEL("localMode", ac.getLocalMode()==Undefined.MODE_LOCAL_OR_ARGUMENTS_ALWAYS?Boolean.TRUE:Boolean.FALSE);
		sct.setEL(KeyConstants._locale,LocaleFactory.toString(pc.getLocale()));
		sct.setEL(KeyConstants._timezone,TimeZoneUtil.toString(pc.getTimeZone()));
		sct.setEL("sessionType", ((PageContextImpl) pc).getSessionType()==ConfigImpl.SESSION_TYPE_CFML?"cfml":"j2ee");
		sct.setEL("serverSideFormValidation", Boolean.FALSE); // TODO impl

		sct.setEL("clientCluster", Caster.toBoolean(ac.getClientCluster()));
		sct.setEL("sessionCluster", Caster.toBoolean(ac.getSessionCluster()));
		

		sct.setEL("invokeImplicitAccessor", Caster.toBoolean(ac.getTriggerComponentDataMember()));
		sct.setEL("triggerDataMember", Caster.toBoolean(ac.getTriggerComponentDataMember()));
		sct.setEL("sameformfieldsasarray", Caster.toBoolean(ac.getSameFieldAsArray(Scope.SCOPE_FORM)));
		sct.setEL("sameurlfieldsasarray", Caster.toBoolean(ac.getSameFieldAsArray(Scope.SCOPE_URL)));
		
		Object ds = ac.getDefDataSource();
		if(ds instanceof DataSource) ds=_call((DataSource)ds);
		else ds=Caster.toString(ds,null);
		sct.setEL(KeyConstants._datasource, ds);
		sct.setEL("defaultDatasource", ds);
		
		Resource src = ac.getSource();
		if(src!=null)sct.setEL(KeyConstants._source,src.getAbsolutePath());
		
		// orm
		if(ac.isORMEnabled()){
			ORMConfiguration conf = ac.getORMConfiguration();
			if(conf!=null)sct.setEL(KeyConstants._orm, conf.toStruct());
		}
		// s3
		Properties props = ac.getS3();
		if(props!=null) {
			sct.setEL(KeyConstants._s3, props.toStruct());
		}
		
		// datasources
		DataSource[] sources = ac.getDataSources();
		if(!ArrayUtil.isEmpty(sources)){
			Struct _sources = new StructImpl(),s;
			sct.setEL(KeyConstants._datasources, _sources);
			for(int i=0;i<sources.length;i++){
				_sources.setEL(KeyImpl.init(sources[i].getName()), _call(sources[i]));
			}
			
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
		jsSct.put("watchExtensions",ListUtil.arrayToList(js.watchedExtensions(),","));
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

	

	private static Struct _call(DataSource source) {
		Struct s = new StructImpl();
		s.setEL(KeyConstants._class, source.getClazz().getName());
		if(source.getConnectionLimit()>=0)s.setEL(AppListenerUtil.CONNECTION_LIMIT, Caster.toDouble(source.getConnectionLimit()));
		if(source.getConnectionTimeout()!=1)s.setEL(AppListenerUtil.CONNECTION_TIMEOUT, Caster.toDouble(source.getConnectionTimeout()));
		s.setEL(AppListenerUtil.CONNECTION_STRING, source.getDsnTranslated());
		if(source.getMetaCacheTimeout() != 60000)s.setEL(AppListenerUtil.META_CACHE_TIMEOUT, Caster.toDouble(source.getMetaCacheTimeout()));
		s.setEL(KeyConstants._username, source.getUsername());
		s.setEL(KeyConstants._password, source.getPassword());
		if(source.getTimeZone()!=null)s.setEL(AppListenerUtil.TIMEZONE, source.getTimeZone().getID());
		if(source.isBlob())s.setEL(AppListenerUtil.BLOB, source.isBlob());
		if(source.isClob())s.setEL(AppListenerUtil.CLOB, source.isClob());
		if(source.isReadOnly())s.setEL(AppListenerUtil.READ_ONLY, source.isReadOnly());
		if(source.isStorage())s.setEL(AppListenerUtil.STORAGE, source.isStorage());
		return s;
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
