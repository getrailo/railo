package railo.runtime.tag;

import railo.runtime.ComponentPro;
import railo.runtime.ComponentScope;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.exp.PageRuntimeException;
import railo.runtime.ext.tag.DynamicAttributes;
import railo.runtime.ext.tag.TagImpl;
import railo.runtime.type.KeyImpl;

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
		property.getMeta().setEL(KeyImpl.init(name),value);
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
    
    /*public void setBatchsize(double batchsize) {
    	notSupported();
    	int ibs=Caster.toIntValue(batchsize);
		property.setBatchsize(ibs);
		setDynamicAttribute(null, "batchsize", Caster.toDouble(ibs));
    }

	public void setCascade(String cascade) throws ORMException {
		notSupported();
    	int cas=HibernateCaster.cascade(cascade);
		property.setCascade(cas);
		setDynamicAttribute(null, "cascade", cascade);
    }
	public void setCatalog(String catalog)  {
		notSupported();
    	property.setCatalog(catalog);
		setDynamicAttribute(null, "catalog", catalog);
    }
	public void setCfc(String cfcPath) throws PageException {
		notSupported();
		Component cfc = CreateObject.doComponent(pageContext, cfcPath);
		property.setCfc(cfc);
		setDynamicAttribute(null, "cfc", cfcPath);
    }
	
	public void setCollectiontype(String strCollectionType) throws ORMException  {
		strCollectionType=strCollectionType.trim().toLowerCase();
		notSupported();
		int collectionType=HibernateCaster.collectionType(strCollectionType);
    	property.setCollectionType(collectionType);
		setDynamicAttribute(null, "collectiontype", strCollectionType);
    }
	public void setConstrained(boolean constrained)  {
		notSupported();
    	property.setConstrained(constrained);
		setDynamicAttribute(null, "constrained", constrained?"yes":"no");
    }

	public void setDatatype(String dataType)  {
		notSupported();
    	property.setDataType(dataType);
		setDynamicAttribute(null, "datatype", dataType);
    }
	public void setDynamicinsert(boolean dynamicInsert)  {
		notSupported();
    	property.setDynamicInsert(dynamicInsert);
		setDynamicAttribute(null, "dynamicinsert", dynamicInsert?"yes":"no");
    }
	public void setDynamicupdate(boolean dynamicUpdate)  {
		notSupported();
    	property.setDynamicUpdate(dynamicUpdate);
		setDynamicAttribute(null, "dynamicupdate", dynamicUpdate?"yes":"no");
    }
	public void setElementcolumn(String elementColumn)  {
		notSupported();
    	property.setElementColumn(elementColumn);
		setDynamicAttribute(null, "elementcolumn", elementColumn);
    }
	public void setElementtype(String elementType)  {
		notSupported();
    	property.setElementType(elementType);
		setDynamicAttribute(null, "elementtype", elementType);
    }
	public void setEntityname(String entityName)  {
		notSupported();
    	property.setEntityName(entityName);
		setDynamicAttribute(null, "entityname", entityName);
    }
	public void setFetchbatchsize(double fetchBatchSize)  {
		int ifbs=Caster.toIntValue(fetchBatchSize);
		notSupported();
    	property.setFetchBatchSize(ifbs);
		setDynamicAttribute(null, "fetchbatchsize", Caster.toString(ifbs));
    }
	public void setFieldtype(String fieldType)  {
		notSupported();
    	property.setFieldType(fieldType);
		setDynamicAttribute(null, "fieldtype", fieldType);
    }
	public void setFkcolumn(String fkColumn)  {
		notSupported();
    	property.setFkColumn(fkColumn);
		setDynamicAttribute(null, "fkcolumn", fkColumn);
    }
	public void setFormula(String formula)  {
		notSupported();
    	property.setFormula(formula);
		setDynamicAttribute(null, "formula", formula);
    }
	public void setGenerator(String generator)  {
		notSupported();
    	property.setGenerator(generator);
		setDynamicAttribute(null, "generator", generator);
    }
	public void setGenerator(boolean getter)  {
		notSupported();
    	property.setGetter(getter);
		setDynamicAttribute(null, "getter", getter?"yes":"no");
    }*/
	
	
    
    private void notSupported() {
		throw new PageRuntimeException(new ApplicationException("this attribute is not supported yet"));
		
	}

	/**
	 * @see javax.servlet.jsp.tagext.Tag#doStartTag()
	*/
	public int doStartTag() throws PageException	{
		if(pageContext.variablesScope() instanceof ComponentScope) {
			ComponentPro comp = ((ComponentScope)pageContext.variablesScope()).getComponent();
			comp.setProperty(property);
			property.setOwnerName(comp.getAbsName());
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