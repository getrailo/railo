package railo.commons.lock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
		}
		lock.lock(timeout);
		return lock;
	}

	public void unlock(Lock lock) {
		if(lock==null) return;
		
		synchronized (locks) {
			if(lock.getQueueLength()==0){
				locks.remove(((SimpleLock<Token<K>>)lock).getLabel());
			}
		}
		lock.unlock();
	}
	
	public List<K> getOpenLockNames() {
		Iterator<Entry<Token<K>, SimpleLock<Token<K>>>> it = locks.entrySet().iterator();
		Entry<Token<K>, SimpleLock<Token<K>>> entry;
		List<K> list=new ArrayList<K>();
		while(it.hasNext()){
			entry = it.next();
			if(entry.getValue().getQueueLength()>0)
				list.add(entry.getKey().getKey());
		}
		return list;
	}

	public void clean() {
		Iterator<Entry<Token<K>, SimpleLock<Token<K>>>> it = locks.entrySet().iterator();
		Entry<Token<K>, SimpleLock<Token<K>>> entry;
		
		while(it.hasNext()){
			entry = it.next();
			if(entry.getValue().getQueueLength()==0){
				synchronized (locks) {
					if(entry.getValue().getQueueLength()==0){
						locks.remove(entry.getKey());
					}
				}
			}
		}
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
	public K getKey() {
		return key;
	}

	@Override
	public String toString(){
		return key.toString();
	}
	
	@Override
	public boolean equals(Object obj){
		if(obj instanceof Token<?>) {
			Token<?> other=(Token<?>) obj;
			obj=other.key;
		}
		return key.equals(obj);
	}
	
	@Override
	public int hashCode(){
		return key.hashCode();
	}
	
	
}


