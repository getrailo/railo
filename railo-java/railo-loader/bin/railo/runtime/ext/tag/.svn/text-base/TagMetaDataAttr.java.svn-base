package railo.runtime.ext.tag;

import railo.runtime.type.Collection;

public interface TagMetaDataAttr {

	/**
	 * A description of the attribute.
	 * @return the description of the attribute
	 */
	public abstract String getDescription();

	/**
	 * The runtime type of the attribute's value 
	 * For example:String,Number,Boolean,Object,...
	 * @return the type of the attribute
	 */
	public abstract String getType();

	/**
	 * The unique name of the attribute being declared
	 * @return the name of the attribute
	 */
	public abstract Collection.Key getName();

	/**
	 * return the default value for this attribute or null if no default value is defined
	 * @return the default value of the attribute
	 */
	public abstract String getDefaultVaue();

	/**
	 * Whether the attribute is required.
	 * @return is required
	 */
	public abstract boolean isRequired();

	/**
	 * Whether the attribute's value can be dynamically calculated at runtime.
	 * @return is a runtime expression
	 */
	public boolean isRuntimeExpressionValue();
	
}