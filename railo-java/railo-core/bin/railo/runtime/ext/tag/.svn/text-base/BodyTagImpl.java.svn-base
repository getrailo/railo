package railo.runtime.ext.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTag;


/**
 * Implementation of the BodyTag
 */
public abstract class BodyTagImpl extends TagImpl implements BodyTag {

	protected BodyContent bodyContent=null;

	/**
	 * @see javax.servlet.jsp.tagext.BodyTag#setBodyContent(javax.servlet.jsp.tagext.BodyContent)
	 */
	public void setBodyContent(BodyContent bodyContent) {
		this.bodyContent=bodyContent;
	}

	/**
	 * @see javax.servlet.jsp.tagext.BodyTag#doInitBody()
	 */
	public void doInitBody() throws JspException {
		
	}

	/**
	 * @see javax.servlet.jsp.tagext.IterationTag#doAfterBody()
	 */
	public int doAfterBody() throws JspException {
		return SKIP_BODY;
	}

	/**
	 * @see javax.servlet.jsp.tagext.Tag#release()
	 */
	public void release() {
		super.release();
		bodyContent=null;
	}
}