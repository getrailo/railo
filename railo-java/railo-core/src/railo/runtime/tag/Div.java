package railo.runtime.tag;

import railo.runtime.exp.PageException;
import railo.runtime.exp.TagNotSupported;
import railo.runtime.ext.tag.BodyTagImpl;

// MUST change behavor of mltiple headers now is a array, it das so?

/**
* Lets you execute HTTP POST and GET operations on files. Using cfhttp, you can execute standard 
*   GET operations and create a query object from a text file. POST operations lets you upload MIME file 
*   types to a server, or post cookie, formfield, URL, file, or CGI variables directly to a specified server.
*
*
*
* 
**/
public final class Div extends BodyTagImpl {
	
	private String bind;
	private boolean bindOnLoad;
	private String id;
	private String onBindError;
	private String tagName;
	
	@Override
	public void release()	{
		super.release();
	    this.bind=null;
	    this.bindOnLoad=false;
	    this.id=null;
	    this.onBindError=null;
	    this.tagName=null;
	}
	
	
	
	
	/**
	 * @param bind the bind to set
	 */
	public void setBind(String bind) {
		this.bind = bind;
	}




	/**
	 * @param bindOnLoad the bindOnLoad to set
	 */
	public void setBindonload(boolean bindOnLoad) {
		this.bindOnLoad = bindOnLoad;
	}




	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}




	/**
	 * @param onBindError the onBindError to set
	 */
	public void setOnbinderror(String onBindError) {
		this.onBindError = onBindError;
	}




	/**
	 * @param tagName the tagName to set
	 */
	public void setTagname(String tagName) {
		this.tagName = tagName;
	}




	@Override
	public int doStartTag() throws TagNotSupported	{
		throw new TagNotSupported("Div");
		//return EVAL_BODY_INCLUDE;
	}

	@Override
	public int doEndTag() throws PageException {
		return EVAL_PAGE;
	}

    @Override
	public void doInitBody()	{
	}

	@Override
	public int doAfterBody()	{
		return SKIP_BODY;
	}

	/**
	 * sets if has body or not
	 * @param hasBody
	 */
	public void hasBody(boolean hasBody) {
	}
}