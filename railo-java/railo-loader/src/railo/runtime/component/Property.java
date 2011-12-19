package railo.runtime.component;

import java.io.Serializable;

import railo.runtime.type.Struct;

/**
 * 
 */
public interface Property extends Serializable,Member {

	/**
	 * @return the _default
	 */
	public String getDefault();

	/**
	 * @return the displayname
	 */
	public String getDisplayname();

	/**
	 * @return the hint
	 */
	public String getHint();

	/**
	 * @return the name
	 */
	public String getName();

	/**
	 * @return the required
	 */
	public boolean isRequired();

	/**
	 * @return the type
	 */
	public String getType();

	/**
	 * @return the setter
	 */
	public boolean getSetter();

	/**
	 * @return the getter
	 */
	public boolean getGetter();

	public Object getMetaData();
	
	public Struct getMeta();

	public Class getClazz();

	public boolean isPeristent();

	public String getOwnerName();
	
	public Struct getDynamicAttributes();

}