package railo.runtime.util.pool;

/**
 * Box for a Object for the Pool
 */
public abstract class PoolHandler {
	
	long time;
	
	/**
	 * constructor of the class
	 */
	public PoolHandler() {
		time=System.currentTimeMillis();
	}
	
	/**
	 * clear the Handler
	 */
	public abstract void clear();

	/**
	 * @return returns the Time
	 */
	public final long getTime() {
		return time;
	}
	/**
	 * Sets the Time
	 */
	public final void setTime() {
		time=System.currentTimeMillis();
	}

	/**
	 * sets the value
	 * @param o 
	 */
	public abstract void setData(Object o);

	/**
	 * returns the Value
	 * @return
	 */
	public abstract Object getData();
	
	
}