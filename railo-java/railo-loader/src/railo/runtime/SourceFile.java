package railo.runtime;

import java.io.Serializable;

import railo.commons.io.res.Resource;


/**
 * represent a cfml source file
 */
public interface SourceFile extends Serializable {

    /**
	 * return file object, based on physical path and relpath
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
	 * @return returns a variable string based on relpath and return it
	 */
	public String getRelPathAsVariableString();

	/**
	 * if the mapping physical path and archive is invalid or not defined, it is possible this method returns null
     * @return base Resource
     */
	public Resource getResource();
    
}