package railo.runtime.ext.tag;

import railo.runtime.type.Collection;


/**
 * Interface for Dynmaic Attributes for tags (in j2ee at version 1.4.x)
 */
public interface DynamicAttributes {
	
	/**
	 * @param uri the namespace of the attribute, or null if in the default namespace.
	 * @param localName the name of the attribute being set.
	 * @param value the value of the attribute
	 * @deprecated use instead <code>setDynamicAttribute(String uri, Collection.Key localName, Object value)</code>
	 */
	public void setDynamicAttribute(String uri, String localName, Object value);
	
	/**
	 * @param uri the namespace of the attribute, or null if in the default namespace.
	 * @param localName the name of the attribute being set.
	 * @param value the value of the attribute
	 */
	public void setDynamicAttribute(String uri, Collection.Key localName, Object value);

}