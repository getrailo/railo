package railo.runtime.type;

import railo.runtime.PageContext;
import railo.runtime.dump.Dumpable;
import railo.runtime.exp.PageException;
import railo.runtime.op.Castable;

/**
 * Hold a native or wild object, to use id inside railo runtime
 */
public interface Objects extends Dumpable,Castable	{
    
    /**
     * return property 
     * @param pc PageContext
     * @param key Name of the Property
     * @return return value of the Property
     */
    public Object get(PageContext pc, Collection.Key key, Object defaultValue);
    
    
    /**
     * return property or getter of the ContextCollection
     * @param pc PageContext
     * @param key Name of the Property
     * @return return value of the Property
     * @throws PageException
     */
    public Object get(PageContext pc, Collection.Key key) throws PageException;
    
    

    /**
     * sets a property (Data Member) value of the object
     * @param pc 
     * @param propertyName property name to set 
     * @param value value to insert
     * @return value set to property
     * @throws PageException
     */
    public Object set(PageContext pc, Collection.Key propertyName, Object value) throws PageException;


    /**
     * sets a property (Data Member) value of the object
     * @param pc 
     * @param propertyName property name to set 
     * @param value value to insert
     * @return value set to property
     */
    public Object setEL(PageContext pc, Collection.Key propertyName, Object value);

    /**
     * calls a method of the object
     * @param pc 
     * @param methodName name of the method to call
     * @param arguments arguments to call method with
     * @return return value of the method
     * @throws PageException
     */
    public Object call(PageContext pc, Collection.Key methodName, Object[] arguments) throws PageException;
    
    
    /**
     * call a method of the Object with named arguments
     * @param pc PageContext
     * @param methodName name of the method
     * @param args Named Arguments for the method
     * @return return result of the method
     * @throws PageException
     */
    public abstract Object callWithNamedValues(PageContext pc, Collection.Key methodName, Struct args) throws PageException;
}