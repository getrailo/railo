package railo.runtime.debug;

import java.io.Serializable;

public interface DebugTimer extends Serializable {
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
