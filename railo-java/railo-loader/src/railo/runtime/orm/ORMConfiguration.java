package railo.runtime.orm;

import railo.commons.io.res.Resource;

public interface ORMConfiguration {
	public static final int DBCREATE_NONE=0;
	public static final int DBCREATE_UPDATE=1;
	public static final int DBCREATE_DROP_CREATE=2;


	public String hash();
	
	/**
	 * @return the autogenmap
	 */
	public boolean autogenmap();

	/**
	 * @return the catalog
	 */
	public String getCatalog();

	/**
	 * @return the cfcLocation
	 */
	public Resource[] getCfcLocations();
	
	public boolean isDefaultCfcLocation();

	/**
	 * @return the dbCreate
	 */
	public int getDbCreate();

	/**
	 * @return the dialect
	 */
	public String getDialect();

	/**
	 * @return the eventHandling
	 */
	public boolean eventHandling();

	public String eventHandler();

	public String namingStrategy();
	
	/**
	 * @return the flushAtRequestEnd
	 */
	public boolean flushAtRequestEnd();

	/**
	 * @return the logSQL
	 */
	public boolean logSQL();

	/**
	 * @return the saveMapping
	 */
	public boolean saveMapping();

	/**
	 * @return the schema
	 */
	public String getSchema();

	/**
	 * @return the secondaryCacheEnabled
	 */
	public boolean secondaryCacheEnabled();

	/**
	 * @return the sqlScript
	 */
	public Resource getSqlScript();

	/**
	 * @return the useDBForMapping
	 */
	public boolean useDBForMapping();

	/**
	 * @return the cacheConfig
	 */
	public Resource getCacheConfig();

	/**
	 * @return the cacheProvider
	 */
	public String getCacheProvider();

	/**
	 * @return the ormConfig
	 */
	public Resource getOrmConfig();

	public boolean skipCFCWithError();
	public boolean autoManageSession();




	public Object toStruct();


	
}