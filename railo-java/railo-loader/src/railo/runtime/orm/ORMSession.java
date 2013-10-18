package railo.runtime.orm;


import railo.runtime.Component;
import railo.runtime.PageContext;
import railo.runtime.db.DataSource;
import railo.runtime.exp.PageException;
import railo.runtime.type.Array;
import railo.runtime.type.Query;
import railo.runtime.type.Struct;

public interface ORMSession {

	/**
	 * flush all elements in session
	 * @param pc
	 * @throws PageException
	 */
	public void flush(PageContext pc) throws PageException;
	
	/**
	 * delete elememt from datasource
	 * @param name
	 * @throws PageException 
	 */
	public void delete(PageContext pc, Object obj) throws PageException;
	
	/**
	 * insert entity into datasource, even the entry already exist
	 * @param entity
	 * @throws PageException 
	 */
	public void save(PageContext pc, Object obj, boolean forceInsert) throws PageException;
	

	/**
	 * Reloads data for an entity that is already loaded. This method refetches data from the database and repopulates the entity with the refreshed data.
	 * @param obj 
	 */
	public void reload(PageContext pc, Object obj) throws PageException;

	/**
	 * creates a entity matching the given name
	 * @param entityName
	 * @return
	 */
	public Component create(PageContext pc, String entityName) throws PageException;
	
	/**
	 * Attaches the specified entity to the current ORM session. It copies the state of the given object onto the persistent object with the same identifier and returns the persistent object.
	 * If there is no persistent instance currently associated with the session, it is loaded. The given instance is not associated with the session. User have to use the returned object from this session.
	 * @param entity
	 * @return
	 * @throws PageException 
	 */
	public Component merge(PageContext pc, Object obj) throws PageException;
	
	/**
	 * clear the session
	 * @param pc
	 * @throws PageException
	 */
	public void clear(PageContext pc) throws PageException;
	
	/**
	 * load and return a Object that match given filter, if there is more than one Object matching the filter, only the first Object is returned
	 * @param name
	 * @param filter
	 * @return
	 */
	public Component load(PageContext pc, String name, Struct filter) throws PageException;
	
	public Query toQuery(PageContext pc, Object obj, String name) throws PageException;


	
	/**
	 * load and return a Object that match given id, if there is more than one Object matching the id, only the first Object is returned
	 * @param name
	 * @param id
	 */
	public Component load(PageContext pc, String name, String id) throws PageException;
	
	/**
	 * load and return a array of Objects matching given filter
	 * @param name
	 * @param filter
	 * @return
	 */
	public Array loadAsArray(PageContext pc, String name, Struct filter) throws PageException;
	
	
	/**
	 * load and return a array of Objects matching given filter
	 * @param name
	 * @param filter
	 * @param options
	 * @return
	 */
	public Array loadAsArray(PageContext pc, String name, Struct filter, Struct options)throws PageException;
	
	/**
	 * @param pc
	 * @param name
	 * @param filter
	 * @param options
	 * @param order
	 * @return
	 * @throws PageException
	 */
	public Array loadAsArray(PageContext pc, String name, Struct filter, Struct options, String order)throws PageException;

	/**
	 * load and return a array of Objects matching given id
	 * @param name
	 * @param id
	 */
	public Array loadAsArray(PageContext pc, String name, String id) throws PageException;
	
	/**
	 * @param pc
	 * @param name
	 * @param id
	 * @param order
	 * @return
	 * @throws PageException
	 */
	public Array loadAsArray(PageContext pc, String name, String id, String order) throws PageException;

	/**
	 * load and return a array of Objects matching given sampleEntity
	 * @param name
	 * @param id
	 */
	public Array loadByExampleAsArray(PageContext pc, Object obj) throws PageException;
	
	/**
	 * load and return a Object that match given sampleEntity, if there is more than one Object matching the id, only the first Object is returned
	 * @param name
	 * @param id
	 */
	public Component loadByExample(PageContext pc, Object obj) throws PageException;
	
	public void evictCollection(PageContext pc,String entity, String collection) throws PageException;
	
	public void evictCollection(PageContext pc,String entity, String collection, String id) throws PageException;

	public void evictEntity(PageContext pc,String entity) throws PageException;
	
	public void evictEntity(PageContext pc,String entity, String id) throws PageException;

	public void evictQueries(PageContext pc) throws PageException;
	
	public void evictQueries(PageContext pc,String cacheName) throws PageException;

	public Object executeQuery(PageContext pc,String hql, Array params, boolean unique,Struct queryOptions) throws PageException;

	public Object executeQuery(PageContext pc,String hql, Struct params, boolean unique,Struct queryOptions) throws PageException;

	/**
	 * close the session
	 * @param pc
	 * @throws PageException
	 */
	public void close(PageContext pc) throws PageException;
	
	/**
	 * is session valid or not
	 * @return is session valid
	 */
	public boolean isValid();
	
	/**
	 * engine from session
	 * @return engine
	 */
	public ORMEngine getEngine();
	
	public Object getRawSession();

	public ORMTransaction getTransaction(boolean autoManage);
	

	public DataSource getDataSource();

	public String[] getEntityNames(); 
}
