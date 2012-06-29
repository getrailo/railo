package railo.runtime.lock;


/**
 * Manager to open and close locks
 */
public interface LockManager {

    /**
     * Field <code>TYPE_READONLY</code>
     */
    public static final int TYPE_READONLY = 0;

    /**
     * Field <code>TYPE_EXCLUSIVE</code>
     */
    public static final int TYPE_EXCLUSIVE = 1;

    /**
     * locks a thread if already a other thread is come 
     * until other thread notify him by unlock method
     * @param type 
     * @param name Lock Name (not case sensitive)
     * @param timeout tiemout to for waiting in this method, if timeout occurs "lockTiemoutException" will be throwd
     * @param pageContextId 
     * @return  lock data object key for unlocking this lock
     * @throws LockTimeoutException
     * @throws InterruptedException
     */
    public abstract LockData lock(int type, String name, int timeout,
            int pageContextId) throws LockTimeoutException,
            InterruptedException;

    /**
     * unlocks a locked thread in lock method
     * @param data 
     */
    public abstract void unlock(LockData data);
    
    public String[] getOpenLockNames();
    

	public abstract void clean();

}