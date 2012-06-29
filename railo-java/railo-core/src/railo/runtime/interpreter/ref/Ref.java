package railo.runtime.interpreter.ref;

import railo.runtime.PageContext;
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
    public Object touchValue(PageContext pc) throws PageException;

    /**
     * return the value for that the reference is for
     * @return value to reference
     * @throws PageException
     */
    public Object getValue(PageContext pc) throws PageException;

    /**
     * return the value for that the reference is for
     * @return value to reference
     * @throws PageException
     */
    public Object getCollection(PageContext pc) throws PageException;
    
	/**
	 * return the name name of a reference
	 * @return type as string
	 */
	public String getTypeName();
	

	public boolean eeq(PageContext pc,Ref other) throws PageException;
}
