package railo.runtime.type;

import java.util.Map;

import railo.runtime.exp.PageException;

/**
 * inteface for resultset (query) object
 */
public interface Query extends Collection, Iterator,com.allaire.cfx.Query {

	/**
	 * Constant <code>ORDER_ASC</code>, used for method sort
	 */
	public static final int ORDER_ASC=1;
	
	/**
	 * Constant <code>ORDER_DESC</code>, used for method sort
	 */
	public static final int ORDER_DESC=2;

	
	/**
	 * @return return how many lines are affected by a update/insert
	 */
	public int getUpdateCount();
	
	// FUTURE public Query getGeneratedKeys();
	
	/**
	 * return a value of the resultset by specifed colmn and row
	 * @param key column to get 
	 * @param row row to get from (1-recordcount)
	 * @return value at the called poition
	 * @throws PageException if invalid position definition
	 * @deprecated use instead <code>{@link #getAt(railo.runtime.type.Collection.Key, int)}</code>
	*/
	public Object getAt(String key,int row) throws PageException;
	
	/**
	 * return a value of the resultset by specifed colmn and row
	 * @param key column to get 
	 * @param row row to get from (1-recordcount)
	 * @return value at the called poition
	 * @throws PageException if invalid position definition
	 */
	public Object getAt(Collection.Key key,int row) throws PageException;
	
	/**
	 * return a value of the resultset by specifed colmn and row, otherwise to getAt this method throw no exception if value dont exist (return null)
	 * @param key column to get 
	 * @param row row to get from (1-recordcount)
	 * @return value at the called poition
	 * @deprecated use instead <code>{@link #getAt(railo.runtime.type.Collection.Key, int, Object)}</code>
	*/
	public Object getAt(String key,int row, Object defaultValue);
	
	/**
	 * return a value of the resultset by specifed colmn and row, otherwise to getAt this method throw no exception if value dont exist (return null)
	 * @param key column to get 
	 * @param row row to get from (1-recordcount)
	 * @return value at the called poition
	 */
	public Object getAt(Collection.Key key,int row, Object defaultValue);

    /**
     * set a value at the defined position
     * @param key column to set
     * @param row row to set
     * @param value value to fill
     * @return filled value
     * @throws PageException 
     * @deprecated use instead <code>{@link #setAtEL(railo.runtime.type.Collection.Key, int, Object)}</code>
	*/
    public Object setAt(String key,int row, Object value) throws PageException;

    /**
     * set a value at the defined position
     * @param key column to set
     * @param row row to set
     * @param value value to fill
     * @return filled value
     * @throws PageException 
     */
    public Object setAt(Collection.Key key,int row, Object value) throws PageException;

    /**
     * set a value at the defined position
     * @param key column to set
     * @param row row to set
     * @param value value to fill
     * @return filled value
     * @deprecated use instead <code>{@link #setAtEL(railo.runtime.type.Collection.Key, int, Object)}</code>
	*/
    public Object setAtEL(String key,int row, Object value);

    /**
     * set a value at the defined position
     * @param key column to set
     * @param row row to set
     * @param value value to fill
     * @return filled value
     */
    public Object setAtEL(Collection.Key key,int row, Object value);
    
	/**
	 * adds a new row to the resultset
	 * @param count count of rows to add
	 * @return return if row is addded or nod (always true)
	 */
	public boolean addRow(int count);
    
    /**
     * remove row from query
     * @param row
     * @return return new rowcount
     * @throws PageException 
     */
    public int removeRow(int row) throws PageException;
    
    /**
     * remove row from query
     * @param row
     * @return return new rowcount
     */
    public int removeRowEL(int row);
    
	/**
	 * adds a new column to the resultset
	 * @param columnName name of the new column
	 * @param content content of the new column inside a array (must have same size like query has records)
	 * @return if column is added return true otherwise false (always true, throw error when false)
	 * @throws PageException
	 * @deprecated use instead <code>{@link #addColumn(railo.runtime.type.Collection.Key, Array)}</code>
	*/
	public boolean addColumn(String columnName, Array content) throws PageException;
    
	/**
	 * adds a new column to the resultset
	 * @param columnName name of the new column
	 * @param content content of the new column inside a array (must have same size like query has records)
	 * @return if column is added return true otherwise false (always true, throw error when false)
	 * @throws PageException
	 */
	public boolean addColumn(Collection.Key columnName, Array content) throws PageException;
	
    /**
     * adds a new column to the resultset
	 * @param columnName name of the new column
	 * @param content content of the new column inside a array (must have same size like query has records)
     * @param type data type from (java.sql.Types)
	 * @return if column is added return true otherwise false (always true, throw error when false)
	 * @throws PageException
	 * @deprecated use instead <code>{@link #addColumn(railo.runtime.type.Collection.Key, Array, int)}</code>
	*/
    public boolean addColumn(String columnName, Array content, int type) throws PageException;
	
    /**
     * adds a new column to the resultset
	 * @param columnName name of the new column
	 * @param content content of the new column inside a array (must have same size like query has records)
     * @param type data type from (java.sql.Types)
	 * @return if column is added return true otherwise false (always true, throw error when false)
	 * @throws PageException
	 */
    public boolean addColumn(Collection.Key columnName, Array content, int type) throws PageException;
	
	/**
	 * @return Coloned Object
	 */
	public Object clone();
	

	/**
	 * @return return all types 
	 */
	public int[] getTypes();
	
	/**
	 * @return returns all types as Map (key==column)
	 */
	public Map getTypesAsMap();

    /**
     * return the query column matching to key
     * @param key key to get
     * @return QieryColumn object
     * @throws PageException
     * @deprecated use instead <code>{@link #getColumn(railo.runtime.type.Collection.Key)}</code>
	*/
    public QueryColumn getColumn(String key) throws PageException;

    /**
     * return the query column matching to key
     * @param key key to get
     * @return QieryColumn object
     * @throws PageException
     */
    public QueryColumn getColumn(Collection.Key key) throws PageException;

	/**
	 * return the query column matching to key, if key not exist return null
	 * @param key key to get
	 * @return QieryColumn object
	 * @deprecated use instead <code>{@link #getColumn(railo.runtime.type.Collection.Key, QueryColumn)}</code>
	*/
	public QueryColumn getColumn(String key,QueryColumn column);

	/**
	 * return the query column matching to key, if key not exist return null
	 * @param key key to get
	 * @return QieryColumn object
	 */
	public QueryColumn getColumn(Collection.Key key,QueryColumn column);

    /**
     * remove column matching to key
     * @param key key to remove
     * @return QueryColumn object removed
     * @throws PageException
     * @deprecated use instead <code>{@link #removeColumn(railo.runtime.type.Collection.Key)}</code>
	*/
    public QueryColumn removeColumn(String key) throws PageException;

    /**
     * remove column matching to key
     * @param key key to remove
     * @return QueryColumn object removed
     * @throws PageException
     */
    public QueryColumn removeColumn(Collection.Key key) throws PageException;


    /**
     * remove column matching to key
     * @param key key to remove
     * @return QueryColumn object removed or null if column not exists
     * @deprecated use instead <code>{@link #removeColumnEL(railo.runtime.type.Collection.Key)}</code>
	*/
    public QueryColumn removeColumnEL(String key);


    /**
     * remove column matching to key
     * @param key key to remove
     * @return QueryColumn object removed or null if column not exists
     */
    public QueryColumn removeColumnEL(Collection.Key key);

    
	/**
	 * @return returns the execution time
	 */
	public int executionTime();

	/**
	 * sets the execution Time of the query
	 * @param l
	 */
	public void setExecutionTime(long l);
	
	/**
	 * sorts a query by a column, direction is asc
	 * @param column colun to sort
	 * @throws PageException
	 * @deprecated use instead <code>{@link #sort(railo.runtime.type.Collection.Key)}</code>
	*/
	public void sort(String column) throws PageException;
	
	/**
	 * sorts a query by a column, direction is asc
	 * @param column colun to sort
	 * @throws PageException
	 */
	public void sort(Collection.Key column) throws PageException;

	/**
	 * sorts a query by a column 
	 * @param strColumn column to sort
	 * @param order sort type (Query.ORDER_ASC or Query.ORDER_DESC)
	 * @throws PageException
	 * @deprecated use instead <code>{@link #sort(railo.runtime.type.Collection.Key, int)}</code>
	*/
	public void sort(String strColumn, int order) throws PageException;

	/**
	 * sorts a query by a column 
	 * @param strColumn column to sort
	 * @param order sort type (Query.ORDER_ASC or Query.ORDER_DESC)
	 * @throws PageException
	 */
	public void sort(Collection.Key strColumn, int order) throws PageException;

    /**
     * sets if query is form cache or not
     * @param isCached is cached or not
     */
    public void setCached(boolean isCached);
    
    /**
     * is query from cache or not
     * @return is cached or not
     */
    public boolean isCached();

    /**
     * @return returns struct with meta data to the query
     */
    //public Struct getMetaData();
    
    /**
     * @return returns array with meta data to the query (only column names and type)
     */
    public Array getMetaDataSimple();

	
}