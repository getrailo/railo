package railo.runtime.lock;

import java.util.ArrayList;
import java.util.List;

import railo.commons.lock.Lock;
import railo.commons.lock.LockException;
import railo.commons.lock.LockInterruptedException;
import railo.commons.lock.rw.RWKeyLock;

/**
 * Lock mnager to make a log by a string name
 */
public final class LockManagerImpl implements LockManager {

	private static List<LockManagerImpl> managers=new ArrayList<LockManagerImpl>();
    private RWKeyLock<String> locks=new RWKeyLock<String>();
	private boolean caseSensitive;
	
    private LockManagerImpl(boolean caseSensitive) {
    	this.caseSensitive=caseSensitive;
    }
	
    public static LockManager getInstance(boolean caseSensitive) {
    	LockManagerImpl lmi = new LockManagerImpl(caseSensitive);
    	managers.add(lmi);
    	return lmi;
    }
	
	@Override
	public LockData lock(int type, String name, int timeout, int pageContextId) throws LockTimeoutException, InterruptedException {
		if(!caseSensitive)name=name.toLowerCase();
		//if(type==LockManager.TYPE_READONLY) return new ReadLockData(name,pageContextId);
		if(timeout<=0)timeout=1;
		Lock lock;
		try {
			lock=locks.lock(name,timeout,type==LockManager.TYPE_READONLY);
		} catch (LockException e) {
			throw new LockTimeoutException(type,name,timeout);
		} 
		catch (LockInterruptedException e) {
			throw e.getLockInterruptedException();
		}
		
		return new LockDataImpl(lock,name,pageContextId,type==LockManager.TYPE_READONLY);
	}
	
	public void unlock(LockData data) {
		Lock l = data.getLock();
		locks.unlock(l);
	}
    
	@Override
	public String[] getOpenLockNames() {
		List<String> list = locks.getOpenLockNames();
		return list.toArray(new String[list.size()]);
	}

	@Override
	public void clean() {
		locks.clean();
	}


	public Boolean isReadLocked(String name) {
		if(!caseSensitive)name=name.toLowerCase();
		return locks.isReadLocked(name);
	}
	
	public Boolean isWriteLocked(String name) {
		if(!caseSensitive)name=name.toLowerCase();
		return locks.isWriteLocked(name);
	}
	
	
}