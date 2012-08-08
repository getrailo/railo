package railo.runtime.lock;

import railo.commons.lock.Lock;

class LockDataImpl implements LockData {

	private final Lock lock;
    private final String name;
    private final int id;
	private final boolean readOnly;

    /**
     * constructor of the class
     * @param token 
     * @param name name of the token
     * @param id id of the token
     * @param readOnly 
     */
    protected LockDataImpl(Lock lock,String name, int id, boolean readOnly) {
    	this.lock=lock;
    	this.name=name.toLowerCase();
        this.id=id;
        this.readOnly=readOnly;
    }

	/**
     * @see railo.runtime.lock.LockData#isReadOnly()
     */
    public boolean isReadOnly() {
        return readOnly;
    }

    /**
     * @see railo.runtime.lock.LockData#getId()
     */
    public int getId() {
        return id;
    }

    /**
     * @see railo.runtime.lock.LockData#getName()
     */
    public String getName() {
        return name;
    }
    
	 /**
	 * @return the lock
	 */
	public Lock getLock() {
		return lock;
	}
}
