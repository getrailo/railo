package railo.runtime.spooler;


public class ExecutionPlanImpl  implements ExecutionPlan {

	private int tries;
	private int interval;

	public ExecutionPlanImpl(int tries, int interval) {
		this.tries=tries;
		this.interval=interval;
	}

	/**
	 * @return the tries
	 */
	@Override
	public int getTries() {
		return tries;
	}

	/**
	 * @return the interval in seconds
	 */
	@Override
	public int getIntervall() {
		return interval;
	}
	
	@Override
	public int getInterval() {
		return interval;
	}
}
