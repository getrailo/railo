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
	// FUTURE deprecated
	public int getIntervall();
	

	// FUTURE public int getInterval();
}
