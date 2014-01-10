package railo.runtime.type.scope;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import railo.runtime.PageContext;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.listener.ApplicationContext;
import railo.runtime.type.Collection;
import railo.runtime.type.scope.storage.MemoryScope;
import railo.runtime.type.util.KeyConstants;

/**
 * 
 */
public final class JSession extends ScopeSupport implements Session,HttpSessionBindingListener,MemoryScope {
    
	public static final Collection.Key SESSION_ID = KeyConstants._sessionid;
	private static Set<Collection.Key> FIX_KEYS=new HashSet<Collection.Key>();
	static {
		FIX_KEYS.add(KeyConstants._sessionid);
		FIX_KEYS.add(KeyConstants._urltoken);
	}

	
	private String name;
    private long timespan=-1;
    private HttpSession httpSession;
    private long lastAccess;
	private long created;

    /**
     * constructor of the class
     */
    public JSession() {
        super(true,"session",SCOPE_SESSION);
        setDisplayName("Scope Session (Type J2ee)");
        this.created=System.currentTimeMillis();
    }

    @Override
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
        setEL(KeyConstants._sessionid,id);
        setEL(KeyConstants._urltoken,"CFID="+pc.getCFID()+"&CFTOKEN="+pc.getCFToken()+"&jsessionid="+id);
	}

	public void touchAfterRequest(PageContext pc) {
		
	}
	@Override
	public void release() {
		release(ThreadLocalPageContext.get());
	}
	
	@Override
	public void release(PageContext pc) {
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
        super.release(pc);
    }

    @Override
    public long getLastAccess() {
        return lastAccess;
    }

    @Override
    public long getTimeSpan() {
        return timespan;
    }

    @Override
    public boolean isExpired() {
        return (getLastAccess()+getTimeSpan())<System.currentTimeMillis();
    }

    @Override
    public void valueBound(HttpSessionBindingEvent event) {
        
    }

    @Override
    public void valueUnbound(HttpSessionBindingEvent event) {
        clear();
    }
	
    @Override
    public void touch() {
		lastAccess=System.currentTimeMillis();
	}

	@Override
	public long getCreated() {
		return created;
	}
	
	public Collection.Key[] pureKeys() {
		List<Collection.Key> keys=new ArrayList<Collection.Key>();
		Iterator<Key> it = keyIterator();
		Collection.Key key;
		while(it.hasNext()){
			key=it.next();
			if(!FIX_KEYS.contains(key))keys.add(key);
		}
		return keys.toArray(new Collection.Key[keys.size()]);
	}

	@Override
	public void resetEnv(PageContext pc) {
		created=System.currentTimeMillis();
		lastAccess=System.currentTimeMillis();
		touchBeforeRequest(pc);
	}
}