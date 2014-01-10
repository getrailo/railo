package railo.runtime; 

import java.net.URL;
import java.util.Iterator;
import java.util.Stack;

import javax.servlet.Servlet;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspEngineInfo;

import railo.commons.io.SystemUtil;
import railo.commons.io.log.Log;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.SizeOf;
import railo.commons.lang.SystemOut;
import railo.runtime.config.ConfigWeb;
import railo.runtime.config.ConfigWebImpl;
import railo.runtime.engine.CFMLEngineImpl;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.Abort;
import railo.runtime.exp.PageException;
import railo.runtime.exp.PageExceptionImpl;
import railo.runtime.exp.RequestTimeoutException;
import railo.runtime.functions.string.Hash;
import railo.runtime.lock.LockManager;
import railo.runtime.op.Caster;
import railo.runtime.query.QueryCache;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.Collection;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.dt.DateTimeImpl;
import railo.runtime.type.scope.ArgumentIntKey;
import railo.runtime.type.scope.LocalNotSupportedScope;
import railo.runtime.type.scope.ScopeContext;
import railo.runtime.type.util.ArrayUtil;
import railo.runtime.type.util.KeyConstants;
import railo.runtime.type.util.ListUtil;

/**
 * implements a JSP Factory, this class produce JSP Compatible PageContext Object
 * this object holds also the must interfaces to coldfusion specified functionlity
 */
public final class CFMLFactoryImpl extends CFMLFactory {
	
	private static JspEngineInfo info=new JspEngineInfoImpl("1.0");
	private ConfigWebImpl config;
	Stack<PageContext> pcs=new Stack<PageContext>();
    private Struct runningPcs=new StructImpl();
    int idCounter=1;
    private QueryCache queryCache;
    private ScopeContext scopeContext=new ScopeContext(this);
    private HttpServlet servlet;
	private URL url=null;
	private CFMLEngineImpl engine;

	/**
	 * constructor of the JspFactory
	 * @param config Railo specified Configuration
	 * @param compiler CFML compiler
	 * @param engine
	 */
	public CFMLFactoryImpl(CFMLEngineImpl engine,QueryCache queryCache) {
		this.engine=engine; 
		this.queryCache=queryCache;
	}
    
    /**
     * reset the PageContexes
     */
    public void resetPageContext() {
        SystemOut.printDate(config.getOutWriter(),"Reset "+pcs.size()+" Unused PageContexts");
        synchronized(pcs) {
            pcs.clear();
        }
        
        if(runningPcs!=null) {
        	synchronized(runningPcs) {
        	Iterator<Object> it = runningPcs.valueIterator();
        	while(it.hasNext()){
        		((PageContextImpl)it.next()).reset();
        	}
        	}
        }
    }
    
	@Override
	public javax.servlet.jsp.PageContext getPageContext(
		Servlet servlet,
		ServletRequest req,
		ServletResponse rsp,
		String errorPageURL,
		boolean needsSession,
		int bufferSize,
		boolean autoflush) {
			return getPageContextImpl((HttpServlet)servlet,(HttpServletRequest)req,(HttpServletResponse)rsp,errorPageURL,needsSession,bufferSize,autoflush,true,false);
	}
	
	/**
	 * similar to getPageContext Method but return the concrete implementation of the railo PageCOntext
	 * and take the HTTP Version of the Servlet Objects
	 * @param servlet
	 * @param req
	 * @param rsp
	 * @param errorPageURL
	 * @param needsSession
	 * @param bufferSize
	 * @param autoflush
	 * @return return the page<context
	 */
	public PageContext getRailoPageContext(
	HttpServlet servlet,
	HttpServletRequest req,
	HttpServletResponse rsp,
        String errorPageURL,
		boolean needsSession,
		int bufferSize,
		boolean autoflush)  {
        //runningCount++;
        return getPageContextImpl(servlet, req, rsp, errorPageURL, needsSession, bufferSize, autoflush,true,false);
	}
	
	public PageContextImpl getPageContextImpl(
			HttpServlet servlet,
			HttpServletRequest req,
			HttpServletResponse rsp,
		        String errorPageURL,
				boolean needsSession,
				int bufferSize,
				boolean autoflush,boolean registerPageContext2Thread,boolean isChild)  {
		        //runningCount++;
				PageContextImpl pc;
        		synchronized (pcs) {
		            if(pcs.isEmpty()) pc=new PageContextImpl(scopeContext,config,queryCache,idCounter++,servlet);
		            else pc=((PageContextImpl)pcs.pop());
		            runningPcs.setEL(ArgumentIntKey.init(pc.getId()),pc);
		            this.servlet=servlet;
		            if(registerPageContext2Thread)ThreadLocalPageContext.register(pc);
		    		
		        }
		        pc.initialize(servlet,req,rsp,errorPageURL,needsSession,bufferSize,autoflush,isChild);
		        return pc;
			}

    @Override
	public void releasePageContext(javax.servlet.jsp.PageContext pc) {
		releaseRailoPageContext((PageContext)pc);
	}
	
	/**
	 * Similar to the releasePageContext Method, but take railo PageContext as entry
	 * @param pc
	 */
	public void releaseRailoPageContext(PageContext pc) {
		if(pc.getId()<0)return;
        pc.release();
        ThreadLocalPageContext.release();
        //if(!pc.hasFamily()){
			synchronized (runningPcs) {
	            runningPcs.removeEL(ArgumentIntKey.init(pc.getId()));
	            if(pcs.size()<100)// not more than 100 PCs
	            	pcs.push(pc);
	            //SystemOut.printDate(config.getOutWriter(),"Release: (id:"+pc.getId()+";running-requests:"+config.getThreadQueue().size()+";)");
	        }
       /*}
        else {
        	 SystemOut.printDate(config.getOutWriter(),"Unlink: ("+pc.getId()+")");
        }*/
	}
    
    /**
	 * check timeout of all running threads, downgrade also priority from all thread run longer than 10 seconds
	 */
	public void checkTimeout() {
		if(!engine.allowRequestTimeout())return;
		synchronized (runningPcs) {
            //int len=runningPcs.size();
			Iterator it = runningPcs.keyIterator();
            PageContext pc;
            Collection.Key key;
            while(it.hasNext()) {
            	key=KeyImpl.toKey(it.next(),null);
                //print.out("key:"+key);
                pc=(PageContext) runningPcs.get(key,null);
                if(pc==null) {
                	runningPcs.removeEL(key);
                	continue;
                }
                
                long timeout=pc.getRequestTimeout();
                if(pc.getStartTime()+timeout<System.currentTimeMillis()) {
                    terminate(pc);
                }
                // after 10 seconds downgrade priority of the thread
                else if(pc.getStartTime()+10000<System.currentTimeMillis() && pc.getThread().getPriority()!=Thread.MIN_PRIORITY) {
                    Log log = config.getRequestTimeoutLogger();
                    if(log!=null)log.warn("controller","downgrade priority of the a thread at "+getPath(pc));
                    try {
                    	pc.getThread().setPriority(Thread.MIN_PRIORITY);
                    }
                    catch(Throwable t) {}
                }
            }
        }
	}
	
	public static void terminate(PageContext pc) {
		Log log = pc.getConfig().getRequestTimeoutLogger();
        
		String strLocks="";
		try{
			LockManager manager = pc.getConfig().getLockManager();
	        String[] locks = manager.getOpenLockNames();
	        if(!ArrayUtil.isEmpty(locks)) 
	        	strLocks=" open locks at this time ("+ListUtil.arrayToList(locks, ", ")+").";
	        //LockManagerImpl.unlockAll(pc.getId());
		}
		catch(Throwable t){}
        
        if(log!=null)log.error("controller",
        		"stop thread ("+pc.getId()+") because run into a timeout "+getPath(pc)+"."+strLocks);
        pc.getThread().stop(new RequestTimeoutException(pc,"request ("+getPath(pc)+":"+pc.getId()+") has run into a timeout ("+(pc.getRequestTimeout()/1000)+" seconds) and has been stopped."+strLocks));
        
	}

	private static String getPath(PageContext pc) {
		try {
			String base=ResourceUtil.getResource(pc, pc.getBasePageSource()).getAbsolutePath();
			String current=ResourceUtil.getResource(pc, pc.getCurrentPageSource()).getAbsolutePath();
			if(base.equals(current)) return "path: "+base;
			return "path: "+base+" ("+current+")";
		}
		catch(Throwable t) {
			return "";
		}
	}
	
	@Override
	public JspEngineInfo getEngineInfo() {
		return info;
	}


	/**
	 * @return returns count of pagecontext in use
	 */
	public int getUsedPageContextLength() { 
		int length=0;
		try{
		Iterator it = runningPcs.values().iterator();
		while(it.hasNext()){
			PageContextImpl pc=(PageContextImpl) it.next();
			if(!pc.isGatewayContext()) length++;
		}
		}
		catch(Throwable t){
			return length;
		}
	    return length;
	}
    /**
     * @return Returns the config.
     */
    public ConfigWeb getConfig() {
        return config;
    }
    public ConfigWebImpl getConfigWebImpl() {
        return config;
    }
    /**
     * @return Returns the scopeContext.
     */
    public ScopeContext getScopeContext() {
        return scopeContext;
    }

    /**
     * @return label of the factory
     */
    public Object getLabel() {
    	return ((ConfigWebImpl)getConfig()).getLabel();
    }
    /**
     * @param label
     */
    public void setLabel(String label) {
        // deprecated
    }

	/**
	 * @return the hostName
	 */
	public URL getURL() {
		return url;
	}
    

	public void setURL(URL url) {
		this.url=url;
	}

	/**
	 * @return the servlet
	 */
	public HttpServlet getServlet() {
		return servlet;
	}

	public void setConfig(ConfigWebImpl config) {
		this.config=config;
	}

	public Struct getRunningPageContexts() {
		return runningPcs;
	}
	
	// exists because it is used in Morpheus
	public Struct getRunningPageContextes() {
		return getRunningPageContexts();
	}

	public long getPageContextsSize() {
		return SizeOf.size(pcs);
	}
	
	public Array getInfo() {
		Array info=new ArrayImpl();
		
		synchronized (runningPcs) {
            //int len=runningPcs.size();
			Iterator<Key> it = runningPcs.keyIterator();
            PageContextImpl pc;
            Struct data,sctThread,scopes;
    		Collection.Key key;
            Thread thread;
    		while(it.hasNext()) {
            	data=new StructImpl();
            	sctThread=new StructImpl();
            	scopes=new StructImpl();
            	data.setEL("thread", sctThread);
                data.setEL("scopes", scopes);
                
            	
            	key=KeyImpl.toKey(it.next(),null);
                //print.out("key:"+key);
                pc=(PageContextImpl) runningPcs.get(key,null);
                if(pc==null || pc.isGatewayContext()) continue;
                thread=pc.getThread();
                if(thread==Thread.currentThread()) continue;

                
                thread=pc.getThread();
                if(thread==Thread.currentThread()) continue;
                
               
                
                data.setEL("startTime", new DateTimeImpl(pc.getStartTime(),false));
                data.setEL("endTime", new DateTimeImpl(pc.getStartTime()+pc.getRequestTimeout(),false));
                data.setEL(KeyConstants._timeout,new Double(pc.getRequestTimeout()));

                
                // thread
                sctThread.setEL(KeyConstants._name,thread.getName());
                sctThread.setEL("priority",Caster.toDouble(thread.getPriority()));
                data.setEL("TagContext",PageExceptionImpl.getTagContext(pc.getConfig(),thread.getStackTrace() ));

                data.setEL("urlToken", pc.getURLToken());
                try {
					if(pc.getConfig().debug())data.setEL("debugger", pc.getDebugger().getDebuggingData(pc));
				} catch (PageException e2) {}

                try {
					data.setEL("id", Hash.call(pc, pc.getId()+":"+pc.getStartTime()));
				} catch (PageException e1) {}
                data.setEL("requestid", pc.getId());

                // Scopes
                scopes.setEL(KeyConstants._name, pc.getApplicationContext().getName());
                try {
					scopes.setEL(KeyConstants._application, pc.applicationScope());
				} catch (PageException e) {}

                try {
					scopes.setEL(KeyConstants._session, pc.sessionScope());
				} catch (PageException e) {}
                
                try {
					scopes.setEL(KeyConstants._client, pc.clientScope());
				} catch (PageException e) {}
                scopes.setEL(KeyConstants._cookie, pc.cookieScope());
                scopes.setEL(KeyConstants._variables, pc.variablesScope());
                if(!(pc.localScope() instanceof LocalNotSupportedScope)){
                	scopes.setEL(KeyConstants._local, pc.localScope());
                	scopes.setEL(KeyConstants._arguments, pc.argumentsScope());
                }
                scopes.setEL(KeyConstants._cgi, pc.cgiScope());
                scopes.setEL(KeyConstants._form, pc.formScope());
                scopes.setEL(KeyConstants._url, pc.urlScope());
                scopes.setEL(KeyConstants._request, pc.requestScope());
                
                info.appendEL(data);
            }
            return info;
        }
	}

	public void stopThread(String threadId, String stopType) {
		synchronized (runningPcs) {
            //int len=runningPcs.size();
			Iterator it = runningPcs.keyIterator();
            PageContext pc;
    		while(it.hasNext()) {
            	
            	pc=(PageContext) runningPcs.get(KeyImpl.toKey(it.next(),null),null);
                if(pc==null) continue;
                try {
					String id = Hash.call(pc, pc.getId()+":"+pc.getStartTime());
					if(id.equals(threadId)){
						stopType=stopType.trim();
						Throwable t;
						if("abort".equalsIgnoreCase(stopType) || "cfabort".equalsIgnoreCase(stopType))
							t=new Abort(Abort.SCOPE_REQUEST);
						else
							t=new RequestTimeoutException(pc,"request has been forced to stop.");
						
		                pc.getThread().stop(t);
		                SystemUtil.sleep(10);
						break;
					}
				} catch (PageException e1) {}
                
            }
        }
	}

	@Override
	public QueryCache getDefaultQueryCache() {
		return queryCache;
	}
}