package railo.runtime.component;

import java.util.Iterator;

import org.objectweb.asm.Type;

import railo.commons.lang.StringUtil;
import railo.runtime.Component;
import railo.runtime.converter.ConverterException;
import railo.runtime.converter.ScriptConverter;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.transformer.bytecode.util.ASMProperty;
import railo.transformer.bytecode.util.ASMUtil;

/**
 * FUTURE add a interface to public interface
 */
public final class Property extends MemberSupport implements ASMProperty {
    

	private static final Collection.Key PERSITENT = KeyImpl.getInstance("persistent");
	
	private String type="any";
	private String name;
	private boolean required;
	private boolean setter=true;
	private boolean getter=true;
	

	private String _default;
	private String displayname="";
	private String hint="";
	private Struct meta=new StructImpl();

	private String ownerName; 

	
	
	// ORM Attributes
	/*private int batchsize;
	private int cascade=HibernateConstants.CASCADE_NONE;
	private String catalog=null;
	private Component cfc=null;
	private int collectionType=HibernateConstants.COLLECTION_TYPE_ARRAY;
	private String column=null;
	private boolean constrained=false;
	private String dataType=null;
	private boolean dynamicInsert;
	private boolean dynamicUpdate;
	private String elementColumn;
	private String elementType;
	private String entityName;
	private int fetchBatchSize;
	private String fieldType;
	private String fkColumn;
	private String formula;
	private String generator;
	private boolean getter;*/
	
	public Property() {
		super(Component.ACCESS_REMOTE);
	}

	/**
	 * @return the _default
	 */
	public String getDefault() {
		return _default;
	}

	/**
	 * @param _default the _default to set
	 */
	public void setDefault(String _default) {
		this._default = _default;
	}

	/**
	 * @return the displayname
	 */
	public String getDisplayname() {
		return displayname;
	}

	/**
	 * @param displayname the displayname to set
	 */
	public void setDisplayname(String displayname) {
		this.displayname = displayname;
	}

	/**
	 * @return the hint
	 */
	public String getHint() {
		return hint;
	}

	/**
	 * @param hint the hint to set
	 */
	public void setHint(String hint) {
		this.hint = hint;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the required
	 */
	public boolean isRequired() {
		return required;
	}

	/**
	 * @param required the required to set
	 */
	public void setRequired(boolean required) {
		this.required = required;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	
	
	/*public int getBatchsize() {
		return batchsize;
	}
	public void setBatchsize(int batchsize) {
		this.batchsize = batchsize;
	}
	public int getCascade() {
		return cascade;
	}
	public void setCascade(int cascade) {
		this.cascade = cascade;
	}
	public String getCatalog() {
		return catalog;
	}
	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}
	public Component getCfc() {
		return cfc;
	}
	public void setCfc(Component cfc) {
		this.cfc = cfc;
	}
	public int getCollectionType() {
		return collectionType;
	}
	public void setCollectionType(int collectionType) {
		this.collectionType = collectionType;
	}
	public String getColumn() {
		if(StringUtil.isEmpty(column)) return getName();
		return column;
	}
	public void setColumn(String column) {
		this.column = column;
	}
	public boolean getConstrained() {
		return constrained;
	}
	public void setConstrained(boolean constrained) {
		this.constrained = constrained;
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public boolean getDynamicInsert() {
		return dynamicInsert;
	}
	public void setDynamicInsert(boolean dynamicInsert) {
		this.dynamicInsert = dynamicInsert;
	}
	public boolean getDynamicUpdate() {
		return dynamicUpdate;
	}
	public void setDynamicUpdate(boolean dynamicUpdate) {
		this.dynamicUpdate = dynamicUpdate;
	}
	public String getElementColumn() {
		return elementColumn;
	}
	public void setElementColumn(String elementColumn) {
		this.elementColumn = elementColumn;
	}
	public String getElementType() {
		return elementType;
	}
	public void setElementType(String elementType) {
		this.elementType = elementType;
	}
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}
	public String getEntityName() {
		return entityName;
	}
	public void setFetchBatchSize(int fetchBatchSize) {
		this.fetchBatchSize = fetchBatchSize;
	}
	public int getFetchBatchSize() {
		return fetchBatchSize;
	}
	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}
	public String getFieldType() {
		return fieldType;
	}
	public void setFormula(String formula) {
		this.formula = formula;
	}
	public String getFormula() {
		return formula;
	}
	public void setGenerator(String generator) {
		this.generator = generator;
	}
	public String getGenerator() {
		return generator;
	}
	public void setGetter(boolean getter) {
		this.getter = getter;
	}
	public boolean getGetter() {
		return getter;
	}
	public void setFkColumn(String fkColumn) {
		this.fkColumn = fkColumn;
	}
	public String getFkColumn() {
		if(StringUtil.isEmpty(fkColumn))
			return getColumn();
		return fkColumn;
	}*/
	
	
	
	
	
	
	
	
	

	/**
	 *
	 * @see railo.runtime.component.Member#getValue()
	 */
	public Object getValue() {
		return _default;
	}

	/**
	 *
	 * @throws PageException 
	 * @see railo.transformer.bytecode.util.ASMProperty#getASMType()
	 */
	public Type getASMType() throws PageException {
		return ASMUtil.toType(getType(), true);
	}
	
	/**
	 * @return the setter
	 */
	public boolean getSetter() {
		return setter;
	}

	/**
	 * @param setter the setter to set
	 */
	public void setSetter(boolean setter) {
		this.setter = setter;
	}

	/**
	 * @return the getter
	 */
	public boolean getGetter() {
		return getter;
	}

	/**
	 * @param getter the getter to set
	 */
	public void setGetter(boolean getter) {
		this.getter = getter;
	}
	
	

	public Object getMetaData() {
		Struct sct=new StructImpl();
		sct.setEL("name",name);
		if(!StringUtil.isEmpty(hint))sct.setEL("hint",hint);
		if(!StringUtil.isEmpty(displayname))sct.setEL("displayname",displayname);
		if(!StringUtil.isEmpty(type))sct.setEL("type",type);
		Iterator it = meta.keySet().iterator();
		Object key;
		while(it.hasNext()) {
			key=it.next();
			sct.setEL(key.toString(),meta.get(key));
		}
		return sct;
	}
	
	public Struct getMeta() {
		return meta;
	}

	/**
	 * @see railo.transformer.bytecode.util.ASMProperty#getClazz()
	 */
	public Class getClazz() {
		return null;
	}

	public boolean isPeristent() {
		return Caster.toBooleanValue(meta.get(PERSITENT,Boolean.TRUE),true);
	}

	public void setOwnerName(String ownerName) {
		this.ownerName=ownerName;
	}
	public String getOwnerName() {
		return ownerName;
	}

	
	
	/**
	 *
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String strMeta="";
		try{
		strMeta=new ScriptConverter().serialize(meta);
		}
		catch(ConverterException ce){}
		
		return "default:"+this._default+";displayname:"+this.displayname+";hint:"+this.hint+
		";name:"+this.name+";type:"+this.type+";ownerName:"+ownerName+";meta:"+strMeta+";";
	}
	
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(!(obj instanceof Property)) return false;
		Property other=(Property)obj;
		
		return toString().equals(other.toString());
	}
	



	
}