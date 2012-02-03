package railo.commons.lock;

import railo.runtime.exp.ApplicationException;
import railo.runtime.lock.LockManager;


/**
 * Lock Timeout
 */
public final class LockException extends ApplicationException {

	private static final long serialVersionUID = 9132132031478280069L;

	/**
	 * @param type type of the log
	 * @param name name of the Lock
	 * @param timeout 
	 */
	public LockException(int type, String name, long timeout) {
	    //A timeout occurred while attempting to lock lockname
		super("a timeout occurred on a "+toString(type)+" lock with name ["+name+"] after "+(timeout/1000)+" seconds");
	}
	

	public LockException(Long timeout) {
		
	    super("a timeout occurred after "+toTime(timeout));
	}
	

	public LockException(String text) {
	    super(text);
	}

    private static String toTime(long timeout) {
		
    	if(timeout>=1000 && (((timeout/1000))*1000)==timeout)
    		return (timeout/1000)+" seconds";
    	 return timeout+" milliseconds";
	}


	private static String toString(int type) {
        if(LockManager.TYPE_EXCLUSIVE==type)return "exclusive";
        return "read-only";
    }

}