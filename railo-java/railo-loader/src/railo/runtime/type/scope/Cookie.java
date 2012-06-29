package railo.runtime.type.scope;

import railo.runtime.exp.PageException;
import railo.runtime.type.Collection;

/**
 * interface for the cookie scope
 */
public interface Cookie extends Scope, UserScope {

	/**
     * set a cookie value
     * @param name name of the cookie
     * @param value value of the cookie
     * @param expires expirs of the cookie (Date, number in seconds or keyword as string )
     * @param secure set secure or not
     * @param path path of the cookie
     * @param domain domain of the cookie
     * @throws PageException 
     * @deprecated
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
     * @deprecated
     */
	public abstract void setCookie(Collection.Key name, Object value, int expires, boolean secure, String path, String domain) throws PageException;

    
    /**
     * set a cookie value
     * @param name Name of the cookie
     * @param value value of the cookie
     * @param expires expires in seconds
     * @param secure secute or not
     * @param path path of the cookie
     * @param domain domain of the cookie
     * @deprecated
     */
    public abstract void setCookieEL(Collection.Key name, Object value, int expires, boolean secure, String path, String domain);
    


	/**
     * set a cookie value
     * @param name name of the cookie
     * @param value value of the cookie
     * @param expires expirs of the cookie (Date, number in seconds or keyword as string )
     * @param secure set secure or not
     * @param path path of the cookie
     * @param domain domain of the cookie
     * @param httpOnly if true, sets cookie as httponly so that it cannot be accessed using JavaScripts. Note that the browser must have httponly compatibility.
     * @param preserveCase if true, keep the case of the name as it is
     * @param encode if true, url encode the name and the value
     * @throws PageException 
     */
	public abstract void setCookie(Collection.Key name, Object value, Object expires, boolean secure, String path, String domain, 
			boolean httpOnly, boolean preserveCase, boolean encode) throws PageException;

	
	/**
     * set a cookie value
     * @param name Name of the cookie
     * @param value value of the cookie
     * @param expires expires in seconds
     * @param secure secute or not
     * @param path path of the cookie
     * @param domain domain of the cookie
     * @param httpOnly if true, sets cookie as httponly so that it cannot be accessed using JavaScripts. Note that the browser must have httponly compatibility.
     * @param preserveCase if true, keep the case of the name as it is
     * @param encode if true, url encode the name and the value
     * @throws PageException
     */
	public abstract void setCookie(Collection.Key name, Object value, int expires, boolean secure, String path, String domain, 
			boolean httpOnly, boolean preserveCase, boolean encode) throws PageException;

    
    /**
     * set a cookie value
     * @param name Name of the cookie
     * @param value value of the cookie
     * @param expires expires in seconds
     * @param secure secute or not
     * @param path path of the cookie
     * @param domain domain of the cookie
     * @param httpOnly if true, sets cookie as httponly so that it cannot be accessed using JavaScripts. Note that the browser must have httponly compatibility.
     * @param preserveCase if true, keep the case of the name as it is
     * @param encode if true, url encode the name and the value
     */
    public abstract void setCookieEL(Collection.Key name, Object value, int expires, boolean secure, String path, String domain, 
    		boolean httpOnly, boolean preserveCase, boolean encode);
    
   
	

}