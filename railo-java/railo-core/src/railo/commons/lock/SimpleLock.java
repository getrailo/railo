package railo.commons.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class SimpleLock<L> implements Lock {
	
	private ReentrantLock lock;
	private L label;
	
	public SimpleLock(L label) {
		this.lock=new ReentrantLock(true);
		this.label=label;
	}

	
	public void lock(long timeout) throws LockException, LockInterruptedException {
		if(timeout<=0) throw new LockException("timeout must be a postive number");
		
		try {
			if(!lock.tryLock(timeout, TimeUnit.MILLISECONDS)){
				throw new LockException(timeout);
			}
		} 
		catch (InterruptedException e) {
			throw new LockInterruptedException(e);
		}
		
	}


	public void unlock()	{
		lock.unlock();
	}
	
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
    public int getQueueLength()	{
		return lock.getQueueLength();
	}
    

	public L getLabel(){
		return label;
	}
}
