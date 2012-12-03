package railo.runtime.debug;

public interface ImplicitAccess {

	public void inc();

	/**
	 * @return the count
	 */
	public int getCount();

	/**
	 * @return the scope
	 */
	public String getScope();

	/**
	 * @return the template
	 */
	public String getTemplate();

	/**
	 * @return the line
	 */
	public int getLine();

	/**
	 * @return the name
	 */
	public String getName();
}
