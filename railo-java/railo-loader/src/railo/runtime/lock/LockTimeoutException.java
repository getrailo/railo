package railo.runtime.lock;


/**
 * Lock Timeout
 */
public final class LockTimeoutException extends Exception {

	/**
	 * @param type type of the log
	 * @param name name of the Lock
	 * @param timeout 
	 */
	public LockTimeoutException(int type, String name, int timeout) {
	    //A timeout occurred while attempting to lock lockname
		super("a timeout occurred on a "+toString(type)+" lock with name ["+name+"] after "+getTime(timeout));
	}

    private static String getTime(int timeout) {
		if(timeout/1000*1000==timeout) {
			int s = timeout/1000;
			return s+(s>1?" seconds":" second");
		}
		return timeout+(timeout>1?" milliseconds":" millisecond");
	}

	private static String toString(int type) {
        if(LockManager.TYPE_EXCLUSIVE==type)return "exclusive";
        return "read-only";
    }

}