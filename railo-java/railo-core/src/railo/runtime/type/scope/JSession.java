package railo.runtime.type.scope;

import java.util.Enumeration;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import railo.runtime.PageContext;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.scope.storage.MemoryScope;
import railo.runtime.type.scope.storage.StorageScope;
import railo.runtime.util.ApplicationContext;

/**
 * 
 */
public final class JSession extends ScopeSupport implements SessionPlus,HttpSessionBindingListener,MemoryScope {
    
	//public static final Collection.Key URL_TOKEN = KeyImpl.intern("urltoken");
	public static final Collection.Key SESSION_ID = KeyImpl.intern("sessionid");
	
	private String name;
    private long timespan=-1;
    private HttpSession httpSession;
    private long lastAccess;

    /**
     * constructor of the class
     */
    public JSession() {
        super(true,"session",SCOPE_SESSION);
        setDisplayName("Scope Session (Type J2ee)");
    }

    /**
     * @see railo.runtime.type.scope.ScopeSupport#initialize(railo.runtime.PageContext)
	 */
	public void touchBeforeRequest(PageContext pc) {
		
	    ApplicationContext appContext = pc.getApplicationContext();
	    timespan=appContext.getSessionTimeout().getMillis();
	    this.name=appContext.getName();
	    HttpSession hs = pc.getSession();
	    String id="";
	    try{
		    if(hs!=null)this.httpSession=hs;
		    if(httpSession!=null) {
			    id = httpSession.getId();
			    if(httpSession.getMaxInactiveInterval()<(timespan/1000))
			    	httpSession.setMaxInactiveInterval((int)(timespan/1000));
		    }
		    
		}
	    catch(Throwable t) {
	    	
	    }
         

	    lastAccess=System.currentTimeMillis();
        setEL(SESSION_ID,id);
        setEL(StorageScope.URLTOKEN,"CFID="+pc.getCFID()+"&CFTOKEN="+pc.getCFToken()+"&jsessionid="+id);
	}

	public void touchAfterRequest(PageContext pc) {
		
	}
	
    public void release() {
    	if(httpSession!=null){
	    	try {
	    		Object key;
	    		Enumeration e = httpSession.getAttributeNames();
	    		while(e.hasMoreElements()) {
	    			// TODO set inative time new
	    			key=e.nextElement();
	    			if(key.equals(name))httpSession.removeAttribute(name);
	    		}
	    		name=null;
	    		timespan=-1;
	    		httpSession=null;
	    		lastAccess=-1;
	    	}
	    	catch(Throwable t) {}
    	}
        super.release();
    }

    /**
     * @see railo.runtime.type.scope.Session#getLastAccess()
     */
    public long getLastAccess() {
        return lastAccess;
    }

    /**
     * @see railo.runtime.type.scope.Session#getTimeSpan()
     */
    public long getTimeSpan() {
        return timespan;
    }

    /**
     * @see railo.runtime.type.scope.Session#isExpired()
     */
    public boolean isExpired() {
        return (getLastAccess()+getTimeSpan())<System.currentTimeMillis();
    }

    /**
     * @see javax.servlet.http.HttpSessionBindingListener#valueBound(javax.servlet.http.HttpSessionBindingEvent)
     */
    public void valueBound(HttpSessionBindingEvent event) {
        
    }

    /**
     * @see javax.servlet.http.HttpSessionBindingListener#valueUnbound(javax.servlet.http.HttpSessionBindingEvent)
     */
    public void valueUnbound(HttpSessionBindingEvent event) {
        clear();
    }
	
    /**
     *
     * @see railo.runtime.type.scope.Session#touch()
     */
    public void touch() {
		lastAccess=System.currentTimeMillis();
	}

}