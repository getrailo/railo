package railo.runtime.type.scope;

import java.util.Map;

import railo.runtime.PageContext;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.functions.system.GetApplicationSettings;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.util.ApplicationContext;



/**
 * Session Scope
 */
public final class ApplicationImpl extends ScopeSupport implements Application {

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
	public void initialize(PageContext pc){
	    ApplicationContext appContext = pc.getApplicationContext();
	    timeSpan=appContext.getApplicationTimeout().getMillis();
	    setEL(APPLICATION_NAME,appContext.getName());
		lastAccess=System.currentTimeMillis();
		super.initialize(pc);
	}
	
    /**
     * @see railo.runtime.type.Scope#release()
     */
    public void release() {
        super.release();
        clear();
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