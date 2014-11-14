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

import java.io.IOException;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContextImpl;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.tag.TagImpl;
import railo.runtime.op.Caster;

/**
* Writes the text specified in the text attribute to the 'head' section of a generated HTML page. 
* 	 The cfhtmlhead tag can be useful for embedding JavaScript code, or placing other HTML tags such, as 
* 	 META, LINK, TITLE, or BASE in an HTML page header.
*
*
*
**/
public final class HtmlHead extends TagImpl {

	/** The text to add to the 'head' area of an HTML page. Everything inside the quotation marks is 
	** 		placed in the 'head' section */
	private String text="";
	private String variable="cfhtmlhead";
	private String action=null;



	@Override
	public void release()	{
		super.release();
		text="";
		variable="cfhtmlhead";
		action=null;
	}
	
	/**
	 * @param variable the variable to set
	 */
	public void setVariable(String variable) {
		this.variable = variable;
	}


	/**
	 * @param action the action to set
	 */
	public void setAction(String action) {
		if(!StringUtil.isEmpty(action,true))
			this.action = action.trim().toLowerCase();
		
		
	}


	/** set the value text
	*  The text to add to the 'head' area of an HTML page. Everything inside the quotation marks is 
	* 		placed in the 'head' section
	* @param text value to set
	**/
	public void setText(String text)	{
		this.text=text;
	}


	@Override
	public int doStartTag()	throws PageException {
		try {
			if(StringUtil.isEmpty(action,true) || action.equals("append")) actionAppend();
			else if(action.equals("reset")) actionReset();
			else if(action.equals("write")) actionWrite();
			else if(action.equals("read")) actionRead();
	        else throw new ApplicationException("invalid value ["+action+"] for attribute action","values for attribute action are:append,read,reset,write");
		} 
		catch (IOException e) {
			throw Caster.toPageException(e);
		}
		return SKIP_BODY;
	}
	
	public void actionAppend()	throws IOException, ApplicationException {
		required("htmlhead", "text", text);
		((PageContextImpl)pageContext).getRootOut().appendHTMLHead(text); 
	}
	
	public void actionWrite()	throws IOException, ApplicationException {
		required("htmlhead", "text", text);
		((PageContextImpl)pageContext).getRootOut().writeHTMLHead(text); 
	}
	
	public void actionReset() throws IOException {
		((PageContextImpl)pageContext).getRootOut().resetHTMLHead(); 
	}
	
	public void actionRead() throws PageException, IOException {
		String str=((PageContextImpl)pageContext).getRootOut().getHTMLHead(); 
		pageContext.setVariable(variable, str);
	}
	

	@Override
	public int doEndTag()	{
		return EVAL_PAGE;
	}
}