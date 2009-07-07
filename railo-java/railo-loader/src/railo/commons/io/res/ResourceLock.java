package railo.commons.io.res;

import java.io.Serializable;

public interface ResourceLock extends Serializable {
	
	public void lock(Resource res);

	public void unlock(Resource res);

	public void read(Resource res);

	public long getLockTimeout();
	
	public void setLockTimeout(long timeout);
}
