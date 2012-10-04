package railo.runtime.component;


public interface ImportDefintion {

	/**
	 * @return the wildcard
	 */
	public boolean isWildcard();

	/**
	 * @return the pack
	 */
	public String getPackage();

	/**
	 * @return the name
	 */
	public String getName();

	public String getPackageAsPath();
	
}
