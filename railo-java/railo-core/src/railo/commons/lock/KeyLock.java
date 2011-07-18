package railo.commons.lock;

import java.util.HashMap;
import java.util.Map;

public class KeyLock<K> {

	private Map<K,SimpleLock<K>> locks=new HashMap<K, SimpleLock<K>>();
	
	
	public Lock lock(K key, long timeout) throws LockException, LockInterruptedException {
		if(timeout<=0) throw new LockException("timeout must be a postive number");
		SimpleLock<K> lock;
		synchronized (locks) {
			lock=locks.get(key);
			if(lock==null) locks.put(key, lock=new SimpleLock<K>(key));
			lock.incWaiters();
		}
		lock.lock(timeout,false);
		return lock;
	}

	public void unlock(Lock lock) {
		synchronized (locks) {
			if(lock.waiters()==0){
				locks.remove(((SimpleLock<K>)lock).getLabel());
			}
		}
		lock.unlock();
		
		/*Wrap wrap = locks.get(key);
		if(wrap==null) return;
		wrap.dec();
		wrap.lock.unlock();
		synchronized (locks) {
			wrap = locks.get(key);
			synchronized (wrap) {
				if(wrap!=null && wrap.count()==0)
					locks.remove(key);
			}
		}*/
	}
}

