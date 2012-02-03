package railo.runtime.type.scope.storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import railo.commons.io.log.Log;
import railo.runtime.PageContext;
import railo.runtime.config.Config;
import railo.runtime.config.ConfigImpl;
import railo.runtime.converter.ConverterException;
import railo.runtime.converter.ScriptConverter;
import railo.runtime.db.DataSourceImpl;
import railo.runtime.db.DatasourceConnection;
import railo.runtime.db.DatasourceConnectionPool;
import railo.runtime.db.SQL;
import railo.runtime.db.SQLCaster;
import railo.runtime.db.SQLImpl;
import railo.runtime.db.SQLItem;
import railo.runtime.db.SQLItemImpl;
import railo.runtime.debug.DebuggerImpl;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.DatabaseException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Query;
import railo.runtime.type.QueryImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.dt.DateTimeImpl;
import railo.runtime.type.scope.ScopeContext;

/**
 * client scope that store it's data in a datasource
 */
public abstract class StorageScopeDatasource extends StorageScopeImpl {

	private static final long serialVersionUID = 239179599401918216L;

	public static final Collection.Key EXPIRES = KeyImpl.intern("expires");

	public static final String PREFIX = "cf";
	
	private String datasourceName;

	private String appName;

	private String cfid;
	
	
	/**
	 * Constructor of the class
	 * @param pc
	 * @param name
	 * @param sct
	 * @param b 
	 */
	protected StorageScopeDatasource(PageContext pc,String datasourceName, String strType,int type,Struct sct) { 
		super(
				sct,
				type==SCOPE_CLIENT?doNowIfNull(pc,Caster.toDate(sct.get(TIMECREATED,null),false,pc.getTimeZone(),null)):null,
				doNowIfNull(pc,Caster.toDate(sct.get(LASTVISIT,null),false,pc.getTimeZone(),null)),
				-1, 
				type==SCOPE_CLIENT?Caster.toIntValue(sct.get(HITCOUNT,"1"),1):0,
				strType,type);

		this.datasourceName=datasourceName; 
		appName=pc.getApplicationContext().getName();
		cfid=pc.getCFID();
	}

	/**
	 * Constructor of the class, clone existing
	 * @param other
	 */
	protected StorageScopeDatasource(StorageScopeDatasource other,boolean deepCopy) {
		super(other,deepCopy);
		this.datasourceName=other.datasourceName;
	}
	
	private static DateTime doNowIfNull(PageContext pc,DateTime dt) {
		if(dt==null)return new DateTimeImpl(pc.getConfig());
		return dt;
	}
	
	
	
	
	protected static Struct _loadData(PageContext pc, String datasourceName,String strType,int type, Log log, boolean mxStyle) throws PageException	{
		DatasourceConnection dc=null;
		Query query=null;
	    
	    // select
	    SQL sqlSelect=mxStyle?
				new SQLImpl("mx"):
				new SQLImpl("select data from "+PREFIX+"_"+strType+"_data where cfid=? and name=?"
						,new SQLItem[]{
			 		new SQLItemImpl(pc.getCFID(),Types.VARCHAR),
					new SQLItemImpl(pc.getApplicationContext().getName(),Types.VARCHAR)
				});
		
		ConfigImpl config = (ConfigImpl)pc.getConfig();
		//int pid=1000;
		DatasourceConnectionPool pool = config.getDatasourceConnectionPool();
		try {
			dc=pool.getDatasourceConnection(pc,config.getDataSource(datasourceName),null,null);
			if(!((DataSourceImpl)dc.getDatasource()).isStorage()) 
				throw new ApplicationException("storage usage for this datasource is disabled, you can enable this in the railo administrator.");
			query = new QueryImpl(dc,sqlSelect,-1,-1,-1,"query");
		}
	    catch (DatabaseException de) {
	    	if(dc==null) throw de;
	    	ScopeContext.info(log,"create table "+PREFIX+"_"+strType+"_data in datasource ["+datasourceName+"]");
			try {
				new QueryImpl(dc,createSQL(dc,mxStyle,"text",strType),-1,-1,-1,"query");
	    	}
		    catch (DatabaseException _de) {
		    	try {
					new QueryImpl(dc,createSQL(dc,mxStyle,"memo",strType),-1,-1,-1,"query");
		    	}
			    catch (DatabaseException __de) {
			    	new QueryImpl(dc,createSQL(dc,mxStyle,"clob",strType),-1,-1,-1,"query");
			    }
		    }
	    	query = new QueryImpl(dc,sqlSelect,-1,-1,-1,"query");
		}
	    finally {
	    	if(dc!=null) pool.releaseDatasourceConnection(dc);
	    }
	    boolean debugUsage=DebuggerImpl.debugQueryUsage(pc,query);
	    ((DebuggerImpl)pc.getDebugger()).addQuery(debugUsage?query:null,datasourceName,"",sqlSelect,query.getRecordcount(),pc.getCurrentPageSource(),query.executionTime());
	    boolean _isNew = query.getRecordcount()==0;
	    
	    if(_isNew) {
	    	ScopeContext.info(log,"create new "+strType+" scope for "+pc.getApplicationContext().getName()+"/"+pc.getCFID()+" in datasource ["+datasourceName+"]");
			return null;
	    }
	    String str=Caster.toString(query.get(KeyImpl.DATA));
	    //long expires=Caster.toLongValue(query.get(EXPIRES));
	    if(mxStyle) return null;
	    //if(checkExpires && expires<=System.currentTimeMillis()) return null;
	    
	    Struct s = (Struct)pc.evaluate(str);
	    ScopeContext.info(log,"load existing data from ["+datasourceName+"."+PREFIX+"_"+strType+"_data] to create "+strType+" scope for "+pc.getApplicationContext().getName()+"/"+pc.getCFID());
		
	    return s;
	}

	/**
	 * @see railo.runtime.type.scope.storage.StorageScopeImpl#touchAfterRequest(railo.runtime.PageContext)
	 */
	public void touchAfterRequest(PageContext pc) {
		setTimeSpan(pc);
		super.touchAfterRequest(pc); 
		
		store(pc.getConfig());
	}
	
	public void store(Config config) {
		//if(!super.hasContent()) return;
		
		DatasourceConnection dc = null;
		ConfigImpl ci = (ConfigImpl)config;
		DatasourceConnectionPool pool = ci.getDatasourceConnectionPool();
		try {
			dc=pool.getDatasourceConnection(null,config.getDataSource(datasourceName),null,null);
			int recordsAffected = executeUpdate(config,dc.getConnection(),"update "+PREFIX+"_"+getTypeAsString()+"_data set expires=?,data=? where cfid=? and name=?",false);
		    if(recordsAffected>1) {
		    	executeUpdate(config,dc.getConnection(),"delete from "+PREFIX+"_"+getTypeAsString()+"_data where cfid=? and name=?",true);
		    	recordsAffected=0;
		    }
		    if(recordsAffected==0) {
		    	executeUpdate(config,dc.getConnection(),"insert into "+PREFIX+"_"+getTypeAsString()+"_data (expires,data,cfid,name) values(?,?,?,?)",false);
		    }

		} 
		catch (Exception e) {}
		finally {
			if(dc!=null) pool.releaseDatasourceConnection(dc);
		}
	}
	
	public void unstore(Config config) {
		ConfigImpl ci=(ConfigImpl) config;
		DatasourceConnection dc = null;
		
		
		DatasourceConnectionPool pool = ci.getDatasourceConnectionPool();
		try {
			dc=pool.getDatasourceConnection(null,config.getDataSource(datasourceName),null,null);
			executeUpdate(config,dc.getConnection(),"delete from "+PREFIX+"_"+getTypeAsString()+"_data where cfid=? and name=?",true);
		} 
		catch (Exception e) {}
		finally {
			if(dc!=null) pool.releaseDatasourceConnection(dc);
		}
	}
	
	

	private int executeUpdate(Config config,Connection conn, String strSQL, boolean ignoreData) throws SQLException, PageException, ConverterException {
		//String appName = pc.getApplicationContext().getName();
		SQLImpl sql = new SQLImpl(strSQL,new SQLItem[]{
				new SQLItemImpl(createExpires(getTimeSpan(), config),Types.VARCHAR),
				new SQLItemImpl(new ScriptConverter().serializeStruct(sct,ignoreSet),Types.VARCHAR),
				new SQLItemImpl(cfid,Types.VARCHAR),
				new SQLItemImpl(appName,Types.VARCHAR)
		});
		if(ignoreData)sql = new SQLImpl(strSQL,new SQLItem[]{
				new SQLItemImpl(cfid,Types.VARCHAR),
				new SQLItemImpl(appName,Types.VARCHAR)
			});
		
		return execute(conn, sql);
	}
	
	
	
	

	private static String createExpires(long timespan,Config config) {
		return Caster.toString(timespan+new DateTimeImpl(config).getTime());
	}

	private static int execute(Connection conn, SQLImpl sql) throws SQLException, PageException {
		PreparedStatement preStat = conn.prepareStatement(sql.getSQLString());
		int count=0;
		try {
			SQLItem[] items=sql.getItems();
		    for(int i=0;i<items.length;i++) {
	            SQLCaster.setValue(preStat,i+1,items[i]);
	        }
		    count= preStat.executeUpdate();
		}
		finally {
		    preStat.close();	
		}
	    return count;
	}

	private static SQL createSQL(DatasourceConnection dc, boolean mxStyle, String textType, String type) {
		String clazz = dc.getDatasource().getClazz().getName();
		
	    boolean isMSSQL=
	    	clazz.equals("com.microsoft.jdbc.sqlserver.SQLServerDriver") || 
	    	clazz.equals("net.sourceforge.jtds.jdbc.Driver");
	    boolean isHSQLDB=
	    	clazz.equals("org.hsqldb.jdbcDriver");
	    boolean isOracle=
	    	clazz.indexOf("OracleDriver")!=-1;
	    
	    StringBuffer sb=new StringBuffer("CREATE TABLE ");
	    if(mxStyle) {}
		else {
			if(isMSSQL)sb.append("dbo.");
			sb.append(PREFIX+"_"+type+"_data (");
			
			// expires
			sb.append("expires varchar(64) NOT NULL, ");
			// cfid
			sb.append("cfid varchar(64) NOT NULL, ");
			// name
			sb.append("name varchar(255) NOT NULL, ");
			// data
			sb.append("data ");
			if(isHSQLDB)sb.append("varchar ");
			else if(isOracle)sb.append("CLOB ");
			else sb.append(textType+" ");
			sb.append(" NOT NULL");
		}
	    sb.append(")");
		return new SQLImpl(sb.toString());
	}
	

	/**
	 *
	 * @see railo.runtime.type.scope.ClientSupportOld#initialize(railo.runtime.PageContext)
	 */
	public void touchBeforeRequest(PageContext pc) {
		setTimeSpan(pc);
		super.touchBeforeRequest(pc);
	}
	
	/**
	 * @see railo.runtime.type.scope.storage.StorageScope#getStorageType()
	 */
	public String getStorageType() {
		return "Datasource";
	}

	/**
	 * @return the datasourceName
	 */
	public String getDatasourceName() {
		return datasourceName;
	}
}
