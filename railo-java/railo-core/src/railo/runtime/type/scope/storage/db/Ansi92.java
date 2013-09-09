package railo.runtime.type.scope.storage.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.TimeZone;

import railo.commons.io.log.Log;
import railo.runtime.PageContext;
import railo.runtime.config.Config;
import railo.runtime.converter.ConverterException;
import railo.runtime.converter.ScriptConverter;
import railo.runtime.db.DatasourceConnection;
import railo.runtime.db.SQL;
import railo.runtime.db.SQLCaster;
import railo.runtime.db.SQLImpl;
import railo.runtime.db.SQLItem;
import railo.runtime.db.SQLItemImpl;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.DatabaseException;
import railo.runtime.exp.PageException;
import railo.runtime.interpreter.VariableInterpreter;
import railo.runtime.op.Caster;
import railo.runtime.type.Query;
import railo.runtime.type.QueryImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.scope.ScopeContext;
import railo.runtime.type.scope.storage.StorageScopeDatasource;
import railo.runtime.type.scope.storage.StorageScopeEngine;
import railo.runtime.type.scope.storage.StorageScopeListener;
import railo.runtime.type.scope.storage.clean.DatasourceStorageScopeCleaner;
import railo.runtime.type.util.KeyConstants;

public class Ansi92 extends SQLExecutorSupport {
	
	public static final String PREFIX = "cf";

	@Override
	public Query select(Config config,String cfid,String applicationName,DatasourceConnection dc, int type,Log log, boolean createTableIfNotExist) throws PageException {
		String strType = VariableInterpreter.scopeInt2String(type);
		Query query=null;
	    SQL sqlSelect=new SQLImpl("select data from "+PREFIX+"_"+strType+"_data where cfid=? and name=? and expires > ?"
				,new SQLItem[]{
	 		new SQLItemImpl(cfid,Types.VARCHAR),
			new SQLItemImpl(applicationName,Types.VARCHAR),
			new SQLItemImpl(now(config),Types.VARCHAR)
		});
	    
	    PageContext pc = ThreadLocalPageContext.get();
		
		try {
			query = new QueryImpl(pc,dc,sqlSelect,-1,-1,-1,"query");
		}
	    catch (DatabaseException de) {
	    	if(dc==null || !createTableIfNotExist) throw de;
	    	try {
	    		SQL sql = createSQL(dc,"text",strType);
	    		ScopeContext.info(log,sql.toString());
				new QueryImpl(pc,dc,sql,-1,-1,-1,"query");
	    	}
		    catch (DatabaseException _de) {
		    	try {
		    		SQL sql = createSQL(dc,"memo",strType);
		    		ScopeContext.info(log,sql.toString());
					new QueryImpl(pc,dc,sql,-1,-1,-1,"query");
		    	}
			    catch (DatabaseException __de) {
			    	SQL sql = createSQL(dc,"clob",strType);
		    		ScopeContext.info(log,sql.toString());
			    	new QueryImpl(pc,dc,sql,-1,-1,-1,"query");
			    }
		    }
	    	query = new QueryImpl(pc,dc,sqlSelect,-1,-1,-1,"query");
		}
	    ScopeContext.info(log,sqlSelect.toString());
		return query;
	}
	
	@Override
	public void update(Config config, String cfid, String applicationName, DatasourceConnection dc, int type, Struct data, long timeSpan, Log log) throws PageException, SQLException {
		String strType = VariableInterpreter.scopeInt2String(type);
		TimeZone tz = ThreadLocalPageContext.getTimeZone();
		int recordsAffected = _update(config,dc.getConnection(),cfid,applicationName,"update "+PREFIX+"_"+strType+"_data set expires=?,data=? where cfid=? and name=?",data,timeSpan,log,tz);
		
		if(recordsAffected>1) {
	    	delete(config, cfid, applicationName, dc, type, log);
	    	recordsAffected=0;
	    }
		if(recordsAffected==0) {
	    	_update(config,dc.getConnection(),cfid,applicationName,"insert into "+PREFIX+"_"+strType+"_data (expires,data,cfid,name) values(?,?,?,?)",data,timeSpan,log,tz);
	    }
	}
	
	private static int _update(Config config,Connection conn,String cfid, String applicationName, String strSQL,Struct data, long timeSpan, Log log, TimeZone tz) throws SQLException, PageException {
		//String appName = pc.getApplicationContext().getName();
		try{
			SQLImpl sql = new SQLImpl(strSQL,new SQLItem[]{
				new SQLItemImpl(createExpires(config,timeSpan),Types.VARCHAR),
				new SQLItemImpl(new ScriptConverter().serializeStruct(data,ignoreSet),Types.VARCHAR),
				new SQLItemImpl(cfid,Types.VARCHAR),
				new SQLItemImpl(applicationName,Types.VARCHAR)
			});
			ScopeContext.info(log,sql.toString());
			
			return execute(conn, sql,tz);
		}
		catch(ConverterException ce){
			throw Caster.toPageException(ce);
		}
	}
	
	@Override
	public void delete(Config config, String cfid, String applicationName, DatasourceConnection dc, int type, Log log) throws PageException, SQLException {
		String strType = VariableInterpreter.scopeInt2String(type);
		String strSQL="delete from "+PREFIX+"_"+strType+"_data where cfid=? and name=?";
		SQLImpl sql = new SQLImpl(strSQL,new SQLItem[]{
				new SQLItemImpl(cfid,Types.VARCHAR),
				new SQLItemImpl(applicationName,Types.VARCHAR)
			});
		execute(dc.getConnection(), sql,ThreadLocalPageContext.getTimeZone());
		ScopeContext.info(log,sql.toString());
		
	}


	@Override
	public void clean(Config config, DatasourceConnection dc, int type,StorageScopeEngine engine,DatasourceStorageScopeCleaner cleaner,StorageScopeListener listener, Log log) throws PageException {
		String strType = VariableInterpreter.scopeInt2String(type);
		// select
	    SQL sqlSelect=new SQLImpl("select cfid,name from "+PREFIX+"_"+strType+"_data where expires<=?"
						,new SQLItem[]{
			 		new SQLItemImpl(System.currentTimeMillis(),Types.VARCHAR)
				});
	    QueryImpl query;
	    try{
	    	query = new QueryImpl(ThreadLocalPageContext.get(),dc,sqlSelect,-1,-1,-1,"query");
		}
		catch(Throwable t){
			// possible that the table not exist, if not there is nothing to clean
			return;
		}
		
		int recordcount=query.getRecordcount();
		
		String cfid,name;
		for(int row=1;row<=recordcount;row++){
			cfid=Caster.toString(query.getAt(KeyConstants._cfid, row, null),null);
			name=Caster.toString(query.getAt(KeyConstants._name, row, null),null);
			
			if(listener!=null)listener.doEnd(engine, cleaner,name, cfid);
			
			
			ScopeContext.info(log,"remove "+strType+"/"+name+"/"+cfid+" from datasource "+dc.getDatasource().getName());
			engine.remove(type,name,cfid);
			SQLImpl sql = new SQLImpl("delete from "+StorageScopeDatasource.PREFIX+"_"+strType+"_data where cfid=? and name=?",new SQLItem[]{
					new SQLItemImpl(cfid,Types.VARCHAR),
					new SQLItemImpl(name,Types.VARCHAR)
					});
			new QueryImpl(ThreadLocalPageContext.get(),dc,sql,-1,-1,-1,"query");
			
			
			
		}
	}


	private static int execute(Connection conn, SQLImpl sql, TimeZone tz) throws SQLException, PageException {
		PreparedStatement preStat = conn.prepareStatement(sql.getSQLString());
		int count=0;
		try {
			SQLItem[] items=sql.getItems();
		    for(int i=0;i<items.length;i++) {
	            SQLCaster.setValue(tz,preStat,i+1,items[i]);
	        }
		    count= preStat.executeUpdate();
		}
		finally {
		    preStat.close();	
		}
	    return count;
	}
	
	private static SQL createSQL(DatasourceConnection dc, String textType, String type) {
		String clazz = dc.getDatasource().getClazz().getName();
		
	    boolean isMSSQL=
	    	clazz.equals("com.microsoft.jdbc.sqlserver.SQLServerDriver") || 
	    	clazz.equals("net.sourceforge.jtds.jdbc.Driver");
	    boolean isHSQLDB=
	    	clazz.equals("org.hsqldb.jdbcDriver");
	    boolean isOracle=
	    	clazz.indexOf("OracleDriver")!=-1;
	    
	    StringBuffer sb=new StringBuffer("CREATE TABLE ");
	    
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
		
	    sb.append(")");
		return new SQLImpl(sb.toString());
	}
}
