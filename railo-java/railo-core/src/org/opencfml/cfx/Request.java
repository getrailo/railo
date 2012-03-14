package org.opencfml.cfx;

/**
 * Alternative Implementation of Jeremy Allaire's Request Interface
 */
public interface Request {

	/**
	 * checks if attribute with this key exists
	 * @param key key to check
	 * @return has key or not
	 */
	public boolean attributeExists(String key);
	
	/**
	 * @return if tags has set [debug] attribute
	 */
	public boolean debug();
	
	/**
	 * returns attribute matching key
	 * @param key key to get
	 * @return value to key
	 */
	public String getAttribute(String key);
	
	/**
	 * returns attribute matching key
	 * @param key key to get
	 * @param defaultValue return this value if key not exist
	 * @return value to key
	 */
	public String getAttribute(String key, String defaultValue);
	
	/**
	 * return all sattribute keys
	 * @return all keys
	 */
	public String[] getAttributeList();
	
	/**
	 * returns attribute as int matching key
	 * @param key key to get
	 * @return value to key
	 * @throws NumberFormatException
	 */
	public int getIntAttribute(String key) throws NumberFormatException;
	
	/**
	 * returns attribute as int matching key
	 * @param key key to get
	 * @param defaultValue return this value if key not exist
	 * @return value to key
	 */
	public int getIntAttribute(String key, int defaultValue);
	
	/**
	 * return given query
	 * @return return given query
	 */
	public Query getQuery();

	/**
	 * returns all the settings
	 * @param key
	 * @return settings
	 */
	public String getSetting(String key);

}