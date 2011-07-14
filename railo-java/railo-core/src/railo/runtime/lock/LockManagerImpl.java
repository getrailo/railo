package railo.runtime.lock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


/**
 * Lock mnager to make a log by a string name
 */
public final class LockManagerImpl implements LockManager {

	private static List<LockManagerImpl> managers=new ArrayList<LockManagerImpl>();
    private Map<String,LockToken> locks=new HashMap<String,LockToken>();
	
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
		LockData data = new LockDataImpl(type,name,pageContextId);
        LockToken token=touchLookToken(data);
        long start=System.currentTimeMillis();
        if(timeout<=0)timeout=1;
        synchronized(token) {
        	//print.out("count:"+token.getDataCount()+":"+token.canEnter(data));
        	
        	if(!token.canEnter(data)) { 
            	token.waiters++;
                token.wait(timeout);
            	//print.out("count:"+timeout+"+"+(start+timeout<=System.currentTimeMillis()));
                if(timeout!=0 && start+timeout<=System.currentTimeMillis()) {
                    //SystemOut.printDate("lock ["+name+"] timeout after "+(System.currentTimeMillis()-start)+" millis");
    				throw new LockTimeoutException(type,name,timeout);
    			}
    		}
        	if(!token.canEnter(data)) { 
            	throw new LockTimeoutException(type,name,timeout);
    		}
        	token.addLockData(data);
        	token.waiters--;
            if(data.isReadOnly())token.notify();
        }
        return data;
	}

    /**
     * @see railo.runtime.lock.LockManager#unlock(railo.runtime.lock.LockManager.LockData)
     */
	public void unlock(LockData data) {
        if(data==null)return;
        synchronized (locks) {
	        LockToken token=(LockToken)locks.get(data.getName());
	        if(token!=null) {
	        	if(token.removeLockData(data))
	        		locks.remove(data.getName());
	        }
        }
	}
    
    /**
     * touch a LockToken, create if needed
     * @param data
     * @return token
     */
    private LockToken touchLookToken(LockData data) {
    	synchronized(locks) {
    		LockToken token=locks.get(data.getName());
	        if(token == null){
	            token=new LockToken();
	            locks.put(data.getName(),token);
	        }
	        return token;
    	}
    }
    
    /**
     * a running lock token
     */
    private class LockToken {
        
    	private int waiters=0;
    	
    	
        //private LockData data;
        //private int count;
        private Set<LockData> datas=new HashSet<LockData>();

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
        	return datas.isEmpty() && waiters==0;
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
	        	Iterator<LockData> it = datas.iterator();
	        	while(it.hasNext()) {
	        		d=it.next();
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
		Iterator<Entry<String, LockToken>> it = locks.entrySet().iterator();
		ArrayList<String> rtn=new ArrayList<String>();
		LockToken token;
		Entry<String, LockToken> entry;
		while(it.hasNext()) {
			entry = it.next();
			token=entry.getValue();
			
			if(token.getDataCount()>0) {
				rtn.add(entry.getKey());
			}
		}
		return rtn.toArray(new String[rtn.size()]);
	}

	
	
	private LockData[] getLockDatas(int pageContextId) {
		Iterator<Entry<String, LockToken>> it = locks.entrySet().iterator();
		Iterator<LockData> itt;
		ArrayList<LockData> rtn=new ArrayList<LockData>();
		LockToken token;
		LockData data;
		
		while(it.hasNext()) {
			token=it.next().getValue();
			itt = token.datas.iterator();
			while(itt.hasNext()) {
				data=itt.next();
				if(data.getId()==pageContextId)rtn.add(data);
			}
		}
		return rtn.toArray(new LockData[rtn.size()]);
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
		Iterator<LockManagerImpl> it = managers.iterator();
		LockManagerImpl lmi;
		while(it.hasNext()) {
			lmi=it.next();
			lmi.unlock(pageContextId);
		}
	}
	
	
}