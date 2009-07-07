package railo.runtime.debug;

public interface DebugTimer {
	/**
	 * @return the label
	 */
	public String getLabel();

	/**
	 * @return the template
	 */
	public String getTemplate();

	/**
	 * @return the time
	 */
	public long getTime();
}
