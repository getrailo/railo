package railo.runtime.type;
import java.util.List;

import railo.runtime.exp.PageException;

/**
 * 
 */
public interface Array extends Collection,Cloneable {
		
	/**
	 * return dimension of the array
	 * @return dimension of the array
	 */
	public int getDimension();
	
	/**
	 * return object a given position, key can only be a integer from 1 to array len
	 * @param key key as integer
	 * @return value at key position
	 */
	public Object get(int key, Object defaultValue);
	
	/**
	 * return object a given position, key can only be a integer from 1 to array len
	 * @param key key as integer
	 * @return value at key position
	 * @throws PageException
	 */
	public Object getE(int key) throws PageException;
	
	/**
	 * set value at defined position, on error return null
	 * @param key key of the new value
	 * @param value value to set
	 * @return setted value
	 */
	public Object setEL(int key, Object value);
	
	/**
	 * set value at defined position
	 * @param key 
	 * @param value
	 * @return defined value
	 * @throws PageException
	 */
	public Object setE(int key, Object value) throws PageException;
	
	/**
	 * @return return all array keys as int
	 */
	public int[] intKeys();
	
	/**
	 * insert a value add defined position 
	 * @param key position to insert
	 * @param value value to insert
	 * @return has done or not
	 * @throws PageException
	 */
	public boolean insert(int key, Object value) throws PageException;
	
	/**
	 * append a new value to the end of the array
	 * @param o value to insert
	 * @return inserted value
	 * @throws PageException
	 */
	public Object append(Object o) throws PageException;
	
	public Object appendEL(Object o);
	
	/**
	 * add a new value to the begin of the array
	 * @param o value to insert
	 * @return inserted value
	 * @throws PageException
	 */
	public Object prepend(Object o) throws PageException;
	
	/**
	 * resize array to defined size
	 * @param to new minimum size of the array
	 * @throws PageException
	 */
	public void resize(int to) throws PageException;

	/**
	 * sort values of a array
	 * @param sortType search type (text,textnocase,numeric)
	 * @param sortOrder (asc,desc)
	 * @throws PageException
	 */
	public void sort(String sortType, String sortOrder) throws PageException;
	/**
	 * @return return arra as native (Java) Object Array
	 */
	public Object[] toArray();
	
	/**
	 * @return return array as ArrayList
	 */
	//public ArrayList toArrayList();
	
	public List toList();

	
	/**
	 * removes a value ad defined key
	 * @param key key to remove
	 * @return retuns if value is removed or not
	 * @throws PageException
	*/
	public Object removeE(int key) throws PageException;	
	
	/**
	 * removes a value ad defined key
	 * @param key key to remove
	 * @return retuns if value is removed or not
	*/
	public Object removeEL(int key) ;
    
    /**
     * contains this key
     * @param key
     * @return returns if collection has a key with given name
     */
    public boolean containsKey(int key);
}