package railo.commons.lock.rw;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import railo.commons.lock.Lock;
import railo.commons.lock.LockException;
import railo.commons.lock.LockInterruptedException;
import railo.runtime.exp.PageRuntimeException;
import railo.runtime.op.Caster;

public class RWKeyLock<K> {

	private Map<K,RWLock<K>> locks=new HashMap<K,RWLock<K>>();
	
	public Lock lock(K token, long timeout, boolean readOnly) throws LockException, LockInterruptedException {
		if(timeout<=0) throw new LockException("timeout must be a postive number");
		
		RWWrap<K> wrap;
		//K token=key;
		synchronized (locks) {
			RWLock<K> lock;
			lock=locks.get(token);
			if(lock==null) {
				locks.put(token, lock=new RWLock<K>(token));
			}
			lock.inc();
			wrap= new RWWrap<K>(lock, readOnly);
		}
		try{
			wrap.lock(timeout);
		}
		catch(LockException e){
			synchronized (locks) {wrap.getLock().dec();}
			throw e;
		}
		catch(LockInterruptedException e){
			synchronized (locks) {wrap.getLock().dec();}
			throw e;
		}
		catch(Throwable t){
			synchronized (locks) {wrap.getLock().dec();}
			throw new PageRuntimeException(Caster.toPageException(t));
		}
		return wrap;
	}

	public void unlock(Lock lock) {
		if(!(lock instanceof RWWrap)) {
			return;
		}
		
		lock.unlock();
		
		synchronized (locks) {
			((RWWrap)lock).getLock().dec();
			if(lock.getQueueLength()==0){
				locks.remove(((RWWrap)lock).getLabel());
			}
		}
	}
	
	public List<K> getOpenLockNames() {
		Iterator<Entry<K, RWLock<K>>> it = locks.entrySet().iterator();
		Entry<K, RWLock<K>> entry;
		List<K> list=new ArrayList<K>();
		while(it.hasNext()){
			entry = it.next();
			if(entry.getValue().getQueueLength()>0)
				list.add(entry.getKey());
		}
		return list;
	}
	
	/**
	 * Queries if the write lock is held by any thread on given lock token, returns null when lock with this token does not exists
     * @param token name of the lock to check
	 * @return
	 */
	public Boolean isWriteLocked(K token) {
		RWLock<K> lock = locks.get(token);
		if(lock==null) return null;
		return lock.isWriteLocked();
	}

	/**
	 * Queries if one or more read lock is held by any thread on given lock token, returns null when lock with this token does not exists
     * @param token name of the lock to check
	 * @return
	 */
	public Boolean isReadLocked(K token) {
		RWLock<K> lock = locks.get(token);
		if(lock==null) return null;
		return lock.isReadLocked();
	}

	public void clean() {
		Iterator<Entry<K, RWLock<K>>> it = locks.entrySet().iterator();
		Entry<K, RWLock<K>> entry;
		
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

class RWWrap<L> implements Lock {
	
	private RWLock<L> lock;
	private boolean readOnly;

	public RWWrap(RWLock<L> lock, boolean readOnly){
		this.lock=lock;
		this.readOnly=readOnly;
	}

	public void lock(long timeout) throws LockException, LockInterruptedException {
		lock.lock(timeout, readOnly);
	}

	public void unlock() {
		lock.unlock(readOnly);
	}
	public int getQueueLength() {
		return lock.getQueueLength();
	}

	public L getLabel(){
		return lock.getLabel();
	}

	public RWLock<L> getLock(){
		return lock;
	}
	public boolean isReadOnly(){
		return readOnly;
	}
	
}