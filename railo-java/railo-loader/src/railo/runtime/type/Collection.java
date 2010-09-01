package railo.runtime.type;

import java.io.Serializable;

import railo.runtime.dump.Dumpable;
import railo.runtime.exp.PageException;
import railo.runtime.op.Castable;


/**
 * interface collection, used for all collection types of railo (array, struct, query)
 */
public interface Collection extends Dumpable, Iteratorable, Cloneable, Serializable, Castable  {
    
    
    
	/**
	 * @return the size of the collection
	 */
	public int size();

	/**
	 * @return returns a string array of all keys in the collection
	 */
	public Collection.Key[] keys();
	
	/**
	 * @return returns a string array of all keys in the collection
	 * @deprecated use instead <code>{@link #keys()}</code>
	*/
	public String[] keysAsString();
	
	/* *
	 * removes value from collection and return it when it exists, otherwise returns null
     * @param key key of the collection
	 * @return removed Object
	 * @throws PageException
     * @deprecated use instead <code>{@link #remove(railo.runtime.type.Collection.Key)()}</code>
	 */
   //public object remove(String key) throws PageException;
	
	/**
	 * removes value from collection and return it when it exists, otherwise throws a exception
	 * @param key key of the collection
	 * @return removed Object
	 * @throws PageException
     */
    public Object remove(Collection.Key key) throws PageException;
	

	/* *
	 * removes value from collection and return it when it exists, otherwise returns null
     * @param key key of the collection
	 * @return removed Object
     * @deprecated use instead <code>{@link #removeEL(railo.runtime.type.Collection.Key)()}</code>
	 */
    // public int removeEL(String key);
	

	/**
	 * removes value from collection and return it when it exists, otherwise returns null
	 * @param key key of the collection 
	 * @return removed Object
     */
    public Object removeEL(Collection.Key key);

	/**
	 * clears the collection
	 */
	public void clear();

	/**
	 * return a value from the collection
	 * @param key key of the value to get
	 * @return value on key position 
	 * @throws PageException
	 * @deprecated use instead <code>{@link #get(railo.runtime.type.Collection.Key)}</code>
	 */
	public Object get(String key) throws PageException;
	
	
	/**
	 * return a value from the collection
	 * @param key key of the value to get must be lower case
	 * @return value on key position 
	 * @throws PageException
	 */
	public Object get(Collection.Key key) throws PageException;
	
	/**
	* return a value from the collection, if key doesn't exist, dont throw a exception, reeturns null
	 * @param key key of the value to get
	 * @return value on key position or null
	 * @deprecated use instead <code>{@link #get(railo.runtime.type.Collection.Key, Object)}</code>
	 */
	public Object get(String key, Object defaultValue);
		
	/**
	 * return a value from the collection, if key doesn't exist, dont throw a exception, reeturns null
	 * @param key key of the value to get
	 * @return value on key position or null
	 */
	public Object get(Collection.Key key, Object defaultValue);
	
	/**
	 * sets a value to the collection
	 * @param key key of the new value
	 * @param value value to set 
	 * @return value setted
	 * @throws PageException
	 * @deprecated use instead <code>{@link #set(railo.runtime.type.Collection.Key, Object)}</code>
	 */
	public Object set(String key, Object value) throws PageException;
	
	/**
	 * sets a value to the collection
	 * @param key key of the new value 
	 * @param value value to set 
	 * @return value setted
	 * @throws PageException
	 */
	public Object set(Collection.Key key, Object value) throws PageException;
	
	/**
	* sets a value to the collection, if key doesn't exist, dont throw a exception, returns null
	 * @param key key of the value to get
	 * @param value value to set
	 * @return value on key position or null
	 * @deprecated use instead <code>{@link #setEL(railo.runtime.type.Collection.Key, Object)}</code>
	 */
	public Object setEL(String key, Object value);
	
	/**
	* sets a value to the collection, if key doesn't exist, dont throw a exception, returns null
	 * @param key key of the value to get
	 * @param value value to set
	 * @return value on key position or null
	 */
	public Object setEL(Collection.Key key, Object value);
	
	
	
	/**
	 * @return this object cloned
	 */
	public Object clone();
	
	public Collection duplicate(boolean deepCopy);
	
    /**
     * contains this key
     * @param key
     * @return returns if collection has a key with given name
     * @deprecated use instead <code>{@link #containsKey(railo.runtime.type.Collection.Key)}</code>
	 */
    //public String contains(String key);
    public boolean containsKey(String key);
	
    /**
     * contains this key
	 * @param key
     * @return returns if collection has a key with given name
     */
    public boolean containsKey(Collection.Key key);
    
    interface Key extends Serializable {

    	/**
    	 * return key as String
    	 */
    	public String getString();

    	/**
    	 * return key as lower case String
    	 */
    	public String getLowerString();
    	
    	/**
    	 * return key as upper case String
    	 */
    	public String getUpperString();
    	
    	
    	/**
    	 * return char at given position
    	 * @param index
    	 * @return character at given position
    	 */
    	public char charAt(int index);

    	/**
    	 * return lower case char a given position
    	 * @param index
    	 * @return lower case char from given position
    	 */
    	public char lowerCharAt(int index);

    	/**
    	 * return upper case char a given position
    	 * @param index
    	 * @return upper case char from given position
    	 */
    	public char upperCharAt(int index);
    	
    	/**
    	 * compare to object, ignore case of input
    	 * @param key
    	 * @return is equal to given key?
    	 */
    	public boolean equalsIgnoreCase(Collection.Key key);
    	
    	/**
    	 * @return return id for this key, this key is unique for the system but ignore case of input
    	 */
    	public int getId();
    }
}