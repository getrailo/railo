package railo.runtime.debug;

public interface QueryEntryPro extends QueryEntry {
	/**
	 * @return return the query executionn time in nanoseconds
	 */
	public long getExecutionTime();
}
