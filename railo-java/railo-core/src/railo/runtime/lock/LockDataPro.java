package railo.runtime.lock;

import railo.commons.lock.Lock;

public interface LockDataPro extends LockData {
	public Lock getLock();// FUTURE add to bse interface and delete this interface
}
