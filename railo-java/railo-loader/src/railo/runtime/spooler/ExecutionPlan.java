package railo.runtime.spooler;

import java.io.Serializable;

public interface ExecutionPlan extends Serializable {

	/**
	 * @return the tries
	 */
	public int getTries();

	/**
	 * @return the interval in seconds
	 */
	public int getInterval();
	
	/**
	 * @deprecated typo; use instead <code>public int getInterval();</code>
	 */
	public int getIntervall();
	

	
}
