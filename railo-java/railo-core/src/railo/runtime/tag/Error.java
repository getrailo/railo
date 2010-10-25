package railo.runtime.tag;

import railo.runtime.PageSource;
import railo.runtime.err.ErrorPage;
import railo.runtime.err.ErrorPageImpl;
import railo.runtime.exp.ExpressionException;
import railo.runtime.ext.tag.TagImpl;

/**
* Enables the display of customized HTML pages when errors occur. This lets you maintain a 
*   consistent look and feel within your application, even when errors occur.
*
*
*
**/
public final class Error extends TagImpl {
	
	private static final short TYPE_EXCEPTION=0;

	private ErrorPage errorPage=new ErrorPageImpl();


	/** The type of error that the custom error page handles. */
	private short type;

	/**
	* @see javax.servlet.jsp.tagext.Tag#release()
	*/
	public void release()	{
		super.release();
		errorPage=new ErrorPageImpl();
		//exception="any";
		type=0;
		//template=null;
		//mailto="";
		
	}

	/** set the value exception
	*  Type of exception. Required if type = "exception" or "monitor".
	* @param exception value to set
	**/
	public void setException(String exception)	{
		errorPage.setTypeAsString(exception.toLowerCase().trim());
		//this.exception=exception.toLowerCase().trim();
	}

	/** set the value type
	*  The type of error that the custom error page handles.
	* @param type value to set
	 * @throws ExpressionException
	**/
	public void setType(String type) throws ExpressionException	{
		type=type.toLowerCase().trim();
		if(type.equals("exception")) this.type=TYPE_EXCEPTION;
		//else if(type.equals("validation")) this.type=VALIDATION;
		//else if(type.equals("request")) this.type=REQUEST;
		else throw new ExpressionException("invalid type for tag error","only exception is supported");
	}

	/** set the value template
	*  The relative path to the custom error page.
	* @param template value to set
	 * @throws ExpressionException
	**/
	public void setTemplate(String template) throws ExpressionException	{
	    PageSource sf=pageContext.getCurrentPageSource().getRealPage(template);
	    //new PageSource(pageContext.getCurrentTemplateSourceFile(),template);
		if(!sf.exists())
			throw new ExpressionException("template ["+template+"] is not defined");
		errorPage.setTemplate(sf);
	}
	
	/** set the value mailto
	*  The e-mail address of the administrator to notify of the error. The value
	* 	is available to your custom error page in the MailTo property of the error object.
	* @param mailto value to set
	**/
	public void setMailto(String mailto)	{
		errorPage.setMailto(mailto);
	}


	/**
	* @see javax.servlet.jsp.tagext.Tag#doStartTag()
	*/
	public int doStartTag()	{
		if(type==TYPE_EXCEPTION)pageContext.setErrorPage(errorPage);
		
		return SKIP_BODY;
	}

	/**
	* @see javax.servlet.jsp.tagext.Tag#doEndTag()
	*/
	public int doEndTag()	{
		return EVAL_PAGE;
	}
}