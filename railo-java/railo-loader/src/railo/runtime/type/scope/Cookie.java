package railo.runtime.type.scope;

import railo.runtime.exp.PageException;
import railo.runtime.type.Collection;

/**
 * interface for the cookie scope
 */
public interface Cookie extends Scope {

    /**
     * set a cookie value
     * @param name name of the cookie
     * @param value value of the cookie
     * @param expires expirs of the cookie (Date, number in seconds or keyword as string )
     * @param secure set secure or not
     * @param path path of the cookie
     * @param domain domain of the cookie
     * @throws PageException 
     * @deprecated replaced with <code>setCookie(Collection.Key name, Object value, Object expires, boolean secure, String path, String domain)</code>
     */
	public abstract void setCookie(String name, Object value, Object expires, boolean secure, String path, String domain) throws PageException;

	/**
     * set a cookie value
     * @param name name of the cookie
     * @param value value of the cookie
     * @param expires expirs of the cookie (Date, number in seconds or keyword as string )
     * @param secure set secure or not
     * @param path path of the cookie
     * @param domain domain of the cookie
     * @throws PageException 
     */
	public abstract void setCookie(Collection.Key name, Object value, Object expires, boolean secure, String path, String domain) throws PageException;

    /**
     * set a cookie value
     * @param name Name of the cookie
     * @param value value of the cookie
     * @param expires expires in seconds
     * @param secure secute or not
     * @param path path of the cookie
     * @param domain domain of the cookie
     * @throws PageException
     * @deprecated replaced with <code>setCookie(Collection.Key name, Object value, int expires, boolean secure, String path, String domain)</code>
     */
	public abstract void setCookie(String name, Object value, int expires, boolean secure, String path, String domain)
    	throws PageException;
	
	/**
     * set a cookie value
     * @param name Name of the cookie
     * @param value value of the cookie
     * @param expires expires in seconds
     * @param secure secute or not
     * @param path path of the cookie
     * @param domain domain of the cookie
     * @throws PageException
     */
	public abstract void setCookie(Collection.Key name, Object value, int expires, boolean secure, String path, String domain)
    	throws PageException;

    /**
     * set a cookie value
     * @param name Name of the cookie
     * @param value value of the cookie
     * @param expires expires in seconds
     * @param secure secute or not
     * @param path path of the cookie
     * @param domain domain of the cookie 
     * @deprecated replaced with <code>setCookieEL(Collection.Key name, Object value, int expires, boolean secure, String path, String domain</code>
     */
    public abstract void setCookieEL(String name, Object value, int expires, boolean secure, String path, String domain);
    
    /**
     * set a cookie value
     * @param name Name of the cookie
     * @param value value of the cookie
     * @param expires expires in seconds
     * @param secure secute or not
     * @param path path of the cookie
     * @param domain domain of the cookie
     */
    public abstract void setCookieEL(Collection.Key name, Object value, int expires, boolean secure, String path, String domain);

}