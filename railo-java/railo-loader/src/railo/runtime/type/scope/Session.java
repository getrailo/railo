package railo.runtime.type.scope;

import railo.runtime.type.Collection;


/**
 * 
 */
public interface Session extends Scope,UserScope {
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
    

	public long getCreated();


    /**
     * @return is the scope expired or not
     */
    public abstract boolean isExpired();

	/**
	 * sets the last access timestamp to now
	 */
	public abstract void touch();
	
	public int _getId();

	/**
	 * @return all keys except the readpnly ones (cfid,cftoken,hitcount,lastvisit ...)
	 */
	public abstract Collection.Key[] pureKeys();

}