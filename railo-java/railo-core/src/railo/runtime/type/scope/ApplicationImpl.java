package railo.runtime.type.scope;

import java.util.Map;

import railo.runtime.PageContext;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.functions.system.GetApplicationSettings;
import railo.runtime.listener.ApplicationContext;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.SharedScope;



/**
 * Session Scope
 */
public final class ApplicationImpl extends ScopeSupport implements Application,SharedScope {

	private static final long serialVersionUID = 700830188207594563L;
	
	private static final Collection.Key APPLICATION_NAME = KeyImpl.getInstance("applicationname");
	private long lastAccess;
	private long timeSpan;
	
	/**
	 * default constructor of the session scope
	 */
	public ApplicationImpl() {
		super(true,"application",SCOPE_APPLICATION);
	}

	/**
     * @see railo.runtime.type.scope.Application#getLastAccess()
     */
	public long getLastAccess() { 
		return lastAccess;
	}

	/**
     * @see railo.runtime.type.scope.Application#getTimeSpan()
     */
	public long getTimeSpan() { 
	    return timeSpan;
	}

	/**
	 * @see railo.runtime.type.Scope#initialize(railo.runtime.PageContext)
	 */
	public void touchBeforeRequest(PageContext pc){
	    ApplicationContext appContext = pc.getApplicationContext();
	    setEL(APPLICATION_NAME,appContext.getName());
	    timeSpan=appContext.getApplicationTimeout().getMillis();
		lastAccess=System.currentTimeMillis();
	}

	public void touchAfterRequest(PageContext pc) {
		// do nothing
	}

    /**
     * @see railo.runtime.type.scope.Application#isExpired()
     */
    public boolean isExpired() {
        return (lastAccess+timeSpan)<System.currentTimeMillis();
    }

	/**
	 * @param lastAccess the lastAccess to set
	 */
	public void setLastAccess(long lastAccess) {
		this.lastAccess = lastAccess;
	}

	/**
	 *
	 * @see railo.runtime.type.scope.Application#touch()
	 */
	public void touch() {
		lastAccess=System.currentTimeMillis();
	}
	
	/**
	 * undocumented Feature in ACF
	 * @return
	 */
	public Map getApplicationSettings(){
		return GetApplicationSettings.call(ThreadLocalPageContext.get());
	}
}