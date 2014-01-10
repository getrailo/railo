package railo.runtime.engine;

import railo.commons.lang.SizeOf;
import railo.runtime.CFMLFactory;
import railo.runtime.CFMLFactoryImpl;
import railo.runtime.Mapping;
import railo.runtime.MappingImpl;
import railo.runtime.config.Config;
import railo.runtime.config.ConfigImpl;
import railo.runtime.config.ConfigServer;
import railo.runtime.config.ConfigWeb;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.query.QueryCacheSupport;
import railo.runtime.type.Collection;
import railo.runtime.type.DoubleStruct;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.scope.Scope;
import railo.runtime.type.scope.ScopeContext;
import railo.runtime.type.util.KeyConstants;

 class Surveillance {

	private static final Collection.Key PAGE_POOL = KeyImpl.intern("pagePool");
	private static final Collection.Key CLASS_LOADER = KeyImpl.intern("classLoader");
	private static final Collection.Key QUERY_CACHE = KeyImpl.intern("queryCache");
	private static final Collection.Key PAGE_CONTEXT_STACK = KeyImpl.intern("pageContextStack");
	
	
	public static Struct getInfo(ConfigImpl config) throws PageException {
		
		Struct sct=new StructImpl();
		
		// memory
		DoubleStruct mem=new DoubleStruct();
		sct.set(KeyConstants._memory, mem);
		getInfoMemory(mem, config);
		
		// count
		//ScopeContext sc = ((CFMLFactoryImpl)config.getFactory()).getScopeContext();
		//sc.getSessionCount(pc)
		
		
		return sct;
	}

	
	private static void getInfoMemory(Struct parent, ConfigImpl config) throws PageException {
		DoubleStruct server = new DoubleStruct();
		DoubleStruct web = new DoubleStruct();
		parent.set(KeyConstants._server, server);
		parent.set(KeyConstants._web, web);
		
		boolean isConfigWeb=config instanceof ConfigWeb;
		
		// server
		/*ConfigServer cs=isConfigWeb?
				config.getConfigServerImpl():
				((ConfigServer)config);
		*/
		
		//infoResources(server,cs);
		// web
		if(isConfigWeb){
			_getInfoMemory(web, server,config);
		}
		else  {
			ConfigWeb[] configs = ((ConfigServer) config).getConfigWebs();
			for(int i=0;i<configs.length;i++){
				_getInfoMemory(web,server, (ConfigImpl) configs[i]);	
			}
		}
	}

	private static void _getInfoMemory(Struct web, Struct server, ConfigImpl config) throws PageException {
		DoubleStruct sct = new DoubleStruct();
		infoMapping(sct,config);
		//infoResources(sct,config);
		
		infoScopes(sct,server,config);
		infoPageContextStack(sct,config.getFactory());
		infoQueryCache(sct,config.getFactory());
		//size+=infoResources(sct,cs);
		
		web.set(config.getConfigDir().getPath(), sct);
	}

	



	private static void infoMapping(Struct parent,Config config) throws PageException {
		DoubleStruct map=new DoubleStruct();
		infoMapping(map,config.getMappings(),false);
    	infoMapping(map,config.getCustomTagMappings(),true);
    	parent.set(KeyConstants._mappings, map);
	}

	private static void infoMapping(Struct map, Mapping[] mappings, boolean isCustomTagMapping) throws PageException {
		if(mappings==null) return;
		
		DoubleStruct sct=new DoubleStruct();
    	
		long size;
		MappingImpl mapping;
		for(int i=0;i<mappings.length;i++)	{
			mapping=(MappingImpl) mappings[i];
			
			// archive classloader
			size=SizeOf.size(mapping.getClassLoaderForArchive());
			sct.set("archiveClassLoader", Caster.toDouble(size));
			
			// physical classloader
			size=0;
			try {
				size=SizeOf.size(mapping.touchPCLCollection());
			} catch (Exception e) {}
			sct.set("physicalClassLoader", Caster.toDouble(size));
			
			// pagepool
			size=SizeOf.size(mapping.getPageSourcePool());
			sct.set(PAGE_POOL, Caster.toDouble(size));
			
			map.set(!isCustomTagMapping?
					mapping.getVirtual():mapping.getStrPhysical(), 
					sct);
		}
	}

	private static void infoScopes(Struct web,Struct server,ConfigImpl config) throws PageException {
		ScopeContext sc = ((CFMLFactoryImpl)config.getFactory()).getScopeContext();
		DoubleStruct webScopes=new DoubleStruct();
		DoubleStruct srvScopes=new DoubleStruct();
		
		long s;
		s=sc.getScopesSize(Scope.SCOPE_SESSION);
		webScopes.set("session", Caster.toDouble(s));
		
		s=sc.getScopesSize(Scope.SCOPE_APPLICATION);
		webScopes.set("application", Caster.toDouble(s));
		
		s=sc.getScopesSize(Scope.SCOPE_CLUSTER);
		srvScopes.set("cluster", Caster.toDouble(s));

		s=sc.getScopesSize(Scope.SCOPE_SERVER);
		srvScopes.set("server", Caster.toDouble(s));

		s=sc.getScopesSize(Scope.SCOPE_CLIENT);
		webScopes.set("client", Caster.toDouble(s));
		
		web.set(KeyConstants._scopes, webScopes);
		server.set(KeyConstants._scopes, srvScopes);
	}

	private static void infoQueryCache(Struct parent,CFMLFactory factory) throws PageException {
		long size= ((QueryCacheSupport)factory.getDefaultQueryCache()).sizeOf();
		parent.set(QUERY_CACHE, Caster.toDouble(size));
	}
	
	private static void infoPageContextStack(Struct parent,CFMLFactory factory) throws PageException {
		long size= ((CFMLFactoryImpl)factory).getPageContextsSize();
		parent.set(PAGE_CONTEXT_STACK, Caster.toDouble(size));
	}

	/*private static void infoResources(Struct parent, Config config) throws PageException {
		// add server proviers ti a set for checking
		Set set=new HashSet();
		if(config instanceof ConfigWeb){
			ConfigServerImpl cs=((ConfigImpl)config).getConfigServerImpl();
			ResourceProvider[] providers = cs.getResourceProviders();
			for(int i=0;i<providers.length;i++){
				set.add(providers[i]);
			}
		}
		
		ResourceProvider[] providers = ((ConfigImpl)config).getResourceProviders();
		DoubleStruct sct=new DoubleStruct();
		long s;
		for(int i=0;i<providers.length;i++){
			if(!set.contains(providers[i])){
				s=SizeOf.size(providers[i]);
				sct.set(providers[i].getScheme(), Caster.toDouble(s));
			}
		}
		parent.set("resourceProviders", sct);
	}*/
}
