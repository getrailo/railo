package railo.runtime.lock;

import railo.commons.lock.Lock;


/**
 * all data of a lock
 */
final class ExklLockData extends ReadLockData {


    private Lock lock;

	/**
     * constructor of the class
     * @param token 
     * @param type type of the token (TYPE_READONLY,TYPE_EXCLUSIVE)
     * @param name name of the token
     * @param id id of the token
     */
    protected ExklLockData(Lock lock, String name, int id) {
    	super(name, id);
    	this.lock=lock;
    }

	/**
	 * @return the lock
	 */
	public Lock getLock() {
		return lock;
	}

	/**
     * @see railo.runtime.lock.LockData#isReadOnly()
     */
    public boolean isReadOnly() {
        return false;
    }
}
    