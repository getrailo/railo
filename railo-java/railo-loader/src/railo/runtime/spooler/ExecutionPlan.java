package railo.runtime.spooler;

import java.io.Serializable;

public interface ExecutionPlan extends Serializable {

	/**
	 * @return the tries
	 */
	public int getTries();

	/**
	 * @return the interval in seconds
	 * @deprecated use instead <code>getInterval();</code>
	 */
	public int getIntervall();
	

	/**
	 * @return the interval in seconds
	 */
	public int getInterval();
}
