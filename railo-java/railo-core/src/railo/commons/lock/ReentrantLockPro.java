package railo.commons.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockPro {
	
	private long timeout;
	private ReentrantLock lock;
	private int lockCount=0;

	public ReentrantLockPro(long timeout) throws LockException{
		this.timeout=timeout;
		this.lock=new ReentrantLock(true);
		if(timeout<=0) throw new LockException("timeout must be a postive number");
	}

	public void lock() throws LockException, LockInterruptedException {
		lock(timeout);
	}
	
	
	public void lock(long timeout) throws LockException, LockInterruptedException {
		if(timeout<=0) throw new LockException("timeout must be a postive number");
		lockCount++;
		if(timeout<=0) {
			lock.lock();
		} 
		else {
			try {
				if(!lock.tryLock(timeout, TimeUnit.MILLISECONDS)){
					lockCount--;
					throw new LockException(timeout);
				}
			} 
			catch (InterruptedException e) {
				lockCount--;
				throw new LockInterruptedException(e);
			}
		}
	}


	public void unlock()	{
		lockCount--;
		lock.unlock();
	}
	public int getCount()	{
		return lockCount;
	}
	public int getQueueLength()	{
		return lock.getQueueLength();
	}
}
