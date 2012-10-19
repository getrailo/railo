package railo.runtime.type.scope;


/**
 * implemetnation of the application scope
 */
public interface Application extends Scope {

    /**
     * @return returns the last access timestamp of this Application scope
     */
    public abstract long getLastAccess();

    /**
     * @return returns the actual timespan of the application
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

    /**
     * @return Timestamp of when the application scope was created
     */
    public long getCreated();

}