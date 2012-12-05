package railo.commons.io.res.util;

import java.util.HashMap;
import java.util.Map;

import railo.commons.io.res.Resource;
import railo.commons.io.res.ResourceLock;
import railo.commons.lang.SerializableObject;
import railo.commons.lang.SystemOut;
import railo.runtime.config.Config;
import railo.runtime.engine.ThreadLocalPageContext;

public final class ResourceLockImpl implements ResourceLock {
	
	private static final long serialVersionUID = 6888529579290798651L;
	
	private long lockTimeout;
	private boolean caseSensitive;

	public ResourceLockImpl(long timeout,boolean caseSensitive) {
		this.lockTimeout=timeout;
		this.caseSensitive=caseSensitive;
	}

	private Object token=new SerializableObject();
	private Map<String,Thread> resources=new HashMap<String,Thread>();
	
	@Override
	public void lock(Resource res) {
		String path=getPath(res);
		
		synchronized(token)  {
			_read(path);
			resources.put(path,Thread.currentThread());
		}
	}

	private String getPath(Resource res) {
		return caseSensitive?res.getPath():res.getPath().toLowerCase();
	}

	@Override
	public void unlock(Resource res) {
		String path=getPath(res);
		//if(path.endsWith(".dmg"))print.err("unlock:"+path);
		synchronized(token)  {
			resources.remove(path);
			token.notifyAll();
		}
	}

	@Override
	public void read(Resource res) {
		String path=getPath(res);
		synchronized(token)  {
			//print.ln(".......");
			_read(path);
		}
	}

	private void _read(String path) {
		long start=-1,now;
		Thread t;
		do {
			if((t=resources.get(path))==null) {
				//print.ln("read ok");
				return;
			}
			if(t==Thread.currentThread()) {
				//aprint.err(path);
				Config config = ThreadLocalPageContext.getConfig();
				if(config!=null)
					SystemOut.printDate(config.getErrWriter(),"conflict in same thread: on "+path);
				//throw new RuntimeException("conflict in same thread: on "+res);
				return;
			}
			// bugfix when lock von totem thread, wird es ignoriert
			if(!t.isAlive()) {
				resources.remove(path);
				return;
			}
			if(start==-1)start=System.currentTimeMillis();
			try {
				token.wait(lockTimeout);
				now=System.currentTimeMillis();
				if((start+lockTimeout)<=now) {
					Config config = ThreadLocalPageContext.getConfig();
					if(config!=null)
						SystemOut.printDate(config.getErrWriter(),"timeout after "+(now-start)+" ms ("+(lockTimeout)+" ms) occured while accessing file ["+path+"]");
					else 
						SystemOut.printDate("timeout ("+(lockTimeout)+" ms) occured while accessing file ["+path+"]");
					return;
				}
			} 
			catch (InterruptedException e) {
			}
		}
		while(true);
	}

	@Override
	public long getLockTimeout() {
		return lockTimeout;
	}

	/**
	 * @param lockTimeout the lockTimeout to set
	 */
	public void setLockTimeout(long lockTimeout) {
		this.lockTimeout = lockTimeout;
	}

	/**
	 * @param caseSensitive the caseSensitive to set
	 */
	public void setCaseSensitive(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}
}
