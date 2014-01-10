package railo.runtime.tag;

import railo.runtime.exp.PageException;
import railo.runtime.ext.tag.TagImpl;
import railo.runtime.type.KeyImpl;

/**
* Defines cookie variables, including expiration and security options.
*
*
*
**/
public final class Cookie extends TagImpl {

	/** Yes or No. Specifies that the variable must transmit securely. If the browser does not support
	** 	Secure Socket Layer (SSL) security, the cookie is not sent. */
	private boolean secure=false;

	/** The value assigned to the cookie variable. */
	private String value="";

	/**  */
	private String domain=null;

	/**  */
	private String path="/";

	/** Schedules the expiration of a cookie variable. Can be specified as a date (as in, 10/09/97), 
	** 	number of days (as in, 10, 100), "Now", or "Never". Using Now effectively deletes the cookie from
	** 	the client browser. */
	private Object expires=null;

	/** The name of the cookie variable. */
	private String name;
	
	private boolean httponly;
	private boolean preservecase;
	private boolean encode=true;


	@Override
	public void release()	{
		super.release();
		secure=false;
		value="";
		domain=null;
		path="/";
		expires=null;
		name=null;
		httponly=false;
		preservecase=false;
		encode=true;
	}
	
	/** set the value secure
	*  Yes or No. Specifies that the variable must transmit securely. If the browser does not support
	* 	Secure Socket Layer (SSL) security, the cookie is not sent.
	* @param secure value to set
	**/
	public void setSecure(boolean secure)	{
		this.secure=secure;
	}

	/** set the value value
	*  The value assigned to the cookie variable.
	* @param value value to set
	**/
	public void setValue(String value)	{
		this.value=value;
	}

	/** set the value domain
	*  
	* @param domain value to set
	**/
	public void setDomain(String domain)	{
		this.domain=domain;
	}

	/** set the value path
	*  
	* @param path value to set
	**/
	public void setPath(String path)	{
		this.path=path;
	}

	/** set the value expires
	*  Schedules the expiration of a cookie variable. Can be specified as a date (as in, 10/09/97), 
	* 	number of days (as in, 10, 100), "Now", or "Never". Using Now effectively deletes the cookie from
	* 	the client browser.
	* @param expires value to set
	**/
	public void setExpires(Object expires)	{
		this.expires=expires;
	}
	
	/** set the value expires
	*  Schedules the expiration of a cookie variable. Can be specified as a date (as in, 10/09/97), 
	* 	number of days (as in, 10, 100), "Now", or "Never". Using Now effectively deletes the cookie from
	* 	the client browser.
	* @param expires value to set
	* @deprecated replaced with setExpires(Object expires):void
	**/
	public void setExpires(String expires)	{
		this.expires=expires;
	}

	/** set the value name
	*  The name of the cookie variable.
	* @param name value to set
	**/
	public void setName(String name)	{
		this.name=name;
	}

	public void setHttponly(boolean httponly)	{
		this.httponly=httponly;
	}

	public void setPreservecase(boolean preservecase)	{
		this.preservecase=preservecase;
	}

	public void setEncodevalue(boolean encode)	{
		this.encode=encode;
	}

	public void setEncode(boolean encode)	{
		this.encode=encode;
	}


	@Override
	public int doStartTag() throws PageException	{
		pageContext.cookieScope().setCookie(KeyImpl.getInstance(name),value,expires,secure,path,domain,httponly,preservecase,encode);
		return SKIP_BODY;
	}

	@Override
	public int doEndTag()	{
		return EVAL_PAGE;
	}
}