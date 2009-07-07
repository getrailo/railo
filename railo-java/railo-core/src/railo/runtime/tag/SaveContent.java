package railo.runtime.tag;

import railo.runtime.exp.PageException;
import railo.runtime.ext.tag.BodyTagTryCatchFinallyImpl;

/**
* Saves the generated content inside the tag body in a variable.
*
*
*
**/
public final class SaveContent extends BodyTagTryCatchFinallyImpl {

	/** The name of the variable in which to save the generated content inside the tag. */
	private String variable;
	private boolean trim;
	
	/**
	* @see javax.servlet.jsp.tagext.Tag#release()
	*/
	public void release()	{
		super.release();
		variable=null;
		trim=false;
	}


	/** set the value variable
	*  The name of the variable in which to save the generated content inside the tag.
	* @param variable value to set
	**/
	public void setVariable(String variable)	{
		this.variable=variable;
	}
	

	public void setTrim(boolean trim)	{
		this.trim=trim;
	}
	
	/**
	* @see javax.servlet.jsp.tagext.Tag#doStartTag()
	*/
	public int doStartTag()	{
		return EVAL_BODY_BUFFERED;
	}


	/**
	* @see javax.servlet.jsp.tagext.BodyTag#doAfterBody()
	*/
	public int doAfterBody() throws PageException	{
		pageContext.setVariable(variable,trim?bodyContent.getString().trim():bodyContent.getString());
		bodyContent.clearBody();
		
		return SKIP_BODY;
	}

	
	
	
	
	
}