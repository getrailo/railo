package railo.runtime;

import java.io.Serializable;

import railo.commons.io.res.Resource;


/**
 * represent a cfml source file
 */
public interface SourceFile extends Serializable {

    /**
	 * return file object, based on physical path and realpath
	 * @return file Object
	 */
	public Resource getPhyscalFile();

	/**
     * @return return source path as String
     */
    public String getDisplayPath();

	/**
	 * @return returns the full classname (package and name) matching to filename (Example: my.package.test_cfm)
	 */
	public String getFullClassName();

	/**
	 * @return returns the a classname matching to filename (Example: test_cfm)
	 */
	public String getClassName();

	/**
	 * @return returns the a package matching to file (Example: railo.web)
	 */
	public String getPackageName();

	/**
	 * @return returns a variable string based on realpath and return it
	 */
	public String getRealPathAsVariableString();

	/**
     * @return base Resource
     */
	public Resource getResource();
    
}