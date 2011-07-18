package railo.commons.lock;


public interface Lock {
	public void lock(long timeout) throws LockException, LockInterruptedException;

	public void unlock();
	
	public int waiters();
}
