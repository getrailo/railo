package railo.runtime.tag;

import javax.servlet.jsp.JspException;

import railo.runtime.ext.tag.TagImpl;

/**
 * 
 */
public final class Logout extends TagImpl {

    @Override
    public int doStartTag() throws JspException {
        pageContext.clearRemoteUser();
        return SKIP_BODY;
    }
}