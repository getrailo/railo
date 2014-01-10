package railo.runtime.tag;

import railo.runtime.Component;
import railo.runtime.ComponentScope;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.tag.DynamicAttributes;
import railo.runtime.ext.tag.TagImpl;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.util.KeyConstants;

/**
* Defines components as complex types that are used for web services authoring. The attributes of this tag are exposed as component metadata and are subject to inheritance rules.
*
*
*
**/
public final class Property extends TagImpl  implements DynamicAttributes{
	
	private railo.runtime.component.PropertyImpl property=new railo.runtime.component.PropertyImpl();
	
	@Override
	public void release()	{
		super.release();
		property=new railo.runtime.component.PropertyImpl();
	}
	
	@Override
	public void setDynamicAttribute(String uri, String name, Object value) {
		property.getDynamicAttributes().setEL(KeyImpl.getInstance(name),value);
	}
	
	@Override
	public void setDynamicAttribute(String uri, Collection.Key name, Object value) {
		property.getDynamicAttributes().setEL(name,value);
	}
	public void setMetaData(String name, Object value) {
		property.getMeta().setEL(KeyImpl.getInstance(name),value);
	}
	
	/** set the value type
	*  A string; a property type name; data type.
	* @param type value to set
	**/
	public void setType(String type)	{
		property.setType(type);
		setDynamicAttribute(null, KeyConstants._type, type);
	}

	/** set the value name
	*  A string; a property name. Must be a static value.
	* @param name value to set
	**/
	public void setName(String name)	{
		// Fix for axis 1.4, axis can not handle when first char is upper case
		//name=StringUtil.lcFirst(name.toLowerCase());
		
		property.setName(name);
		setDynamicAttribute(null, KeyConstants._name, name);
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
		property.setAccess(access);
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

    public void setSetter(boolean setter) {
		property.setSetter(setter);
		setDynamicAttribute(null, "setter", setter?"yes":"no");
    }

    public void setGetter(boolean setter) {
		property.setGetter(setter);
		setDynamicAttribute(null, "getter", setter?"yes":"no");
    }
    
	@Override
	public int doStartTag() throws PageException	{
		if(pageContext.variablesScope() instanceof ComponentScope) {
			Component comp = ((ComponentScope)pageContext.variablesScope()).getComponent();
			comp.setProperty(property);
			property.setOwnerName(comp.getAbsName());
		}
		
		return SKIP_BODY;
	}

	@Override
	public int doEndTag()	{
		return EVAL_PAGE;
	}
}