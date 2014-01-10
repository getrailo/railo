package railo.runtime.exp;

import railo.runtime.config.Config;

/**
 * 
 */
public final class LockException extends PageExceptionImpl {
    
    /**
     * Field <code>OPERATION_TIMEOUT</code>
     */
    public static final String OPERATION_TIMEOUT="Timeout";
    /**
     * Field <code>OPERATION_MUTEX</code>
     */
    public static final String OPERATION_MUTEX="Mutex";
    /**
     * Field <code>OPERATION_CREATE</code>
     */
    public static final String OPERATION_CREATE="Create";
    /**
     * Field <code>OPERATION_UNKNOW</code>
     */
    public static final String OPERATION_UNKNOW="Unknown";
    
    private String lockName="";
    private String lockOperation="Unknown";
    
	/**
	 * Class Constuctor
	 * @param operation
	 * @param name
	 * @param message error message
	 */
	public LockException(String operation,String name,String message) {
		super(message,"lock"); 
		this.lockName=name;
		this.lockOperation=operation;
	}

	/**
	 * Class Constuctor
	 * @param operation
	 * @param name
	 * @param message error message
	 * @param detail detailed error message
	 */
	public LockException(String operation,String name,String message, String detail) {
		super(message,"lock");
		this.lockName=name;
		this.lockOperation=operation;
		setDetail(detail);
	}
	
	@Override
	public CatchBlock getCatchBlock(Config config) {
		CatchBlock sct=super.getCatchBlock(config);
		sct.setEL("LockName",lockName);
		sct.setEL("LockOperation",lockOperation);
		return sct;
	}
}