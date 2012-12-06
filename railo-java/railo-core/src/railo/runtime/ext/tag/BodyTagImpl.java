package railo.runtime.ext.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTag;


/**
 * Implementation of the BodyTag
 */
public abstract class BodyTagImpl extends TagImpl implements BodyTag {

	protected BodyContent bodyContent=null;

	@Override
	public void setBodyContent(BodyContent bodyContent) {
		this.bodyContent=bodyContent;
	}

	@Override
	public void doInitBody() throws JspException {
		
	}

	@Override
	public int doAfterBody() throws JspException {
		return SKIP_BODY;
	}

	@Override
	public void release() {
		super.release();
		bodyContent=null;
	}
}