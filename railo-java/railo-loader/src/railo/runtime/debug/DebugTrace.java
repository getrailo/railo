package railo.runtime.debug;

public interface DebugTrace {

	/**
	 * @return the category
	 */
	public String getCategory();

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
	public String getText();

	/**
	 * @return the time
	 */
	public long getTime();

	/**
	 * @return the type
	 */
	public int getType();

	/**
	 * @return the var
	 */
	public String getVarName();
	/**
	 * @return the var
	 */
	public String getVarValue();
	
	public String getAction();
	
}
