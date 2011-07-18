package railo.commons.lock;


import railo.commons.io.SystemUtil;
import railo.commons.lang.SerializableObject;
import railo.commons.lang.types.RefInteger;
import railo.commons.lang.types.RefIntegerSync;

public class SimpleLock<K> implements Lock {
	
	private SerializableObject token;
	private RefInteger waiters=new RefIntegerSync(0);
	//private RefInteger runners=new RefIntegerSync(0);
	private LockException stopException;
	private K label;
	
	public SimpleLock(K label) {
		this.label=label;
	}
	

	public void lock(long timeout) throws LockException, LockInterruptedException {
		lock(timeout, true);
	}

	public void lock(long timeout, boolean doWaitersPlus) throws LockException, LockInterruptedException {
		
		long start=System.currentTimeMillis();

		if(doWaitersPlus)waiters.plus(1);
		SerializableObject t=null;
		do {
			synchronized (this) {
				if(token==null) {
					token=new SerializableObject();
					waiters.minus(1);
					//runners.plus(1);
					return;
				}
				t=token;
			}
			
			try {
				synchronized (t) {
					if(t!=token) continue;// handle if a unlock happen after the "synchronized (this)" above
					token.wait(timeout);
				}
			} 
			catch (InterruptedException e) {
				waiters.minus(1);
				throw new LockInterruptedException(e);
			}
			
			
			synchronized (this) {
				if(stopException!=null) {
					throw stopException;
				}
				else if((System.currentTimeMillis()-start)>timeout) {
					waiters.minus(1);
					throw new LockException(timeout);
				}
			}
		}
		while(true);
	}
	
	public synchronized void unlock() {
		//runners.minus(1);
		SerializableObject t = token;
		token=null;
		SystemUtil.notifyAll(t);
	}

	public void incWaiters() {
		waiters.plus(1);
	}
	public int waiters() {
		return waiters.toInt();
	}
	
	/*public synchronized int runners() {
		return runners.toInt();
	}*/
	
	public K getLabel(){
		return label;
	}
	

	public void terminateAll() {
		terminateAll(new LockException("lock terminated"));
	}
	
	public synchronized void terminateAll(LockException stopException) {
		this.stopException=stopException;
		//waiters.setValue(0);
		
		if(token!=null)SystemUtil.notifyAll(token);
	}
}
