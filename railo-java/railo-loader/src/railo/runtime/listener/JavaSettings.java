package railo.runtime.listener;

import railo.commons.io.res.Resource;

public interface JavaSettings {

	/**
	 * pathes to the directories that contain Java classes or JAR files.
	 * @return resource pathes
	 */
	public Resource[] getResources();

	/**
	 * Indicates whether to load the classes from the default lib directory. 
	 * The default value is false
	 * @return
	 */
	public boolean loadCFMLClassPath();
	
	/**
	 * Indicates whether to reload the updated classes and JARs dynamically, without restarting ColdFusion. 
	 * The default value is false
	 * @return
	 */
	public boolean reloadOnChange();
	
	/**
	 * Specifies the time interval in seconds after which to verify any change in the class files or JAR files.
	 * The default value is 60seconds
	 * @return
	 */
	public int watchInterval();
	
	/**
	 * Specifies the extensions of the files to monitor for changes. 
	 * By default, only .class and .jar files aremonitored.

	 * @return
	 */
	public String[] watchedExtensions();

}
