package railo.runtime.type.scope;

import railo.runtime.PageContext;


/**
 * Session Scope
 */
public final class CFSession extends ScopeSupport implements Session {

	private long lastAccess=-1;
	private long timeSpan=-1;
	
	/**
	 * default constructor of the session scope
	 */
	public CFSession() {
		super(true,"session",SCOPE_SESSION);
        setDisplayName("Scope Session (Type CFML)");
	}
	/**
	 * @see railo.runtime.type.Scope#initialize(railo.runtime.PageContext)
	 */
	public void initialize(PageContext pc) {
		super.initialize(pc);
	    
	    this.timeSpan=pc.getApplicationContext().getSessionTimeout().getMillis();
		setEL(ClientSupport.CFID,pc.getCFID());
		setEL(ClientSupport.CFTOKEN,pc.getCFToken());
		setEL(ClientSupport.URLTOKEN,pc.getURLToken());
		setEL(JSession.SESSION_ID,"_".concat(pc.getCFID()).concat("_").concat(pc.getCFToken()));
		lastAccess=System.currentTimeMillis();
		//print.ln("initialize(timeSpan:"+timeSpan+";)");
	}
	
    /**
     * @see railo.runtime.type.Scope#release()
     */
    public void release() {
        super.release();
		//print.ln("release");
        timeSpan=-1;
        clear();
    }

	/**
	 * @return returns the last acces to this session scope
	 */
	public long getLastAccess() { return lastAccess;}

	/**
	 * @return returns the actuell timespan of the session
	 */
	public long getTimeSpan() { return timeSpan;}


    /**
     * @see railo.runtime.type.scope.Session#isExpired()
     */
    public boolean isExpired() {
        //print.ln("isExpired("+_isExpired()+")");
        return _isExpired();
    }
    private boolean _isExpired() {
	    return (getLastAccess()+getTimeSpan())<System.currentTimeMillis();
    }

	/**
	 *
	 * @see railo.runtime.type.scope.Session#touch()
	 */
	public void touch() {
		lastAccess=System.currentTimeMillis();
	}
}