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
		super("there is a timeout occurred on a "+toString(type)+" lock with name ["+name+"] after "+(timeout/1000)+" seconds");
	}

    private static String toString(int type) {
        if(LockManager.TYPE_EXCLUSIVE==type)return "exclusive";
        return "read-only";
    }

}