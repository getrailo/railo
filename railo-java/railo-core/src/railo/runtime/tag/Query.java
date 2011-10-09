package railo.runtime.tag;

import java.util.ArrayList;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.db.DataSourceManager;
import railo.runtime.db.DatasourceConnection;
import railo.runtime.db.HSQLDBHandler;
import railo.runtime.db.SQL;
import railo.runtime.db.SQLImpl;
import railo.runtime.db.SQLItem;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.DatabaseException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.tag.BodyTagTryCatchFinallyImpl;
import railo.runtime.listener.ApplicationContextPro;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.orm.ORMSession;
import railo.runtime.orm.ORMUtil;
import railo.runtime.tag.util.DeprecatedUtil;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.List;
import railo.runtime.type.QueryColumn;
import railo.runtime.type.QueryImpl;
import railo.runtime.type.QueryPro;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.dt.DateTimeImpl;
import railo.runtime.type.dt.TimeSpan;



/**
* Passes SQL statements to a data source. Not limited to queries.
**/
public final class Query extends BodyTagTryCatchFinallyImpl {

	private static final Collection.Key SQL_PARAMETERS = KeyImpl.intern("sqlparameters");
	private static final Collection.Key EXECUTION_TIME = KeyImpl.intern("executiontime");
	private static final Collection.Key CFQUERY = KeyImpl.intern("cfquery");
	private static final Collection.Key GENERATEDKEY = KeyImpl.intern("generatedKey");
	private static final Collection.Key MAX_RESULTS = KeyImpl.intern("maxResults");
	private static final Collection.Key TIMEOUT = KeyImpl.intern("timeout");
	
	private static final int RETURN_TYPE_QUERY = 1;
	private static final int RETURN_TYPE_ARRAY_OF_ENTITY = 2;

	
	/** If specified, password overrides the password value specified in the data source setup. */
	private String password;

	/** The name of the data source from which this query should retrieve data. */
	private String datasource=null;

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
	private boolean debug;

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
	
	
	
	/**
	* @see javax.servlet.jsp.tagext.Tag#release()
	*/
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
			throw new ApplicationException("attribute returntype of tag query has a invalid value","valid values are [query,array-of-entity] but value is now ["+strReturntype+"]");
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
	**/
	public void setDatasource(String datasource)	{
		this.datasource=datasource;
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
		DeprecatedUtil.tagAttribute("query", "cachename");
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

	/** set the value providerdsn
	*  Data source name for the COM provider, OLE-DB only.
	* @param providerdsn value to set
	 * @throws ApplicationException
	**/
	public void setProviderdsn(String providerdsn) throws ApplicationException	{
	    throw new ApplicationException("attribute providerdsn (with value ["+providerdsn+"]) is Deprecated");
	}

	/** set the value connectstring
	* @param connectstring value to set
	 * @throws ApplicationException
	**/
	public void setConnectstring(String connectstring) throws ApplicationException	{
	    throw new ApplicationException("attribute connectstring (with value ["+connectstring+"]) is Deprecated");
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
	public void setDbname(String dbname) throws ApplicationException	{
	    throw new ApplicationException("attribute dbname (with value ["+dbname+"]) is Deprecated");
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
	public void setProvider(String provider) throws ApplicationException	{
	    throw new ApplicationException("attribute provider (with value ["+provider+"]) is Deprecated");
	}

	/** set the value dbserver
	*  For native database drivers and the SQLOLEDB provider, specifies the name of the database server 
	* 		computer. If specified, dbServer overrides the server specified in the data source.
	* @param dbserver value to set
	 * @throws ApplicationException
	**/
	public void setDbserver(String dbserver) throws ApplicationException	{
	    throw new ApplicationException("attribute dbserver (with value ["+dbserver+"]) is Deprecated");
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


	/**
	* @see javax.servlet.jsp.tagext.Tag#doStartTag()
	*/
	public int doStartTag()	{
		return EVAL_BODY_BUFFERED;
	}

	/**
	* @throws PageException
	 * @see javax.servlet.jsp.tagext.Tag#doEndTag()
	*/
	public int doEndTag() throws PageException	{
		
		if(StringUtil.isEmpty(datasource) && (dbtype==null || !dbtype.equals("query"))){
			datasource=((ApplicationContextPro)pageContext.getApplicationContext()).getDefaultDataSource();
			if(StringUtil.isEmpty(datasource))
				throw new ApplicationException(
						"attribute [datasource] is required, when attribute [dbtype] has not value [query] and no default datasource is defined",
						"you can define a default datasource as attribute [defaultdatasource] of the tag cfapplication or as data member of the application.cfc (this.defaultdatasource=\"mydatasource\";)");
		}
		
		
		if(hasChangedPSQ)pageContext.setPsq(orgPSQ);
		String strSQL=bodyContent.getString();
		if(strSQL.length()==0) throw new DatabaseException("no sql string defined, inside query tag",null,null,null);
		SQL sql=items.size()>0?new SQLImpl(strSQL,(SQLItem[])items.toArray(new SQLItem[items.size()])):new SQLImpl(strSQL);
		
		railo.runtime.type.Query query=null;
		int exe=0;
		boolean hasCached=cachedWithin!=null || cachedafter!=null;
		
		
		if(clearCache) {
			hasCached=false;
			pageContext.getQueryCache().remove(sql,datasource,username,password);
		}
		else if(hasCached) {
			query=pageContext.getQueryCache().getQuery(sql,datasource,username,password,cachedafter);
		}
		
		
		if(query==null) {
			if("query".equals(dbtype)) 		query=executeQoQ(sql);
			else if("orm".equals(dbtype) || "hql".equals(dbtype)) 	{
				long start=System.currentTimeMillis();
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
						sct.setEL(QueryImpl.CACHED, Boolean.FALSE);
						sct.setEL(QueryImpl.EXECUTION_TIME, Caster.toDouble(System.currentTimeMillis()-start));
						sct.setEL(QueryImpl.SQL, sql.getSQLString());
						if(Decision.isArray(obj)){
							
						}
						else sct.setEL(QueryImpl.RECORDCOUNT, Caster.toDouble(1));
							
						pageContext.setVariable(result, sct);
					}
					else
						setExecutionTime(System.currentTimeMillis()-start);
					return EVAL_PAGE;
				}
			}
			else query=executeDatasoure(sql,result!=null);
			//query=(dbtype!=null && dbtype.equals("query"))?executeQoQ(sql):executeDatasoure(sql,result!=null);
			
			
			if(cachedWithin!=null) {
				DateTimeImpl cachedBefore = null;
				//if(cachedWithin!=null)
					cachedBefore=new DateTimeImpl(pageContext,System.currentTimeMillis()+cachedWithin.getMillis(),false);
	                pageContext.getQueryCache().set(sql,datasource,username,password,query,cachedBefore);
                
                
			}
			exe=query.executionTime();
		}
        else query.setCached(hasCached);
		
		
		if(pageContext.getConfig().debug() && debug) {
			pageContext.getDebugger().addQueryExecutionTime(datasource,name,sql,query.getRecordcount(),pageContext.getCurrentPageSource(),exe);
		}
		
		if(!query.isEmpty() && !StringUtil.isEmpty(name)) {
			pageContext.setVariable(name,query);
		}
		
		// Result
		if(result!=null) {
			
			Struct sct=new StructImpl();
			sct.setEL(QueryImpl.CACHED, Caster.toBoolean(query.isCached()));
			if(!query.isEmpty())sct.setEL(QueryImpl.COLUMNLIST, List.arrayToList(query.getColumns(),","));
			int rc=query.getRecordcount();
			if(rc==0)rc=query.getUpdateCount();
			sct.setEL(QueryImpl.RECORDCOUNT, Caster.toDouble(rc));
			sct.setEL(QueryImpl.EXECUTION_TIME, Caster.toDouble(query.executionTime()));
			sct.setEL(QueryImpl.SQL, sql.getSQLString());
			
			// GENERATED KEYS
			// FUTURE when getGeneratedKeys() exist in interface the toQueryImpl can be removed
			QueryPro qi = Caster.toQueryPro(query,null);
			if(qi !=null){
				QueryPro qryKeys = Caster.toQueryPro(qi.getGeneratedKeys(),null);
				if(qryKeys!=null){
					StringBuffer generatedKey=new StringBuffer(),sb;
					Collection.Key[] columnNames = qryKeys.getColumnNames();
					QueryColumn column;
					for(int c=0;c<columnNames.length;c++){
						column = qryKeys.getColumn(columnNames[c]);
						sb=new StringBuffer();
						int size=column.size();
						for(int r=1;r<=size;r++) {
							if(r>1)sb.append(',');
							sb.append(Caster.toString(column.get(r)));
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
			setExecutionTime(exe);
			
		}
		
		
		
		
		return EVAL_PAGE;
	}

	private void setExecutionTime(long exe) {
		Struct sct=new StructImpl();
		sct.setEL(EXECUTION_TIME,new Double(exe));
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
		//ORMEngine engine= ORMUtil.getEngine(pc);
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
	
	private railo.runtime.type.Query executeDatasoure(SQL sql,boolean createUpdateData) throws PageException {
		DataSourceManager manager = pageContext.getDataSourceManager();
		
		DatasourceConnection dc=manager.getConnection(pageContext,datasource, username, password);
		try {
			return new QueryImpl(dc,sql,maxrows,blockfactor,timeout,getName(),pageContext.getCurrentPageSource().getDisplayPath(),createUpdateData);
		}
		finally {
			manager.releaseConnection(pageContext,dc);
		}
	}
	

	/**
	* @see javax.servlet.jsp.tagext.BodyTag#doInitBody()
	*/
	public void doInitBody()	{
		
	}

	/**
	* @see javax.servlet.jsp.tagext.BodyTag#doAfterBody()
	*/
	public int doAfterBody()	{
		return SKIP_BODY;
	}

}