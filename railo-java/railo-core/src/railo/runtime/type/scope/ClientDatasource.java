package railo.runtime.type.scope;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import railo.runtime.PageContext;
import railo.runtime.config.ConfigImpl;
import railo.runtime.converter.ConverterException;
import railo.runtime.converter.ScriptConverter;
import railo.runtime.db.DatasourceConnection;
import railo.runtime.db.DatasourceConnectionPool;
import railo.runtime.db.SQL;
import railo.runtime.db.SQLCaster;
import railo.runtime.db.SQLImpl;
import railo.runtime.db.SQLItem;
import railo.runtime.db.SQLItemImpl;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.dump.DumpTable;
import railo.runtime.exp.DatabaseException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Query;
import railo.runtime.type.QueryImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.dt.DateTimeImpl;

/**
 * client scope that store it's data in a datasource
 */
public final class ClientDatasource extends ClientSupport {

	private static final long serialVersionUID = 239179599401918216L;
	private static final Collection.Key DATA = KeyImpl.getInstance("data");

	private static boolean structOk;
	
	private String datasourceName;
	private PageContext pc;
	
	
	/**
	 * Constructor of the class
	 * @param pc
	 * @param name
	 * @param sct
	 * @param b 
	 */
	private ClientDatasource(PageContext pc,String datasourceName, Struct sct) { 
		super(
				sct,
				doNowIfNull(pc,Caster.toDate(sct.get(TIMECREATED,null),false,pc.getTimeZone(),null)),
				doNowIfNull(pc,Caster.toDate(sct.get(LASTVISIT,null),false,pc.getTimeZone(),null)),
				-1, 
				Caster.toIntValue(sct.get(HITCOUNT,"1"),1));

		//this.isNew=isNew;
		this.datasourceName=datasourceName;
		//this.manager = (DatasourceManagerImpl) pc.getDataSourceManager(); 
	}

	/**
	 * Constructor of the class, clone existing
	 * @param other
	 */
	private ClientDatasource(ClientDatasource other,boolean deepCopy) {
		super(other,deepCopy);
		
		this.datasourceName=other.datasourceName;
		this.pc=other.pc;
		//this.manager=other.manager;
	}
	
	private static DateTime doNowIfNull(PageContext pc,DateTime dt) {
		if(dt==null)return new DateTimeImpl(pc.getConfig());
		return dt;
	}
	
	/**
	 * load an new instance of the client datasource scope
	 * @param datasourceName 
	 * @param appName
	 * @param pc
	 * @return client datasource scope
	 * @throws PageException
	 */
	public static Client getInstance(String datasourceName, PageContext pc) throws PageException {
			
			Struct _sct = _loadData(pc, datasourceName, false);
			structOk=true;
			if(_sct==null) _sct=new StructImpl();
			
		return new ClientDatasource(pc,datasourceName,_sct);
	}
	
	public static Client getInstanceEL(String datasourceName, PageContext pc) {
		try {
			return getInstance(datasourceName, pc);
		}
		catch (PageException e) {}
		return new ClientDatasource(pc,datasourceName,new StructImpl());
	}
	
	
	private static Struct _loadData(PageContext pc, String datasourceName, boolean mxStyle) throws PageException	{
		DatasourceConnection dc=null;
		Query query=null;
	    
	    // select
	    SQL sqlSelect=mxStyle?
				new SQLImpl("mx"):
				new SQLImpl("select data from railo_client_data where cfid=? and name=?"
						,new SQLItem[]{
			 		new SQLItemImpl(pc.getCFID(),Types.VARCHAR),
					new SQLItemImpl(pc.getApplicationContext().getName(),Types.VARCHAR)
				});
		
		ConfigImpl config = (ConfigImpl)pc.getConfig();
		//int pid=1000;
		DatasourceConnectionPool pool = config.getDatasourceConnectionPool();
		try {
			dc=pool.getDatasourceConnection(pc,config.getDataSource(datasourceName),null,null);
			query = new QueryImpl(dc,sqlSelect,-1,-1,-1,"query");
		}
	    catch (DatabaseException de) {
	    	if(dc==null) throw de;
	    	try {
				new QueryImpl(dc,createSQL(dc,mxStyle,"text"),-1,-1,-1,"query");
	    	}
		    catch (DatabaseException _de) {
		    	try {
					new QueryImpl(dc,createSQL(dc,mxStyle,"memo"),-1,-1,-1,"query");
		    	}
			    catch (DatabaseException __de) {
			    	new QueryImpl(dc,createSQL(dc,mxStyle,"clob"),-1,-1,-1,"query");
			    }
		    }
	    	query = new QueryImpl(dc,sqlSelect,-1,-1,-1,"query");
		}
	    finally {
	    	if(dc!=null) pool.releaseDatasourceConnection(dc);
	    }
	    pc.getDebugger().addQueryExecutionTime(datasourceName,"",sqlSelect,query.getRecordcount(),pc.getCurrentPageSource(),query.executionTime());
	    boolean _isNew = query.getRecordcount()==0;
	    
	    if(_isNew) return null;
	    String str=Caster.toString(query.get(DATA));
	    if(mxStyle) return null;
	    return (Struct)pc.evaluate(str);
	}

	/**
	 *
	 * @see railo.runtime.type.scope.ClientSupport#release()
	 */
	public void release() {
		structOk=false;
		super.release();
		if(!super.hasContent()) return;
		
		DatasourceConnection dc = null;
		ConfigImpl config = (ConfigImpl)pc.getConfig();
		//int pid=1000;//pc.getId()+10000;
		DatasourceConnectionPool pool = config.getDatasourceConnectionPool();
		try {
			dc=pool.getDatasourceConnection(pc,config.getDataSource(datasourceName),null,null);
			int recordsAffected = executeUpdate(dc.getConnection(),"update railo_client_data set data=? where cfid=? and name=?",false);
		    if(recordsAffected>1) {
		    	executeUpdate(dc.getConnection(),"delete from railo_client_data where cfid=? and name=?",true);
		    	recordsAffected=0;
		    }
		    if(recordsAffected==0) {
		    	executeUpdate(dc.getConnection(),"insert into railo_client_data (data,cfid,name) values(?,?,?)",false);
		    }

		} 
		catch (Exception e) {}
		finally {
			if(dc!=null) pool.releaseDatasourceConnection(dc);
			pc=null;
		}
	}

	private int executeUpdate(Connection conn, String strSQL, boolean ignoreData) throws SQLException, PageException, ConverterException {
		String appName = pc.getApplicationContext().getName();
		SQLImpl sql = new SQLImpl(strSQL,new SQLItem[]{
			new SQLItemImpl(new ScriptConverter().serializeStruct(sct,ignoreSet),Types.VARCHAR),
			new SQLItemImpl(pc.getCFID(),Types.VARCHAR),
			new SQLItemImpl(appName,Types.VARCHAR)
		});
		if(ignoreData)sql = new SQLImpl(strSQL,new SQLItem[]{
				new SQLItemImpl(pc.getCFID(),Types.VARCHAR),
				new SQLItemImpl(appName,Types.VARCHAR)
			});
		
		//print.out(sql);
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

	private static SQL createSQL(DatasourceConnection dc, boolean mxStyle, String textType) {
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
			sb.append("railo_client_data (");
			
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
	 * @see railo.runtime.dump.Dumpable#toDumpData(railo.runtime.PageContext, int)
	 */
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
		DumpTable table = super.toDumpTable(pageContext, maxlevel,dp);
		table.setTitle("Scope Client (Datasource)");
		return table;
	}
	
	/**
	 *
	 * @see railo.runtime.type.Collection#duplicate(boolean)
	 */
	public Collection duplicate(boolean deepCopy) {
    	return new ClientDatasource(this,deepCopy);
	}

	/**
	 *
	 * @see railo.runtime.type.scope.ClientSupport#initialize(railo.runtime.PageContext)
	 */
	public void initialize(PageContext pc) {
		this.pc=pc;
		//print.out(isNew);
		try {
			if(!structOk)sct=_loadData(pc, datasourceName, false);
			
		} catch (PageException e) {
			//
		}
		super.initialize(pc);
	}
}
