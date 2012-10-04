package railo.runtime.util;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.type.Collection;
import railo.runtime.type.Struct;

/**
 * Variable Util
 */
public interface VariableUtil {

    /**
     * return a property from the given Object, when property doesn't exists return null
     * @param pc
     * @param coll Collection to check
     * @param key to get from Collection
     * @return value or null
     */
    public abstract Object getCollection(PageContext pc, Object coll,
            String key, Object defaultValue);

    /**
     * return a property from the given Object, when property doesn't exists return null
     * @param pc
     * @param coll Collection to check
     * @param key to get from Collection
     * @return value or null
     * @deprecated use instead <code>get(PageContext pc, Object coll, Collection.Key key, Object defaultValue);</code>
     */
    public abstract Object get(PageContext pc, Object coll, String key, Object defaultValue);
    
    /**
     * return a property from the given Object, when property doesn't exists return null
     * @param pc
     * @param coll Collection to check
     * @param key to get from Collection
     * @return value or null
     */
    public abstract Object get(PageContext pc, Object coll, Collection.Key key, Object defaultValue);

    /**
     * return a property from the given Object, when property doesn't exists return null
     * @param pc
     * @param coll Collection to check
     * @param key to get from Collection
     * @return value or null
     */
    public abstract Object getLight(PageContext pc, Object coll, String key, Object defaultValue);

    /**
     * return a property from the given Object, when coll is a query return a Column,when property doesn't exists throw exception
     * @param pc
     * @param coll Collection to check
     * @param key to get from Collection
     * @return value value to get
     * @throws PageException
     */
    public abstract Object getCollection(PageContext pc, Object coll, String key)
            throws PageException;

    /**
     * return a property from the given Object, when property doesn't exists throw exception
     * @param pc
     * @param coll Collection to check
     * @param key to get from Collection
     * @return value value to get
     * @throws PageException
     */
    public abstract Object get(PageContext pc, Object coll, String key)
            throws PageException;

    /**
     * sets a value to the Object
     * @param pc
     * @param coll Collection to check
     * @param key to get from Collection
     * @param value Value to set
     * @return value setted
     * @throws PageException
     */
    public abstract Object set(PageContext pc, Object coll, String key,
            Object value) throws PageException;

    /**
     * sets a value to the Object
     * @param pc
     * @param coll Collection to check
     * @param key to get from Collection
     * @param value Value to set
     * @return value setted or null if can't set
     * @deprecated use instead <code>setEL(PageContext pc, Object coll, Collection.Key key,Object value);</code>
     */
    public abstract Object setEL(PageContext pc, Object coll, String key,Object value);
    
    /**
     * sets a value to the Object
     * @param pc
     * @param coll Collection to check
     * @param key to get from Collection
     * @param value Value to set
     * @return value setted or null if can't set
     */
    public abstract Object setEL(PageContext pc, Object coll, Collection.Key key,Object value);

    /**
     * remove value from Collection
     * @param coll
     * @param key
     * @return has cleared or not
     */
    public abstract Object removeEL(Object coll, String key);

    /**
     * clear value from Collection
     * @param coll
     * @param key
     * @return has cleared or not
     * @throws PageException
     */
    public abstract Object remove(Object coll, String key) throws PageException;

    /**
     * call a Function (UDF, Method) with or witout named values
     * @param pc 
     * @param coll Collection of the UDF Function
     * @param key name of the function
     * @param args arguments to call the function
     * @return return value of the function
     * @throws PageException
     */
    public abstract Object callFunction(PageContext pc, Object coll,
            String key, Object[] args) throws PageException;

    /**
     * call a Function (UDF, Method) without Named Values
     * @param pc 
     * @param coll Collection of the UDF Function
     * @param key name of the function
     * @param args arguments to call the function
     * @return return value of the function
     * @throws PageException
     * @deprecated use instead <code>callFunctionWithoutNamedValues(PageContext pc, Object coll, Collection.Key key, Object[] args)</code> 
     */
    public abstract Object callFunctionWithoutNamedValues(PageContext pc,
            Object coll, String key, Object[] args) throws PageException;
    
    /**
     * call a Function (UDF, Method) without Named Values
     * @param pc 
     * @param coll Collection of the UDF Function
     * @param key name of the function
     * @param args arguments to call the function
     * @return return value of the function
     * @throws PageException
     */
    public Object callFunctionWithoutNamedValues(PageContext pc, 
    		Object coll, Collection.Key key, Object[] args) throws PageException;

    /**
     * call a Function (UDF, Method) with Named Values
     * @param pc 
     * @param coll Collection of the UDF Function
     * @param key name of the function
     * @param args arguments to call the function
     * @return return value of the function
     * @throws PageException
     * @deprecated use instead <code>callFunctionWithNamedValues(PageContext pc, Object coll, Collection.Key key, Object[] args)</code>
     */
    public abstract Object callFunctionWithNamedValues(PageContext pc,
            Object coll, String key, Object[] args) throws PageException;
    
    

    /**
     * call a Function (UDF, Method) with Named Values
     * @param pc 
     * @param coll Collection of the UDF Function
     * @param key name of the function
     * @param args arguments to call the function
     * @return return value of the function
     * @throws PageException
     */
    public Object callFunctionWithNamedValues(PageContext pc, 
			Object coll, Collection.Key key, Object[] args) throws PageException;
    
    public Object callFunctionWithNamedValues(PageContext pc, 
    		Object coll, Collection.Key key, Struct args) throws PageException;

}