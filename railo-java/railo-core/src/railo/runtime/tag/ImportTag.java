package railo.runtime.tag;

import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.ExpressionException;
import railo.runtime.ext.tag.TagImpl;

/**
 * this tag is not used, it will ranslation over a evaluator
 * 
 * 
* Imports a jsp Tag Library or a Custom Tag Directory
*
*
*
**/
public final class ImportTag extends TagImpl {

	private String path;

	/**
	 * @see railo.runtime.ext.tag.TagImpl#release()
	 */
	@Override
	public void release() {
		path=null;
		super.release();
	}

	/**
	 * @param prefix
	 */
	public void setPrefix(String prefix)	{}
	
	public void setPath(String path)	{
		this.path=path;
		
	}

	/**
	 * @param taglib
	 */
	public void setTaglib(String taglib)	{}


	/**
	 * @see javax.servlet.jsp.tagext.Tag#doStartTag()
	*/
	public int doStartTag() throws ExpressionException, ApplicationException {
		return SKIP_BODY;
	}

	/**
	* @see javax.servlet.jsp.tagext.Tag#doEndTag()
	*/
	public int doEndTag()	{
		return EVAL_PAGE;
	}
}