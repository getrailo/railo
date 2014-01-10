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


	@Override
	public int doStartTag() throws ExpressionException, ApplicationException {
		return SKIP_BODY;
	}

	@Override
	public int doEndTag()	{
		return EVAL_PAGE;
	}
}