package railo.runtime.ext.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.PageContextImpl;
import railo.runtime.exp.ApplicationException;

/**
 * Implementation of the Tag
 */
public abstract class TagImpl implements Tag {

	protected PageContext pageContext; 
	private Tag parent;
	   
	/**
	 * sets a Railo PageContext
	 * @param pageContext
	 */
	public void setPageContext(PageContextImpl pageContext) {
		this.pageContext=pageContext;
	}
	@Override
	public void setPageContext(javax.servlet.jsp.PageContext pageContext) {
		this.pageContext=(PageContext) pageContext;
	}

	@Override
	public void setParent(Tag parent) {
        this.parent=parent;
	}

	@Override
	public Tag getParent() {
        return parent;
	}

	@Override
	public int doStartTag() throws JspException {
		return SKIP_BODY;
	}

	@Override
	public int doEndTag() throws JspException {
		return EVAL_PAGE;
	}

	@Override
	public void release() {
		pageContext=null;
		parent=null;
	}    
	
	/**
	 * check if value is not empty
	 * @param tagName
	 * @param attributeName
	 * @param attribute
	 * @throws ApplicationException
	 */
	public void required(String tagName, String actionName, String attributeName, Object attribute) throws ApplicationException {
	    if(attribute==null)
	        throw new ApplicationException("Attribute ["+attributeName+"] for tag ["+tagName+"] is required if attribute action has the value ["+actionName+"]");
    
	}
	public void required(String tagName, String attributeName, Object attribute) throws ApplicationException {
	    if(attribute==null)
	        throw new ApplicationException("Attribute ["+attributeName+"] for tag ["+tagName+"] is required");
    
	}
	
	public void required(String tagName, String actionName, String attributeName, String attribute,boolean trim) throws ApplicationException {
	    if(StringUtil.isEmpty(attribute,trim))
	        throw new ApplicationException("Attribute ["+attributeName+"] for tag ["+tagName+"] is required if attribute action has the value ["+actionName+"]");
    }
	
	public void required(String tagName, String actionName, String attributeName, double attributeValue, double nullValue) throws ApplicationException {
	    if(attributeValue==nullValue)
	        throw new ApplicationException("Attribute ["+attributeName+"] for tag ["+tagName+"] is required if attribute action has the value ["+actionName+"]");
    }
	
	
	
}