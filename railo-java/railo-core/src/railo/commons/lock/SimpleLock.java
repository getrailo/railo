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

	
	@Override
	public void lock(long timeout) throws LockException {
		if(timeout<=0) throw new LockException("timeout must be a postive number");
		long initialTimeout=timeout;
		long start=System.currentTimeMillis();
		do{
			try {
				if(!lock.tryLock(timeout, TimeUnit.MILLISECONDS)){
					throw new LockException(initialTimeout);
				}
				break; // exit loop
			}
			catch (InterruptedException e) {
				timeout-=System.currentTimeMillis()-start;
			}
			if(timeout<=0) {
				// Railo was not able to aquire lock in time
				throw new LockException(initialTimeout);
			}
		}
		while(true);
		
	}

	@Override
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
