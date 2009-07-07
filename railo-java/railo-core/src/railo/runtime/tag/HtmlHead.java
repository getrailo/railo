package railo.runtime.tag;

import java.io.IOException;

import railo.runtime.PageContextImpl;
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

	/** set the value text
	*  The text to add to the 'head' area of an HTML page. Everything inside the quotation marks is 
	* 		placed in the 'head' section
	* @param text value to set
	**/
	public void setText(String text)	{
		this.text=text;
	}


	/**
	* @see javax.servlet.jsp.tagext.Tag#doStartTag()
	*/
	public int doStartTag()	throws PageException {
		try {
            ((PageContextImpl)pageContext).getRootOut().setToHTMLHead(text); 
		} catch (IOException e) {
			throw Caster.toPageException(e);
		}
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
		text="";
	}
}