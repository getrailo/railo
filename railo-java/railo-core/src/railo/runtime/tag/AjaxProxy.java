package railo.runtime.tag;

import railo.runtime.exp.PageException;
import railo.runtime.exp.TagNotSupported;
import railo.runtime.ext.tag.TagImpl;

public class AjaxProxy extends TagImpl {
	private String bind;
	private String cfc;
	private String jsClassName;
	private String onError;
	private String onSuccess;
	


	@Override
	public void release()	{
		super.release();
		bind=null;
		cfc=null;
		jsClassName=null;
		onError=null;
		onSuccess=null;
	}


	/**
	 * @param bind the bind to set
	 */
	public void setBind(String bind) {
		this.bind = bind;
	}


	/**
	 * @param cfc the cfc to set
	 */
	public void setCfc(String cfc) {
		this.cfc = cfc;
	}


	/**
	 * @param jsClassName the jsClassName to set
	 */
	public void setJsclassname(String jsClassName) {
		this.jsClassName = jsClassName;
	}


	/**
	 * @param onError the onError to set
	 */
	public void setOnerror(String onError) {
		this.onError = onError;
	}


	/**
	 * @param onSuccess the onSuccess to set
	 */
	public void setOnsuccess(String onSuccess) {
		this.onSuccess = onSuccess;
	}
 

	@Override
	public int doStartTag() throws PageException	{
		throw new TagNotSupported("AjaxProxy");
		//return SKIP_BODY;
	}

	@Override
	public int doEndTag()	{
		return EVAL_PAGE;
	}
}