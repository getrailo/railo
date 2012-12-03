package railo.runtime.lock;

import railo.commons.lock.Lock;


/**
 * lock data
 */
public interface LockData {
    /**
     * is type of token read only
     * @return is read only
     * @deprecated 
     */
    boolean isReadOnly();

    /**
     * @return Returns the id.
     */
    int getId();

    /**
     * @return Returns the name.
     */
    String getName();
    
    public Lock getLock();
}