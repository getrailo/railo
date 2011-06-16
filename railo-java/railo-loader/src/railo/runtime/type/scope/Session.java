package railo.runtime.type.scope;


/**
 * 
 */
public interface Session extends Scope,SharedScope {
    /**
     * @return returns the last acces to this session scope
     * @deprecated 
     */
    public abstract long getLastAccess();

    /**
     * @return returns the actuell timespan of the session
     * @deprecated 
     */
    public abstract long getTimeSpan();


    /**
     * @return is the scope expired or not
     */
    public abstract boolean isExpired();

	/**
	 * sets the last access timestamp to now
	 */
	public abstract void touch();
	
	public int _getId();
}