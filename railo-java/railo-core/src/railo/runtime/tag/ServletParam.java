package railo.runtime.tag;

import railo.runtime.exp.ExpressionException;
import railo.runtime.ext.tag.TagImpl;

/**
* A child of cfservlet. It passes data to the servlet. Each cfservletparam tag within the cfservlet 
*   block passes a separate piece of data to the servlet.
*
*
*
**/
public final class ServletParam extends TagImpl {
	private String value;
	private String type;
	private String variable;
	private String name;


	/**
	* constructor for the tag class
	 * @throws ExpressionException
	**/
	public ServletParam() throws ExpressionException {
		throw new ExpressionException("tag cfservletparam is deprecated");
	}

	/** set the value value
	*  Value of a name-value pair passed to the servlet as a parameter.
	* @param value value to set
	**/
	public void setValue(String value)	{
		this.value=value;
	}

	/** set the value type
	* @param type value to set
	**/
	public void setType(String type)	{
		this.type=type;
	}

	/** set the value variable
	* @param variable value to set
	**/
	public void setVariable(String variable)	{
		this.variable=variable;
	}

	/** set the value name
	*  If used with the value attribute, it is the name of the servlet parameter. If used with the variable attribute, it is 
	* 		the name of the servlet attribute
	* @param name value to set
	**/
	public void setName(String name)	{
		this.name=name;
	}


	/**
	* @see javax.servlet.jsp.tagext.Tag#doStartTag()
	*/
	public int doStartTag()	{
		return SKIP_BODY;
	}

	/**
	* @see javax.servlet.jsp.tagext.Tag#doEndTag()
	*/
	public int doEndTag()	{
		return EVAL_PAGE;
	}

	/**
	* @see javax.servlet.jsp.tagext.Tag#release()
	*/
	public void release()	{
		super.release();
		value="";
		type="";
		variable="";
		name="";
	}
}