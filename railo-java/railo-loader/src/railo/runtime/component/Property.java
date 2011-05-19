package railo.runtime.component;

import java.io.Serializable;

import railo.runtime.type.Struct;


/**
 * FUTURE add a interface to public interface
 */
public interface Property extends Serializable,Member {
    

	/**
	 * @return the _default
	 */
	public String getDefault();

	/**
	 * @param _default the _default to set
	 */
	public void setDefault(String _default);

	/**
	 * @return the displayname
	 */
	public String getDisplayname();
	

	/**
	 * @param displayname the displayname to set
	 */
	public void setDisplayname(String displayname);

	/**
	 * @return the hint
	 */
	public String getHint();

	/**
	 * @param hint the hint to set
	 */
	public void setHint(String hint);

	/**
	 * @return the name
	 */
	public String getName();

	/**
	 * @param name the name to set
	 */
	public void setName(String name);

	/**
	 * @return the required
	 */
	public boolean isRequired();

	/**
	 * @param required the required to set
	 */
	public void setRequired(boolean required);

	/**
	 * @return the type
	 */
	public String getType();

	/**
	 * @param type the type to set
	 */
	public void setType(String type);

	/**
	 *
	 * @see railo.runtime.component.Member#getValue()
	 */
	public Object getValue();

	
	/**
	 * @return the setter
	 */
	public boolean getSetter();

	/**
	 * @param setter the setter to set
	 */
	public void setSetter(boolean setter);

	/**
	 * @return the getter
	 */
	public boolean getGetter();

	/**
	 * @param getter the getter to set
	 */
	public void setGetter(boolean getter);
	
	

	public Object getMetaData();
	
	public Struct getMeta();

	/**
	 * @see railo.transformer.bytecode.util.ASMProperty#getClazz()
	 */
	public Class getClazz();

	public boolean isPeristent();

	public void setOwnerName(String ownerName);
	
	public String getOwnerName();

}