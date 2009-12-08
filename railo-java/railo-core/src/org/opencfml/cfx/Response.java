package org.opencfml.cfx;

/**
 * Alternative Implementation of Jeremy Allaire's Response Interface
 */
public interface Response {

	/**
	 * adds a query to response
	 * @param name name of the new Query
	 * @param column columns of the new Query
	 * @return created query
	 */
	public Query addQuery(String name, String[] column);

	/**
	 * sets a variable to response
	 * @param key key of the variable
	 * @param value value of the variable
	 */
	public void setVariable(String key, String value);

	/**
	 * write out a String to response
	 * @param str String to write
	 */
	public void write(String str);

	/**
	 * write out if debug is enabled
	 * @param str String to write
	 */
	public void writeDebug(String str);

}