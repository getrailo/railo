package railo.runtime.debug;

import java.io.Serializable;

// FUTURE move to loader

public interface DebugDump extends Serializable {

	/**
	 * @return the line
	 */
	public int getLine();

	/**
	 * @return the template
	 */
	public String getTemplate();

	/**
	 * @return the text
	 */
	public String getOutput();	
}
