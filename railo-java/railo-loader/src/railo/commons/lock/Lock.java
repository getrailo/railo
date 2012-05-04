package railo.commons.lock;

import railo.runtime.exp.PageException;


public interface Lock {
	
	public void lock(long timeout) throws PageException;

	public void unlock();
	
	/**
     * Returns an estimate of the number of threads waiting to
     * acquire this lock.  The value is only an estimate because the number of
     * threads may change dynamically while this method traverses
     * internal data structures.  This method is designed for use in
     * monitoring of the system state, not for synchronization
     * control.
     *
     * @return the estimated number of threads waiting for this lock
     */
    public int getQueueLength();
}
