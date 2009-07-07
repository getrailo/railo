package railo.runtime.lock;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import railo.commons.collections.HashTable;


/**
 * Lock mnager to make a log by a string name
 */
public final class LockManagerImpl implements LockManager {

	private static List managers=new ArrayList();
    private Map locks=new HashTable();
	
    private LockManagerImpl() {
    	
    }
	
    public static LockManager getInstance() {
    	LockManagerImpl lmi = new LockManagerImpl();
    	managers.add(lmi);
    	return lmi;
    }
	
	/**
     * @see railo.runtime.lock.LockManager#lock(int, java.lang.String, int, int)
     */
	public LockData lock(int type, String name, int timeout, int pageContextId) throws LockTimeoutException, InterruptedException {
		//print.out("lock:"+timeout);
        
        LockData data = new LockDataImpl(type,name,pageContextId);
        LockToken token=touchLookToken(data);
        long start=System.currentTimeMillis();
        synchronized(token) {
        	//print.out("count:"+token.getDataCount()+":"+token.canEnter(data));

        	while(!token.canEnter(data)) { 
            	//print.out("wait:"+timeout);
                token.wait(timeout);
            	//print.out("count:"+timeout+"+"+(start+timeout<=System.currentTimeMillis()));
                if(timeout!=0 && start+timeout<=System.currentTimeMillis()) {
                    //SystemOut.printDate("lock ["+name+"] timeout after "+(System.currentTimeMillis()-start)+" millis");
    				throw new LockTimeoutException(type,name,timeout);
    			}
    		}
            token.addLockData(data);
            if(data.isReadOnly())token.notify();
        }
        return data;
	}

    /**
     * @see railo.runtime.lock.LockManager#unlock(railo.runtime.lock.LockManager.LockData)
     */
	public void unlock(LockData data) {
        if(data==null)return;
        LockToken token=(LockToken)locks.get(data.getName());
        
        if(token!=null) {
           if(token.removeLockData(data));
               //locks.remove(data.getName());
        }
	}
    
    /**
     * touch a LockToken, create if needed
     * @param data
     * @return token
     */
    private LockToken touchLookToken(LockData data) {
    	LockToken token=null;
    	synchronized(locks) {
	    	token=(LockToken)locks.get(data.getName());
	        if(token == null){
	            token=new LockToken();
	            locks.put(data.getName(),token);
	        }
    	}
        return token;
    }
    
    /**
     * a running lock token
     */
    private class LockToken {
        
        //private LockData data;
        //private int count;
        private Set datas=new HashSet();

        public int getDataCount() {
        	return datas.size();
        }
        
        /**
         * remove lock data
         * @param data 
         * @return has removed or not
         */
        private synchronized boolean removeLockData(LockData data) {
        	if(datas.remove(data))notify();
        	return datas.isEmpty();
        }

        /**
         * adds a data to token
         * @param data
         */
        private void addLockData(LockData data) {
        	//print.out("b:"+datas.size()+":"+this);
        	datas.add(data);
        	//print.out("a:"+datas.size());
        }

        /**
         * can the data entry in the lock with this token
         * @param data
         * @return can entry
         */
        private boolean canEnter(LockData data) {
        	LockData d=null;
        	synchronized(datas){ 
	        	Iterator it = datas.iterator();
	        	while(it.hasNext()) {
	        		d=(LockData) it.next();
	        		if(d!=data && d.getId()!=data.getId() && !data.isReadOnly()) return false;
	        	}
        	}
        	return true;
        }
        /*private boolean hasId(int id) {
        	LockData d=null;
        	synchronized(datas){ 
	        	Iterator it = datas.iterator();
	        	while(it.hasNext()) {
	        		d=(LockData) it.next();
	        		if(d.getId()==id) return true;
	        	}
        	}
        	return false;
        }*/
    }

	/**
	 *
	 * @see railo.runtime.lock.LockManager#getOpenLockNames()
	 */
	public String[] getOpenLockNames() {
	/*	return getOpenLockNames(-1);
	}
	
	private String[] getOpenLockNames(int pageContextId) {*/
		Iterator it = locks.entrySet().iterator();
		ArrayList rtn=new ArrayList();
		LockToken token;
		Map.Entry entry;
		while(it.hasNext()) {
			entry=(Entry) it.next();
			token=(LockToken) entry.getValue();
			
			if(token.getDataCount()>0) {
				rtn.add(entry.getKey());
			}
		}
		return (String[]) rtn.toArray(new String[rtn.size()]);
	}

	
	
	private LockData[] getLockDatas(int pageContextId) {
		Iterator it = locks.entrySet().iterator(),itt;
		ArrayList rtn=new ArrayList();
		LockToken token;
		LockData data;
		
		while(it.hasNext()) {
			token=(LockToken) ((Entry) it.next()).getValue();
			itt=token.datas.iterator();
			while(itt.hasNext()) {
				data=(LockData) itt.next();
				if(data.getId()==pageContextId)rtn.add(data);
			}
		}
		return (LockData[]) rtn.toArray(new LockData[rtn.size()]);
	}

	/**
	 *
	 * @see railo.runtime.lock.LockManager#unlock(int)
	 */
	public void unlock(int pageContextId) {
		LockData[] datas = getLockDatas(pageContextId);
		for(int i=0;i<datas.length;i++) {
			unlock(datas[i]);
		}
	}
	
	public void clear() {
		//locks.clear();
	}
	
	public static void unlockAll(int pageContextId) {
		Iterator it = managers.iterator();
		LockManagerImpl lmi;
		while(it.hasNext()) {
			lmi=(LockManagerImpl) it.next();
			lmi.unlock(pageContextId);
		}
	}
	
	
}