package railo.runtime.cfx.customtag;

import railo.runtime.cfx.CFXTagException;

/**
 * interface for a CustomTag Class, a CustomTag Class is Base to generate a Custom Tag
 */
public interface CFXTagClass {
	
	
	/**
	 * @return return a New Instance
	 * @throws CFXTagException
	 */
	public Object newInstance() throws CFXTagException;
	
	/**
	 * @return returns if Tag is readOnly (for Admin)
	 */
	public boolean isReadOnly();

	/**
	 * @return returns a readonly copy of the tag
	 */
	public CFXTagClass cloneReadOnly();
	
	/**
	 * @return returns Type of the CFX Tag as String
	 */
	public String getDisplayType();
	
	/**
	 * @return returns the Source Name as String 
	 */
	public String getSourceName();

    /**
     * @return returns if tag is ok
     */
    public boolean isValid();
}