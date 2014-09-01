package railo.runtime.tag;

import railo.runtime.exp.PageException;
import railo.runtime.ext.tag.BodyTagImpl;
import railo.runtime.ext.tag.DynamicAttributes;
import railo.runtime.type.Collection;

public final class RailoConfiguration extends BodyTagImpl implements DynamicAttributes {

    public void setDynamicAttribute(String uri, Collection.Key localName, Object value) {
    }

	@Override
	public void setDynamicAttribute(String uri, String localName, Object value) {
	}
    
	@Override
	public int doEndTag() throws PageException	{
		// disable debug output
		pageContext.getDebugger().setOutput(false);
		
		// set 404
		pageContext.getHttpServletResponse().setStatus(404);
    	
		// reset response buffer
		pageContext.clear();
		
		return SKIP_PAGE;
	}
}