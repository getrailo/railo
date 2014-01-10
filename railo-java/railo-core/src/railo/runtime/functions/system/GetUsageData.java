package railo.runtime.functions.system;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import railo.commons.io.SystemUtil;
import railo.commons.io.res.Resource;
import railo.commons.lang.SizeAndCount;
import railo.commons.lang.SizeAndCount.Size;
import railo.loader.engine.CFMLEngineFactory;
import railo.runtime.CFMLFactoryImpl;
import railo.runtime.Mapping;
import railo.runtime.MappingImpl;
import railo.runtime.PageContext;
import railo.runtime.PageContextImpl;
import railo.runtime.PageSourceImpl;
import railo.runtime.PageSourcePool;
import railo.runtime.config.ConfigImpl;
import railo.runtime.config.ConfigServer;
import railo.runtime.config.ConfigWeb;
import railo.runtime.config.ConfigWebImpl;
import railo.runtime.debug.ActiveLock;
import railo.runtime.debug.ActiveQuery;
import railo.runtime.engine.CFMLEngineImpl;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.lock.LockManager;
import railo.runtime.op.Caster;
import railo.runtime.type.Collection;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Query;
import railo.runtime.type.QueryImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.dt.DateTimeImpl;
import railo.runtime.type.scope.ScopeContext;
import railo.runtime.type.util.KeyConstants;

public final class GetUsageData implements Function {
    
	private static final Key START_TIME = KeyImpl.init("starttime");
	private static final Key CACHED_QUERIES = KeyImpl.init("cachedqueries");
	private static final Key OPEN_CONNECTIONS = KeyImpl.init("openconnections");
	private static final Key ELEMENTS = KeyImpl.init("elements");
	private static final Key USERS = KeyImpl.init("users");
	private static final Key QUERIES = KeyImpl.init("queries");
	private static final Key LOCKS = KeyImpl.init("locks");
	
	public static Struct call(PageContext pc) throws PageException  {
		ConfigWeb cw = pc.getConfig();
		ConfigServer cs = cw.getConfigServer("server");
		ConfigWeb[] webs = cs.getConfigWebs();
		CFMLEngineFactory.getInstance();
		CFMLEngineImpl engine = (CFMLEngineImpl) cs.getCFMLEngine();
		
		Struct sct=new StructImpl();
		
		
		// Locks
		/*LockManager manager = pc.getConfig().getLockManager();
        String[] locks = manager.getOpenLockNames();
        for(int i=0;i<locks.length;i++){
        	locks[i].
        }
        if(!ArrayUtil.isEmpty(locks)) 
        	strLocks=" open locks at this time ("+List.arrayToList(locks, ", ")+").";
		*/
		
		// Requests
		Query req=new QueryImpl(new Collection.Key[]{KeyConstants._web,KeyConstants._uri,START_TIME,KeyConstants._timeout}, 0, "requests");
		sct.setEL(KeyConstants._requests, req);
		
		// Template Cache
		Query tc=new QueryImpl(new Collection.Key[]{KeyConstants._web,ELEMENTS,KeyConstants._size}, 0, "templateCache");
		sct.setEL(KeyImpl.init("templateCache"), tc);
		
		// Scopes 
		Struct scopes=new StructImpl();
		sct.setEL(KeyConstants._scopes, scopes);
		Query app=new QueryImpl(new Collection.Key[]{KeyConstants._web,KeyConstants._application,ELEMENTS,KeyConstants._size}, 0, "templateCache");
		scopes.setEL(KeyConstants._application, app);
		Query sess=new QueryImpl(new Collection.Key[]{KeyConstants._web,KeyConstants._application,USERS,ELEMENTS,KeyConstants._size}, 0, "templateCache");
		scopes.setEL(KeyConstants._session, sess);

		// Query
		Query qry=new QueryImpl(new Collection.Key[]{KeyConstants._web,KeyConstants._application,START_TIME,KeyConstants._sql}, 0, "requests");
		sct.setEL(QUERIES, qry);
		
		// Locks
		Query lck=new QueryImpl(new Collection.Key[]{KeyConstants._web,KeyConstants._application,KeyConstants._name,START_TIME,KeyConstants._timeout,KeyConstants._type}, 0, "requests");
		sct.setEL(LOCKS, lck);

		// Loop webs
		ConfigWebImpl web;
		Struct pcs;
		PageContextImpl _pc;
		int row,openConnections=0;
		CFMLFactoryImpl factory;
		ActiveQuery[] queries;
		ActiveQuery aq;
		ActiveLock[] locks;
		ActiveLock al;
		for(int i=0;i<webs.length;i++){
			
			// Loop requests
			web=(ConfigWebImpl) webs[i];
			factory=(CFMLFactoryImpl)web.getFactory();
			pcs = factory.getRunningPageContexts();
			Iterator<Object> it = pcs.valueIterator();
			while(it.hasNext()){
				_pc = (PageContextImpl) it.next();
				if(_pc.isGatewayContext()) continue;
				
				// Request
				row = req.addRow();
				req.setAt(KeyConstants._web, row, web.getLabel());
				req.setAt(KeyConstants._uri, row, getPath(_pc.getHttpServletRequest()));
				req.setAt(START_TIME, row, new DateTimeImpl(pc.getStartTime(),false));
				req.setAt(KeyConstants._timeout, row, new Double(pc.getRequestTimeout()));
				
				// Query
				queries = _pc.getActiveQueries();
				if(queries!=null) {
					for(int y=0;y<queries.length;y++){
						aq=queries[y];
						row = qry.addRow();
						qry.setAt(KeyConstants._web, row, web.getLabel());
						qry.setAt(KeyConstants._application, row, _pc.getApplicationContext().getName());
						qry.setAt(START_TIME, row, new DateTimeImpl(web,aq.startTime,true));
						qry.setAt(KeyConstants._sql, row, aq.sql);
					}
				}
				
				// Lock
				locks = _pc.getActiveLocks();
				if(locks!=null) {
					for(int y=0;y<locks.length;y++){
						al=locks[y];
						row = lck.addRow();
						lck.setAt(KeyConstants._web, row, web.getLabel());
						lck.setAt(KeyConstants._application, row, _pc.getApplicationContext().getName());
						lck.setAt(KeyConstants._name, row, al.name);
						lck.setAt(START_TIME, row, new DateTimeImpl(web,al.startTime,true));
						lck.setAt(KeyConstants._timeout, row, Caster.toDouble(al.timeoutInMillis/1000));
						lck.setAt(KeyConstants._type, row, al.type==LockManager.TYPE_EXCLUSIVE?"exclusive":"readonly");
					}
				}
			}
			openConnections+=web.getDatasourceConnectionPool().openConnections();


			// Template Cache
			Mapping[] mappings = ConfigImpl.getAllMappings(web);
			long[] tce = templateCacheElements(mappings);
			row = tc.addRow();
			tc.setAt(KeyConstants._web, row, web.getLabel());
			tc.setAt(KeyConstants._size, row, new Double(tce[1]));
			tc.setAt(ELEMENTS, row, new Double(tce[0]));
			
			// Scope Application
			getAllApplicationScopes(web,factory.getScopeContext(),app);
			getAllCFSessionScopes(web,factory.getScopeContext(),sess);
			
			
		}
		
		// Datasource
		Struct ds=new StructImpl();
		sct.setEL(KeyConstants._datasources, ds);
		ds.setEL(CACHED_QUERIES, Caster.toDouble(pc.getQueryCache().size(pc))); // there is only one cache for all contexts
		ds.setEL(OPEN_CONNECTIONS, Caster.toDouble(openConnections));
		
		// Memory
		Struct mem=new StructImpl();
		sct.setEL(KeyConstants._memory, mem);
		mem.setEL("heap", SystemUtil.getMemoryUsageAsStruct(SystemUtil.MEMORY_TYPE_HEAP));
		mem.setEL("nonheap", SystemUtil.getMemoryUsageAsStruct(SystemUtil.MEMORY_TYPE_NON_HEAP));
		
		
		// uptime
		sct.set("uptime", new DateTimeImpl(engine.uptime(),true));
		
		// now
		sct.set("now", new DateTimeImpl(pc));
		
		
		//SizeAndCount.Size size = SizeAndCount.sizeOf(pc.serverScope());
		
		
		
		return sct;
    }
	
	private static void getAllApplicationScopes(ConfigWebImpl web, ScopeContext sc, Query app) throws PageException {
		Struct all = sc.getAllApplicationScopes();
		Iterator<Entry<Key, Object>> it = all.entryIterator();
		Entry<Key, Object> e;
		int row;
		Size sac;
		while(it.hasNext()){
			e = it.next();
			row=app.addRow();
			sac = SizeAndCount.sizeOf(e.getValue());
			app.setAt(KeyConstants._web, row, web.getLabel());
			app.setAt(KeyConstants._application, row, e.getKey().getString());
			app.setAt(KeyConstants._size, row, new Double(sac.size));
			app.setAt(ELEMENTS, row, new Double(sac.count));
			
		}
	}
	
	private static void getAllCFSessionScopes(ConfigWebImpl web, ScopeContext sc, Query sess) throws PageException {
		Struct all = sc.getAllCFSessionScopes();
		Iterator it = all.entryIterator(),itt;
		Entry e,ee;
		int row,size,count,users;
		Size sac;
		// applications
		while(it.hasNext()){
			e = (Entry) it.next();
			itt = ((Map)e.getValue()).entrySet().iterator();
			size=0;count=0;users=0;
			while(itt.hasNext()){
				ee=(Entry)itt.next();
				sac = SizeAndCount.sizeOf(ee.getValue());
				size+=sac.size;
				count+=sac.count;
				users++;
			}
			row=sess.addRow();
			
			sess.setAt(KeyConstants._web, row, web.getLabel());
			sess.setAt(USERS, row, new Double(users));
			sess.setAt(KeyConstants._application, row, e.getKey().toString());
			sess.setAt(KeyConstants._size, row, new Double(size));
			sess.setAt(ELEMENTS, row, new Double(count));
		}
	}
	
	private static long[] templateCacheElements(Mapping[] mappings) {
		long elements=0,size=0;
		
		PageSourcePool psp;
		Object[] keys;
		PageSourceImpl ps;
		Resource res;
		MappingImpl mapping;
		for(int i=0;i<mappings.length;i++){
			mapping=(MappingImpl)mappings[i];
			psp = mapping.getPageSourcePool();
			keys = psp.keys();
			for(int y=0;y<keys.length;y++)	{
				ps = (PageSourceImpl) psp.getPageSource(keys[y], false);
				if(ps.isLoad()) {
					elements++;
					res=mapping.getClassRootDirectory().getRealResource(ps.getJavaName()+".class");
					size+=res.length();
				}
			}
		}
		return new long[]{elements,size};
	}
	
	
	public static String getScriptName(HttpServletRequest req) {
		return emptyIfNull(req.getContextPath())+emptyIfNull(req.getServletPath());
	}

	public static String getPath(HttpServletRequest req) {
		String qs=emptyIfNull(req.getQueryString());
		if(qs.length()>0)qs="?"+qs;
			
			
		return emptyIfNull(req.getContextPath())+emptyIfNull(req.getServletPath())+qs;
	}

	private static String emptyIfNull(String str) {
		if(str==null) return "";
		return str;
	}
}