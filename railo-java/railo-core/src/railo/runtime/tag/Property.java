package railo.runtime.tag;

import railo.runtime.ComponentImpl;
import railo.runtime.ComponentScope;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.tag.DynamicAttributes;
import railo.runtime.ext.tag.TagImpl;

/**
* Defines components as complex types that are used for web services authoring. The attributes of this tag are exposed as component metadata and are subject to inheritance rules.
*
*
*
**/
public final class Property extends TagImpl  implements DynamicAttributes{
	
	private railo.runtime.component.Property property=new railo.runtime.component.Property();
	
	/**
	* @see javax.servlet.jsp.tagext.Tag#release()
	*/
	public void release()	{
		super.release();
		
		property=new railo.runtime.component.Property();
	}
	
	/**
	 * @see railo.runtime.ext.tag.DynamicAttributes#setDynamicAttribute(java.lang.String, java.lang.String, java.lang.Object)
	 */
	public void setDynamicAttribute(String uri, String name, Object value) {
		property.getMeta().put(name,value);
	}
	
	/** set the value type
	*  A string; a property type name; data type.
	* @param type value to set
	**/
	public void setType(String type)	{
		property.setType(type);
		setDynamicAttribute(null, "type", type);
	}

	/** set the value name
	*  A string; a property name. Must be a static value.
	* @param name value to set
	**/
	public void setName(String name)	{
		// Fix for axis 1.4, axis can not handle when first char is upper case
		//name=StringUtil.lcFirst(name.toLowerCase());
		
		property.setName(name);
		setDynamicAttribute(null, "name", name);
	}
	
    /**
     * @param _default The _default to set.
     */
    public void setDefault(String _default) {
		property.setDefault(_default);
		setDynamicAttribute(null, "default", _default);
		
    }
    /**
     * @param access The access to set.
     * @throws ExpressionException 
     */
    public void setAccess(String access) throws ExpressionException {
    	setDynamicAttribute(null, "access", access);
		//property.setAccess(access);
    }
    /**
     * @param displayname The displayname to set.
     */
    public void setDisplayname(String displayname) {
		property.setDisplayname(displayname);
		setDynamicAttribute(null, "displayname", displayname);
    }
    /**
     * @param hint The hint to set.
     */
    public void setHint(String hint) {
		property.setHint(hint);
		setDynamicAttribute(null, "hint", hint);
    }
    /**
     * @param required The required to set.
     */
    public void setRequired(boolean required) {
		property.setRequired(required);
		setDynamicAttribute(null, "required", required?"yes":"no");
    }


	/**
	 * @see javax.servlet.jsp.tagext.Tag#doStartTag()
	*/
	public int doStartTag() throws PageException	{
		if(pageContext.variablesScope() instanceof ComponentScope) {
			ComponentImpl comp = ((ComponentScope)pageContext.variablesScope()).getComponent();
			
			comp.setProperty(property);
		}
		
		return SKIP_BODY;
	}

	/**
	* @see javax.servlet.jsp.tagext.Tag#doEndTag()
	*/
	public int doEndTag()	{
		return EVAL_PAGE;
	}
}