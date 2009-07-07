package railo.runtime.tag;

import railo.runtime.exp.PageException;
import railo.runtime.exp.TagNotSupported;
import railo.runtime.ext.tag.TagImpl;

public class AjaxImport extends TagImpl {
	private String csssrc;
	private String scriptsrc;
	private String tags;
	


	/**
	* @see javax.servlet.jsp.tagext.Tag#release()
	*/
	public void release()	{
		super.release();
		csssrc=null;
		scriptsrc=null;
		tags=null;
	}
 
	/**
	 * @param csssrc the csssrc to set
	 */
	public void setCsssrc(String csssrc) {
		this.csssrc = csssrc;
	}

	/**
	 * @param scriptsrc the scriptsrc to set
	 */
	public void setScriptsrc(String scriptsrc) {
		this.scriptsrc = scriptsrc;
	}

	/**
	 * @param tags the tags to set
	 */
	public void setTags(String tags) {
		this.tags = tags;
	}

	/**
	* @see javax.servlet.jsp.tagext.Tag#doStartTag()
	*/
	public int doStartTag() throws PageException	{
		throw new TagNotSupported("AjaxImport");
		//return SKIP_BODY;
	}

	/**
	* @see javax.servlet.jsp.tagext.Tag#doEndTag()
	*/
	public int doEndTag()	{
		return EVAL_PAGE;
	}
}