package railo.runtime.lock;

import java.util.ArrayList;
import java.util.List;

import railo.commons.lock.KeyLock;
import railo.commons.lock.Lock;
import railo.commons.lock.LockException;
import railo.commons.lock.LockInterruptedException;

/**
 * Lock mnager to make a log by a string name
 */
public final class LockManagerImpl implements LockManager {

	private static List<LockManagerImpl> managers=new ArrayList<LockManagerImpl>();
    private KeyLock<String> locks=new KeyLock<String>();
	private boolean caseSensitive;
	
    private LockManagerImpl(boolean caseSensitive) {
    	this.caseSensitive=caseSensitive;
    }
	
    public static LockManager getInstance(boolean caseSensitive) {
    	LockManagerImpl lmi = new LockManagerImpl(caseSensitive);
    	managers.add(lmi);
    	return lmi;
    }
	
	/**
     * @see railo.runtime.lock.LockManager#lock(int, java.lang.String, int, int)
     */
	public LockData lock(int type, String name, int timeout, int pageContextId) throws LockTimeoutException, InterruptedException {
		if(!caseSensitive)name=name.toLowerCase();
		//if(type==LockManager.TYPE_READONLY) return new ReadLockData(name,pageContextId);
		if(timeout<=0)timeout=1;
		Lock lock;
		try {
			lock=locks.lock(name,timeout);
		} catch (LockException e) {
			throw new LockTimeoutException(type,name,timeout);
		} 
		catch (LockInterruptedException e) {
			throw e.getLockInterruptedException();
		}
		if(type==LockManager.TYPE_READONLY) {
			unlock(new ExklLockData(lock,name,pageContextId));
			return new ReadLockData(name,pageContextId);
		}
		return new ExklLockData(lock,name,pageContextId);
	}
	
	public void unlock(LockData data) {
		if(data.isReadOnly()) return;
		Lock l = ((ExklLockData)data).getLock();
		locks.unlock(l);
		//locks.unlock(data.getName());
	}


	/**
	 *
	 * @see railo.runtime.lock.LockManager#unlock(int)
	 */
	public void unlock(int pageContextId) {
		throw new RuntimeException("no longer supported");//FUTURE remove from interface
	}
	
    
	/**
	 *
	 * @see railo.runtime.lock.LockManager#getOpenLockNames()
	 */
	public String[] getOpenLockNames() {
		List<String> list = locks.getOpenLockNames();
		return list.toArray(new String[list.size()]);
		//throw new RuntimeException("no longer supported");//FUTURE remove from interface
	}

	public void clean() {
		locks.clean();
	}
	
	
}