/**
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library.  If not, see <http://www.gnu.org/licenses/>.
 * 
 **/
package railo.runtime.tag;

import railo.runtime.PageSource;
import railo.runtime.err.ErrorPageImpl;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.MissingIncludeException;
import railo.runtime.ext.tag.TagImpl;

/**
* Enables the display of customized HTML pages when errors occur. This lets you maintain a 
*   consistent look and feel within your application, even when errors occur.
*
*
*
**/
public final class Error extends TagImpl {


	private ErrorPageImpl errorPage=new ErrorPageImpl();


	
	@Override
	public void release()	{
		super.release();
		errorPage=new ErrorPageImpl();
		//exception="any";
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
		if(type.equals("exception")) {
			errorPage.setType(ErrorPageImpl.TYPE_EXCEPTION);
		}
		else if(type.equals("request")) {
			errorPage.setType(ErrorPageImpl.TYPE_REQUEST);
		}
		//else if(type.equals("validation")) this.type=VALIDATION;
		else throw new ExpressionException("invalid type ["+type+"] for tag error, use one of the following types [exception,request]");
	}

	/** set the value template
	*  The relative path to the custom error page.
	* @param template value to set
	* @throws MissingIncludeException 
	**/
	public void setTemplate(String template) throws MissingIncludeException	{
	    PageSource ps=pageContext.getCurrentPageSource().getRealPage(template);
	    if(!ps.exists())
			throw new MissingIncludeException(ps);
		errorPage.setTemplate(ps);
	}
	
	/** set the value mailto
	*  The e-mail address of the administrator to notify of the error. The value
	* 	is available to your custom error page in the MailTo property of the error object.
	* @param mailto value to set
	**/
	public void setMailto(String mailto)	{
		errorPage.setMailto(mailto);
	}


	@Override
	public int doStartTag()	{
		if(errorPage.getType()==ErrorPageImpl.TYPE_REQUEST) errorPage.setException("any");
		pageContext.setErrorPage(errorPage);
		return SKIP_BODY;
	}

	@Override
	public int doEndTag()	{
		return EVAL_PAGE;
	}
}