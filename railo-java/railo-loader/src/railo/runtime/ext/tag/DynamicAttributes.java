package railo.runtime.ext.tag;


/**
 * Interface for Dynmaic Attributes for tags (in j2ee at version 1.4.x)
 */
public interface DynamicAttributes {
	
	/**
	 * @param uri the namespace of the attribute, or null if in the default namespace.
	 * @param localName the name of the attribute being set.
	 * @param value the value of the attribute
	 */
	public void setDynamicAttribute(String uri, String localName, Object value);

}