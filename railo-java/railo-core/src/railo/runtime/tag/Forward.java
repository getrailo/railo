package railo.runtime.tag;

import railo.runtime.exp.PageException;
import railo.runtime.ext.tag.TagImpl;
import railo.runtime.op.Caster;

/**
* Used to: Abort the processing of the currently executing CFML custom tag, exit the template 
*   within the currently executing CFML custom tag and reexecute a section of code within the currently
*   executing CFML custom tag
*
**/
public final class Forward extends TagImpl {

    private String template;
    
    /**
     * @param template The template to set.
     */
    public void setTemplate(String template) {
        this.template = template;
    }

    @Override
    public int doStartTag() throws PageException {
    	try {
			pageContext.forward(template);
		} 
    	catch (Exception e) {
            throw Caster.toPageException(e);
		} 
		
        /*RequestDispatcher disp = pageContext. getHttpServletRequest().getRequestDispatcher(template);
        try {
            disp.forward(pageContext. getHttpServletRequest(),pageContext. getHttpServletResponse());
        } 
        catch (Exception e) {
            throw Caster.toPageException(e);
        }*/
        return SKIP_BODY;
    }


}