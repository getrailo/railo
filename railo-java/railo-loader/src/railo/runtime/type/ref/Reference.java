package railo.runtime.type.ref;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.type.Collection;
/**
 * 
 */
public interface Reference {

	/**
	 * @return returns the value of the Variable
	 * @throws PageException
	 * @deprecated use instead <code>{@link #getKey()}</code>
	 */
	public abstract String getKeyAsString() throws PageException;

	/**
	 * @return returns the value of the Variable
	 * @throws PageException
	 */
	public abstract Collection.Key getKey() throws PageException;

    /**
     * @param pc PageContext of the actuell Request
     * @return returns the value of the Variable
     * @throws PageException
     */
    public abstract Object get(PageContext pc) throws PageException;

    /**
     * @param pc PageContext of the actuell Request
     * @return returns the value of the Variable
     * @throws PageException
     */
    public abstract Object get(PageContext pc, Object defaultValue);

	/**
	 * @param pc PageContext of the actuell Request
	 * @param value resets the value of the variable
	 * @return new Value setted
	 * @throws PageException
	 */
	public abstract Object set(PageContext pc,Object value) throws PageException;

	/**
	 * @param pc PageContext of the actuell Request
	 * @param value resets the value of the variable
	 * @return new Value setted
	 * @throws PageException
	 */
	public abstract Object setEL(PageContext pc,Object value);

    /**
     * clears the variable from collection
     * @param pc 
     * @return removed Object
     * @throws PageException
     */
    public abstract Object remove(PageContext pc) throws PageException;

    /**
     * clears the variable from collection
     * @param pc 
     * @return removed Object
     * @throws PageException
     */
    public abstract Object removeEL(PageContext pc);

    /**
     * create it when not exists
     * @param pc 
     * @return removed Object
     * @throws PageException
     */
    public abstract Object touch(PageContext pc) throws PageException;

    /**
     * create it when not exists
     * @param pc 
     * @return removed Object
     * @throws PageException
     */
    public abstract Object touchEL(PageContext pc);
    
	/**
	 * @return returns the collection
	 */
	public abstract Object getParent();
}