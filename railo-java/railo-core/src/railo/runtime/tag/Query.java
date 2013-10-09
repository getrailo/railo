package railo.runtime.tag;

import java.util.ArrayList;
import java.util.TimeZone;

import railo.commons.date.TimeZoneUtil;
import railo.commons.lang.ClassException;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.PageContextImpl;
import railo.runtime.config.ConfigImpl;
import railo.runtime.config.ConfigWebImpl;
import railo.runtime.config.Constants;
import railo.runtime.db.DataSource;
import railo.runtime.db.DatasourceConnection;
import railo.runtime.db.DatasourceManagerImpl;
import railo.runtime.db.HSQLDBHandler;
import railo.runtime.db.SQL;
import railo.runtime.db.SQLImpl;
import railo.runtime.db.SQLItem;
import railo.runtime.debug.DebuggerPro;
import railo.runtime.debug.DebuggerUtil;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.DatabaseException;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.tag.BodyTagTryCatchFinallyImpl;
import railo.runtime.listener.AppListenerUtil;
import railo.runtime.listener.ApplicationContextPro;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.orm.ORMSession;
import railo.runtime.orm.ORMUtil;
import railo.runtime.tag.util.DeprecatedUtil;
import railo.runtime.tag.util.QueryParamConverter;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.QueryColumn;
import railo.runtime.type.QueryImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.dt.DateTimeImpl;
import railo.runtime.type.dt.TimeSpan;
import railo.runtime.type.query.SimpleQuery;
import railo.runtime.type.util.KeyConstants;
import railo.runtime.type.util.ListUtil;



/**
* Passes SQL statements to a data source. Not limited to queries.
**/
public final class Query extends BodyTagTryCatchFinallyImpl {

	private static final Collection.Key SQL_PARAMETERS = KeyImpl.intern("sqlparameters");
	private static final Collection.Key CFQUERY = KeyImpl.intern("cfquery");
	private static final Collection.Key GENERATEDKEY = KeyImpl.intern("generatedKey");
	private static final Collection.Key MAX_RESULTS = KeyImpl.intern("maxResults");
	private static final Collection.Key TIMEOUT = KeyConstants._timeout;
	
	private static final int RETURN_TYPE_QUERY = 1;
	private static final int RETURN_TYPE_ARRAY_OF_ENTITY = 2;

	
	/** If specified, password overrides the password value specified in the data source setup. */
	private String password;

	/** The name of the data source from which this query should retrieve data. */
	private DataSource datasource=null;

	/** The maximum number of milliseconds for the query to execute before returning an error 
	** 		indicating that the query has timed-out. This attribute is not supported by most ODBC drivers. 
	** 		timeout is supported by the SQL Server 6.x or above driver. The minimum and maximum allowable values 
	** 		vary, depending on the driver. */
	private int timeout=-1;

	/** This is the age of which the query data can be */
	private TimeSpan cachedWithin;

	/** Specifies the maximum number of rows to fetch at a time from the server. The range is 1, 
	** 		default to 100. This parameter applies to ORACLE native database drivers and to ODBC drivers. 
	** 		Certain ODBC drivers may dynamically reduce the block factor at runtime. */
	private int blockfactor=-1;

	/** The database driver type. */
	private String dbtype;

	/** Used for debugging queries. Specifying this attribute causes the SQL statement submitted to the 
	** 		data source and the number of records returned from the query to be returned. */
	private boolean debug=true;

	/* This is specific to JTags, and allows you to give the cache a specific name */
	//private String cachename;

	/** Specifies the maximum number of rows to return in the record set. */
	private int maxrows=-1;

	/** If specified, username overrides the username value specified in the data source setup. */
	private String username;

	/**  */
	private DateTime cachedafter;

	/** The name query. Must begin with a letter and may consist of letters, numbers, and the underscore 
	** 		character, spaces are not allowed. The query name is used later in the page to reference the query's 
	** 		record set. */
	private String name;
	
	private String result=null;

	//private static HSQLDBHandler hsql=new HSQLDBHandler();
	
	private boolean orgPSQ;
	private boolean hasChangedPSQ;
	
	ArrayList<SQLItem> items=new ArrayList<SQLItem>();
	
	private boolean clearCache;
	private boolean unique;
	private Struct ormoptions;
	private int returntype=RETURN_TYPE_ARRAY_OF_ENTITY;
	private TimeZone timezone;
	private TimeZone tmpTZ;
	private boolean lazy;
	private Object params;
	
	
	
	@Override
	public void release()	{
		super.release();
		items.clear();
		password=null;
		datasource=null;
		timeout=-1;
		clearCache=false;
		cachedWithin=null;
		cachedafter=null;
		//cachename="";
		blockfactor=-1;
		dbtype=null;
		debug=true;
		maxrows=-1;
		username=null;
		name="";
		result=null;

		orgPSQ=false;
		hasChangedPSQ=false;
		unique=false;
		
		ormoptions=null;
		returntype=RETURN_TYPE_ARRAY_OF_ENTITY;
		timezone=null;
		tmpTZ=null;
		lazy=false;
		params=null;
	}
	
	
	public void setOrmoptions(Struct ormoptions) {
		this.ormoptions = ormoptions;
	}


	public void setReturntype(String strReturntype) throws ApplicationException {
		if(StringUtil.isEmpty(strReturntype)) return;
		strReturntype=strReturntype.toLowerCase().trim();
		
		if(strReturntype.equals("query"))
			returntype=RETURN_TYPE_QUERY;
		    //mail.setType(railo.runtime.mail.Mail.TYPE_TEXT);
		else if(strReturntype.equals("array_of_entity") || strReturntype.equals("array-of-entity") || 
				strReturntype.equals("array_of_entities") || strReturntype.equals("array-of-entities") || 
				strReturntype.equals("arrayofentities") || strReturntype.equals("arrayofentities"))
			returntype=RETURN_TYPE_ARRAY_OF_ENTITY;
		    //mail.setType(railo.runtime.mail.Mail.TYPE_TEXT);
		else
			throw new ApplicationException("attribute returntype of tag query has an invalid value","valid values are [query,array-of-entity] but value is now ["+strReturntype+"]");
	}


	public void setUnique(boolean unique) {
		this.unique = unique;
	}
	/**
	 * @param result the result to set
	 */
	public void setResult(String result) {
		this.result = result;
	}

	/**
	 * @param psq set preserver single quote
	 */
	public void setPsq(boolean psq)	{
		orgPSQ=pageContext.getPsq();
        if(orgPSQ!=psq){
        	pageContext.setPsq(psq);
        	hasChangedPSQ=true;
        }
	}
	
	/** set the value password
	*  If specified, password overrides the password value specified in the data source setup.
	* @param password value to set
	**/
	public void setPassword(String password)	{
		this.password=password;
	}

	/** set the value datasource
	*  The name of the data source from which this query should retrieve data.
	* @param datasource value to set
	 * @throws ClassException 
	**/

	public void setDatasource(Object datasource) throws PageException, ClassException	{
		if (Decision.isStruct(datasource)) {
			this.datasource=AppListenerUtil.toDataSource("__temp__", Caster.toStruct(datasource));
		} 
		else if (Decision.isString(datasource)) {
			this.datasource=((PageContextImpl)pageContext).getDataSource(Caster.toString(datasource));
		} 
		else {
			throw new ApplicationException("attribute [datasource] must be datasource name or a datasource definition(struct)");
			
		}
	}

	/** set the value timeout
	*  The maximum number of milliseconds for the query to execute before returning an error 
	* 		indicating that the query has timed-out. This attribute is not supported by most ODBC drivers. 
	* 		timeout is supported by the SQL Server 6.x or above driver. The minimum and maximum allowable values 
	* 		vary, depending on the driver.
	* @param timeout value to set
	**/
	public void setTimeout(double timeout)	{
		this.timeout=(int)timeout;
	}

	/** set the value cachedafter
	*  This is the age of which the query data can be
	* @param cachedafter value to set
	**/
	public void setCachedafter(DateTime cachedafter)	{
		//railo.print.ln("cachedafter:"+cachedafter);
		this.cachedafter=cachedafter;
	}

	/** set the value cachename
	*  This is specific to JTags, and allows you to give the cache a specific name
	* @param cachename value to set
	**/
	public void setCachename(String cachename)	{
		DeprecatedUtil.tagAttribute(pageContext,"query", "cachename");
		//this.cachename=cachename;
	}

	/** set the value cachedwithin
	*  
	* @param cachedwithin value to set
	**/
	public void setCachedwithin(TimeSpan cachedwithin)	{
		if(cachedwithin.getMillis()>0)
			this.cachedWithin=cachedwithin;
		else clearCache=true;
	}
	
	public void setLazy(boolean lazy)	{
		this.lazy=lazy;
	}

	/** set the value providerdsn
	*  Data source name for the COM provider, OLE-DB only.
	* @param providerdsn value to set
	 * @throws ApplicationException
	**/
	public void setProviderdsn(String providerdsn) throws ApplicationException	{
		DeprecatedUtil.tagAttribute(pageContext,"Query", "providerdsn");
	}

	/** set the value connectstring
	* @param connectstring value to set
	 * @throws ApplicationException
	**/
	public void setConnectstring(String connectstring) throws ApplicationException	{
		DeprecatedUtil.tagAttribute(pageContext,"Query", "connectstring");
	}
	

	public void setTimezone(String timezone) throws ExpressionException	{
	    this.timezone=TimeZoneUtil.toTimeZone(timezone);
	}

	/** set the value blockfactor
	*  Specifies the maximum number of rows to fetch at a time from the server. The range is 1, 
	* 		default to 100. This parameter applies to ORACLE native database drivers and to ODBC drivers. 
	* 		Certain ODBC drivers may dynamically reduce the block factor at runtime.
	* @param blockfactor value to set
	**/
	public void setBlockfactor(double blockfactor)	{
		this.blockfactor=(int) blockfactor;
	}

	/** set the value dbtype
	*  The database driver type.
	* @param dbtype value to set
	**/
	public void setDbtype(String dbtype)	{
		this.dbtype=dbtype.toLowerCase();
	}

	/** set the value debug
	*  Used for debugging queries. Specifying this attribute causes the SQL statement submitted to the 
	* 		data source and the number of records returned from the query to be returned.
	* @param debug value to set
	**/
	public void setDebug(boolean debug)	{
		this.debug=debug;
	}

	/** set the value dbname
	*  The database name, Sybase System 11 driver and SQLOLEDB provider only. If specified, dbName 
	* 		overrides the default database specified in the data source.
	* @param dbname value to set
	 * @throws ApplicationException
	**/
	public void setDbname(String dbname) {
		DeprecatedUtil.tagAttribute(pageContext,"Query", "dbname");
	}

	/** set the value maxrows
	*  Specifies the maximum number of rows to return in the record set.
	* @param maxrows value to set
	**/
	public void setMaxrows(double maxrows)	{
		this.maxrows=(int) maxrows;
	}

	/** set the value username
	*  If specified, username overrides the username value specified in the data source setup.
	* @param username value to set
	**/
	public void setUsername(String username)	{
		if(!StringUtil.isEmpty(username))
			this.username=username;
	}

	/** set the value provider
	*  COM provider, OLE-DB only.
	* @param provider value to set
	 * @throws ApplicationException
	**/
	public void setProvider(String provider) {
		DeprecatedUtil.tagAttribute(pageContext,"Query", "provider");
	}

	/** set the value dbserver
	*  For native database drivers and the SQLOLEDB provider, specifies the name of the database server 
	* 		computer. If specified, dbServer overrides the server specified in the data source.
	* @param dbserver value to set
	 * @throws ApplicationException
	**/
	public void setDbserver(String dbserver) {
		DeprecatedUtil.tagAttribute(pageContext,"Query", "dbserver");
	}

	/** set the value name
	*  The name query. Must begin with a letter and may consist of letters, numbers, and the underscore 
	* 		character, spaces are not allowed. The query name is used later in the page to reference the query's 
	* 		record set.
	* @param name value to set
	**/
	public void setName(String name)	{
		this.name=name;
	}
	
	public String getName()	{
		return name==null? "query":name;
	}
	
	
	


    /**
     * @param item
     */
    public void setParam(SQLItem item) {
        items.add(item);
    }
    
    public void setParams(Object params) {
        this.params=params;
    }


	@Override
	public int doStartTag() throws PageException	{
		// default datasource
		if(datasource==null && (dbtype==null || !dbtype.equals("query"))){
			Object obj = ((ApplicationContextPro)pageContext.getApplicationContext()).getDefDataSource();
			if(StringUtil.isEmpty(obj))
				throw new ApplicationException(
						"attribute [datasource] is required, when attribute [dbtype] has not value [query] and no default datasource is defined",
						"you can define a default datasource as attribute [defaultdatasource] of the tag "+Constants.CFAPP_NAME+" or as data member of the "+Constants.APP_CFC+" (this.defaultdatasource=\"mydatasource\";)");
			
			datasource=obj instanceof DataSource?(DataSource)obj:((PageContextImpl)pageContext).getDataSource(Caster.toString(obj));
		}
		
		
		// timezone
		if(timezone!=null || (datasource!=null && (timezone=datasource.getTimeZone())!=null)) {
			tmpTZ=pageContext.getTimeZone();
			pageContext.setTimeZone(timezone);
		}
		
		
		return EVAL_BODY_BUFFERED;
	}
	
	@Override
	public void doFinally() {
		if(tmpTZ!=null) {
			pageContext.setTimeZone(tmpTZ);
		}
		super.doFinally();
	}

	@Override
	public int doEndTag() throws PageException	{		
		if(hasChangedPSQ)pageContext.setPsq(orgPSQ);
		String strSQL=bodyContent.getString();
		// no SQL String defined
		if(strSQL.length()==0) 
			throw new DatabaseException("no sql string defined, inside query tag",null,null,null);
		// cannot use attribute params and queryparam tag
		if(items.size()>0 && params!=null)
			throw new DatabaseException("you cannot use the attribute params and sub tags queryparam at the same time",null,null,null);
		// create SQL
		SQL sql;
		if(params!=null) {
			if(Decision.isArray(params))
				sql=QueryParamConverter.convert(strSQL, Caster.toArray(params));
			else if(Decision.isStruct(params))
				sql=QueryParamConverter.convert(strSQL, Caster.toStruct(params));
			else
				throw new DatabaseException("value of the attribute [params] has to be a struct or a array",null,null,null);
		}
		else sql=items.size()>0?new SQLImpl(strSQL,items.toArray(new SQLItem[items.size()])):new SQLImpl(strSQL);
		
		railo.runtime.type.Query query=null;
		long exe=0;
		boolean hasCached=cachedWithin!=null || cachedafter!=null;
		
		
		if(clearCache) {
			hasCached=false;
			pageContext.getQueryCache().remove(pageContext,sql,datasource!=null?datasource.getName():null,username,password);
		}
		else if(hasCached) {
			query=pageContext.getQueryCache().getQuery(pageContext,sql,datasource!=null?datasource.getName():null,username,password,cachedafter);
		}
		
		
		if(query==null) {
			if("query".equals(dbtype)) 		query=executeQoQ(sql);
			else if("orm".equals(dbtype) || "hql".equals(dbtype)) 	{
				long start=System.nanoTime();
				Object obj = executeORM(sql,returntype,ormoptions);
				
				if(obj instanceof railo.runtime.type.Query){
					query=(railo.runtime.type.Query) obj;
				}
				else {
					if(!StringUtil.isEmpty(name)) {
						pageContext.setVariable(name,obj);
					}
					if(result!=null){
						Struct sct=new StructImpl();
						sct.setEL(KeyConstants._cached, Boolean.FALSE);
						long time=System.nanoTime()-start;
						sct.setEL(KeyConstants._executionTime, Caster.toDouble(time/1000000));
						sct.setEL(KeyConstants._executionTimeNano, Caster.toDouble(time));
						sct.setEL(KeyConstants._SQL, sql.getSQLString());
						if(Decision.isArray(obj)){
							
						}
						else sct.setEL(KeyConstants._RECORDCOUNT, Caster.toDouble(1));
							
						pageContext.setVariable(result, sct);
					}
					else
						setExecutionTime((System.nanoTime()-start)/1000000);
					return EVAL_PAGE;
				}
			}
			else query=executeDatasoure(sql,result!=null,pageContext.getTimeZone());
			//query=(dbtype!=null && dbtype.equals("query"))?executeQoQ(sql):executeDatasoure(sql,result!=null);
			
			if(cachedWithin!=null) {
				DateTimeImpl cachedBefore = null;
				//if(cachedWithin!=null)
					cachedBefore=new DateTimeImpl(pageContext,System.currentTimeMillis()+cachedWithin.getMillis(),false);
	                pageContext.getQueryCache().set(pageContext,sql,datasource!=null?datasource.getName():null,username,password,query,cachedBefore);
                
                
			}
			exe=query.getExecutionTime();
		}
        else query.setCached(hasCached);
		
		if(pageContext.getConfig().debug() && debug) {
			boolean logdb=((ConfigImpl)pageContext.getConfig()).hasDebugOptions(ConfigImpl.DEBUG_DATABASE);
			if(logdb){
				boolean debugUsage=DebuggerUtil.debugQueryUsage(pageContext,query);
				((DebuggerPro)pageContext.getDebugger()).addQuery(debugUsage?query:null,datasource!=null?datasource.getName():null,name,sql,query.getRecordcount(),pageContext.getCurrentPageSource(),exe);
			}
		}
		
		if(!query.isEmpty() && !StringUtil.isEmpty(name)) {
			pageContext.setVariable(name,query);
		}
		
		// Result
		if(result!=null) {
			
			Struct sct=new StructImpl();
			sct.setEL(KeyConstants._cached, Caster.toBoolean(query.isCached()));
			if(!query.isEmpty())sct.setEL(KeyConstants._COLUMNLIST, ListUtil.arrayToList(query.getColumnNamesAsString(),","));
			int rc=query.getRecordcount();
			if(rc==0)rc=query.getUpdateCount();
			sct.setEL(KeyConstants._RECORDCOUNT, Caster.toDouble(rc));
			sct.setEL(KeyConstants._executionTime, Caster.toDouble(query.getExecutionTime()/1000000));
			sct.setEL(KeyConstants._executionTimeNano, Caster.toDouble(query.getExecutionTime()));
			
			sct.setEL(KeyConstants._SQL, sql.getSQLString());
			
			// GENERATED KEYS
			railo.runtime.type.Query qi = Caster.toQuery(query,null);
			if(qi !=null){
				railo.runtime.type.Query qryKeys = qi.getGeneratedKeys();
				if(qryKeys!=null){
					StringBuilder generatedKey=new StringBuilder(),sb;
					Collection.Key[] columnNames = qryKeys.getColumnNames();
					QueryColumn column;
					for(int c=0;c<columnNames.length;c++){
						column = qryKeys.getColumn(columnNames[c]);
						sb=new StringBuilder();
						int size=column.size();
						for(int row=1;row<=size;row++) {
							if(row>1)sb.append(',');
							sb.append(Caster.toString(column.get(row,null)));
						}
						if(sb.length()>0){
							sct.setEL(columnNames[c], sb.toString());
							if(generatedKey.length()>0)generatedKey.append(',');
							generatedKey.append(sb);
						}
					}
					if(generatedKey.length()>0)
						sct.setEL(GENERATEDKEY, generatedKey.toString());
				}
			}
			
			// sqlparameters
			SQLItem[] params = sql.getItems();
			if(params!=null && params.length>0) {
				Array arr=new ArrayImpl();
				sct.setEL(SQL_PARAMETERS, arr); 
				for(int i=0;i<params.length;i++) {
					arr.append(params[i].getValue());
					
				}
			}
			pageContext.setVariable(result, sct);
		}
		// cfquery.executiontime
		else {
			setExecutionTime(exe/1000000);
			
		}
		
		
		// listener
		((ConfigWebImpl)pageContext.getConfig()).getActionMonitorCollector()
			.log(pageContext, "query", "Query", exe, query);
		
		return EVAL_PAGE;
	}

	private void setExecutionTime(long exe) {
		Struct sct=new StructImpl();
		sct.setEL(KeyConstants._executionTime,new Double(exe));
		pageContext.undefinedScope().setEL(CFQUERY,sct);
	}


	private Object executeORM(SQL sql, int returnType, Struct ormoptions) throws PageException {
		ORMSession session=ORMUtil.getSession(pageContext);
		
		// params
		SQLItem[] _items = sql.getItems();
		Array params=new ArrayImpl();
		for(int i=0;i<_items.length;i++){
			params.appendEL(_items[i]);
		}
		
		// query options
		if(maxrows!=-1 && !ormoptions.containsKey(MAX_RESULTS)) ormoptions.setEL(MAX_RESULTS, new Double(maxrows));
		if(timeout!=-1 && !ormoptions.containsKey(TIMEOUT)) ormoptions.setEL(TIMEOUT, new Double(timeout));
		/* MUST
offset: Specifies the start index of the resultset from where it has to start the retrieval.
cacheable: Whether the result of this query is to be cached in the secondary cache. Default is false.
cachename: Name of the cache in secondary cache.
		 */
		Object res = session.executeQuery(pageContext,sql.getSQLString(),params,unique,ormoptions);
		if(returnType==RETURN_TYPE_ARRAY_OF_ENTITY) return res;
		return session.toQuery(pageContext, res, null);
		
	}
	
	public static Object _call(PageContext pc,String hql, Object params, boolean unique, Struct queryOptions) throws PageException {
		ORMSession session=ORMUtil.getSession(pc);
		if(Decision.isCastableToArray(params))
			return session.executeQuery(pc,hql,Caster.toArray(params),unique,queryOptions);
		else if(Decision.isCastableToStruct(params))
			return session.executeQuery(pc,hql,Caster.toStruct(params),unique,queryOptions);
		else
			return session.executeQuery(pc,hql,(Array)params,unique,queryOptions);
	}
	

	private railo.runtime.type.Query executeQoQ(SQL sql) throws PageException {
		try {
			return new HSQLDBHandler().execute(pageContext,sql,maxrows,blockfactor,timeout);
		} 
		catch (Exception e) {
			throw Caster.toPageException(e);
		} 
	}
	
	private railo.runtime.type.Query executeDatasoure(SQL sql,boolean createUpdateData,TimeZone tz) throws PageException {
		DatasourceManagerImpl manager = (DatasourceManagerImpl) pageContext.getDataSourceManager();
		DatasourceConnection dc=manager.getConnection(pageContext,datasource, username, password);
		
		try {
			if(lazy && !createUpdateData && cachedWithin==null && cachedafter==null && result==null)
				return new SimpleQuery(dc,sql,maxrows,blockfactor,timeout,getName(),pageContext.getCurrentPageSource().getDisplayPath(),tz);
			
			
			return new QueryImpl(pageContext,dc,sql,maxrows,blockfactor,timeout,getName(),pageContext.getCurrentPageSource().getDisplayPath(),createUpdateData,true);
		}
		finally {
			manager.releaseConnection(pageContext,dc);
		}
	}
	

	@Override
	public void doInitBody()	{
		
	}

	@Override
	public int doAfterBody()	{
		return SKIP_BODY;
	}
}