package railo.runtime.type.scope;


/**
 * implemetnation of the application scope
 */
public interface Application extends Scope {

    /**
     * @return returns the last acces to this session scope
     */
    public abstract long getLastAccess();

    /**
     * @return returns the actuell timespan of the application
     */
    public abstract long getTimeSpan();

    /**
     * @return is expired
     */
    public abstract boolean isExpired();

	/**
	 * sets the last access timestamp to now
	 */
	public abstract void touch();

}