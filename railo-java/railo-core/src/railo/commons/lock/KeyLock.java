package railo.commons.lock;

import java.util.HashMap;
import java.util.Map;

public class KeyLock<K> {

	private Map<Token<K>,SimpleLock<Token<K>>> locks=new HashMap<Token<K>, SimpleLock<Token<K>>>();
	
	
	public Lock lock(K key, long timeout) throws LockException, LockInterruptedException {
		if(timeout<=0) throw new LockException("timeout must be a postive number");
		
		SimpleLock<Token<K>> lock;
		Token<K> token=new Token<K>(key);
		synchronized (locks) {
			lock=locks.get(token);
			if(lock==null) {
				locks.put(token, lock=new SimpleLock<Token<K>>(token));
			}
			// ignore inner calls with same id
			else if(lock.getLabel().getThreadId()==token.getThreadId()) {
				return null;
			}
			lock.incWaiters();
		}
		lock.lock(timeout,false);
		return lock;
	}

	public void unlock(Lock lock) {
		if(lock==null) {
			return;
		}
		synchronized (locks) {
			if(lock.waiters()==0){
				locks.remove(((SimpleLock<Token<K>>)lock).getLabel());
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

class Token<K> {

	private K key;
	private long threadid;

	/**
	 * @param key
	 */
	public Token(K key) {
		this.key=key;
		this.threadid=Thread.currentThread().getId();
	}

	/**
	 * @return the id
	 */
	public long getThreadId() {
		return threadid;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return key.toString();
	}
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj){
		if(obj instanceof Token<?>) {
			Token<?> other=(Token<?>) obj;
			obj=other.key;
		}
		return key.equals(obj);
	}
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode(){
		return key.hashCode();
	}
	
	
}


