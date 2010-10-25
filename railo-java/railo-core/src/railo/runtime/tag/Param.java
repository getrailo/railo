package railo.runtime.tag;

import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.tag.TagImpl;

/**
* Tests for a parameter's existence, tests its data type, and provides a default value if one 
*   is not assigned.
*
*
*
**/
public final class Param extends TagImpl {

	/** The type of parameter that is required. The default is 'any'. */
	private String type="any";

	/** Default value to set the parameter to if it does not exist. */
	private Object _default;

	/** The name of the parameter to test, such as Client.Email or Cookie.BackgroundColor. If 
	** 		you omit the DEFAULT attribute, an error occurs if the specified parameter does not exist */
	private String name;

	private double min;
	private double max;
	private String pattern;
	
	/**
	* @see javax.servlet.jsp.tagext.Tag#release()
	*/
	public void release()	{
		super.release();
		type="any";
		_default=null;
		name=null;
		
		min=-1;
		max=-1;
		pattern=null;
	}
	
	public Param() throws ApplicationException {
		throw new ApplicationException("this Tag Implemenation is deprecated and replaced with a Translation Time Transformer");
	}



	/** set the value type
	*  The type of parameter that is required. The default is 'any'.
	* @param type value to set
	**/
	public void setType(String type)	{
		this.type=type.trim().toLowerCase();
	}

	/** set the value default
	*  Default value to set the parameter to if it does not exist.
	* @param _default value to set
	**/
	public void setDefault(Object _default)	{
		this._default=_default;
	}

	/**
	 * @param max the max to set
	 */
	public void setMax(double max) {
		this.max = max;
	}

	/**
	 * @param min the min to set
	 */
	public void setMin(double min) {
		this.min = min;
	}

	/**
	 * @param pattern the pattern to set
	 */
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	/** set the value name
	*  The name of the parameter to test, such as Client.Email or Cookie.BackgroundColor. If 
	* 		you omit the DEFAULT attribute, an error occurs if the specified parameter does not exist
	* @param name value to set
	**/
	public void setName(String name)	{
		this.name=name;
	}


	/**
	* @see javax.servlet.jsp.tagext.Tag#doStartTag()
	*/
	public int doStartTag() throws PageException	{
		if("range".equals(type))
			pageContext.param(type, name, _default,min,max);
		else if("regex".equals(type) || "regular_expression".equals(type))
			pageContext.param(type, name, _default,pattern);
		else 
			pageContext.param(type, name, _default);
		return SKIP_BODY;
	}
	
}