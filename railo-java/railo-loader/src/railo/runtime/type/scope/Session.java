package railo.runtime.type.scope;

import railo.runtime.type.Scope;

/**
 * 
 */
public interface Session extends Scope {
    /**
     * @return returns the last acces to this session scope
     */
    public abstract long getLastAccess();

    /**
     * @return returns the actuell timespan of the session
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
}