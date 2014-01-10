package railo.runtime.ext.tag;

import railo.runtime.tag.MissingAttribute;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;

public class TagMetaDataAttrImpl extends MissingAttribute implements TagMetaDataAttr { 
	private String description;
	private boolean required;
	private boolean isRuntimeExpressionValue;
	private String defaultValue;
	
	/**
	 * Constructor of the class
	 * @param name
	 * @param required
	 * @param type
	 */
	public TagMetaDataAttrImpl(String name, boolean required, String type, boolean isRuntimeExpressionValue,String defaultValue,String description) {
		this(KeyImpl.getInstance(name),required,type,isRuntimeExpressionValue,defaultValue,description);
	}
	
	/**
	 * Constructor of the class
	 * @param name
	 * @param required
	 * @param type
	 * @param description
	 */
	public TagMetaDataAttrImpl(Collection.Key name, boolean required, String type, boolean isRuntimeExpressionValue,String defaultValue, String description) {
		super(name,type);
		this.required = required;
		this.description = description;
		this.defaultValue = defaultValue;
		this.isRuntimeExpressionValue = isRuntimeExpressionValue;
	}

	@Override
	public String getDescription() {
		return description;
	}


	@Override
	public boolean isRequired() {
		return required;
	}

	@Override
	public boolean isRuntimeExpressionValue() {
		return isRuntimeExpressionValue;
	}

	@Override
	public String getDefaultVaue() {
		return defaultValue;
	}
}
