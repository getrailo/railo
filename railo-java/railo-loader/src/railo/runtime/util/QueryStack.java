package railo.runtime.util;

import railo.runtime.PageContext;
import railo.runtime.type.Collection;
import railo.runtime.type.Query;
import railo.runtime.type.QueryColumn;

/**
 * Query Stack
 */
public interface QueryStack {

    /**
     * adds a Query to the Stack
     * @param query 
     */
    public abstract void addQuery(Query query);

    /**
     * removes a Query from Stack
     */
    public abstract void removeQuery();

    /**
     * @return returns if stack is empty or not
     */
    public abstract boolean isEmpty();

    /**
     * loop over all Queries and return value at first ocurrence
     * @param key column name of the value to get
     * @return value
     * @deprecated use instead <code>{@link #getDataFromACollection(PageContext,String)}</code>
     */
    public abstract Object getDataFromACollection(String key);
    
    /**
     * loop over all Queries and return value at first ocurrence
     * @param key column name of the value to get
     * @return value
     */
    public abstract Object getDataFromACollection(PageContext pc,String key);

    /**
     * loop over all Queries and return value at first ocurrence
     * @param key column name of the value to get
     * @return value
     * @deprecated use instead <code>{@link #getDataFromACollection(PageContext,Collection.Key)}</code>
     */
    public abstract Object getDataFromACollection(Collection.Key key);
    
    /**
     * loop over all Queries and return value at first ocurrence
     * @param key column name of the value to get
     * @return value
     */
    public abstract Object getDataFromACollection(PageContext pc,Collection.Key key);

    /**
     * loop over all Queries and return value as QueryColumn at first ocurrence
     * @param key column name of the value to get
     * @return value
     */
    public abstract QueryColumn getColumnFromACollection(String key);

    /**
     * loop over all Queries and return value as QueryColumn at first ocurrence
     * @param key column name of the value to get
     * @return value
     */
    public abstract QueryColumn getColumnFromACollection(Collection.Key key);

    /**
     * clear the collection stack
     */
    public abstract void clear();
    
    /**
     * @return returns all queries in the stack
     */
    public Query[] getQueries();

}