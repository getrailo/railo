package railo.runtime.interpreter.ref;

import railo.runtime.exp.PageException;

/**
 * Define a Reference to a Object
 */
public interface Ref {
	
    /**
     * return the value for that the reference is for
     * @return value to reference
     * @throws PageException
     */
    public Object touchValue() throws PageException;

    /**
     * return the value for that the reference is for
     * @return value to reference
     * @throws PageException
     */
    public Object getValue() throws PageException;

    /**
     * return the value for that the reference is for
     * @return value to reference
     * @throws PageException
     */
    public Object getCollection() throws PageException;
    
	/**
	 * return the name name of a reference
	 * @return type as string
	 */
	public String getTypeName();
	

	public boolean eeq(Ref other) throws PageException;
}
