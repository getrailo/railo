package railo.runtime.exp;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import railo.commons.lang.StringUtil;
import railo.runtime.config.Config;
import railo.runtime.db.DataSource;
import railo.runtime.db.DatasourceConnection;
import railo.runtime.db.SQL;
import railo.runtime.op.Caster;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.util.KeyConstants;


/**
 * Database Exception Object
 */



public final class DatabaseException extends PageExceptionImpl {

	private SQL sql;
	private String sqlstate="";
	private int errorcode=-1;
	private DataSource datasource;

	public DatabaseException(SQLException sqle,DatasourceConnection dc) {
		super(
				sqle.getCause() instanceof SQLException?
						(sqle=(SQLException)sqle.getCause()).getMessage():
						sqle.getMessage(),"database");
		
		set(sqle);
		set(dc);
	}

	public DatabaseException(String message,String detail,SQL sql,DatasourceConnection dc) {
		super(message,"database");
		
		set(sql);
		set(null,detail);
		set(dc);
	}
	
	/**
	 * Constructor of the class
	 * @param message error message
	 * @param detail detailed error message
	 * @param sqle
	 * @param sql
	 * @param dc
	 */
	private DatabaseException(String message, String detail, SQLException sqle, SQL sql,DatasourceConnection dc) {
		super(sqle.getCause() instanceof SQLException?message:"","database");
		
		set(sql);
		set(sqle,detail);
		set(sqle);
		set(dc);
	}
	
	private void set(SQL sql) {
		this.sql=sql;
		if(sql!=null) {
			setAdditional(KeyConstants._SQL,sql.toString());
		}
	}

	private void set(SQLException sqle,String detail) {
		String sqleMessage=sqle!=null?sqle.getMessage():"";
		if(detail!=null){
			if(!StringUtil.isEmpty(sqleMessage))
				setDetail(detail+"\n"+sqleMessage);
			else 
				setDetail(detail);
		}
		else {
			if(!StringUtil.isEmpty(sqleMessage))
				setDetail(sqleMessage);
		}
	}

	private void set(SQLException sqle) {
		if(sqle!=null) {
			sqlstate=sqle.getSQLState();
			errorcode=sqle.getErrorCode();
			
			this.setStackTrace(sqle.getStackTrace());
		}
	}

	private void set(DatasourceConnection dc) {
		if(dc!=null) {
			datasource=dc.getDatasource();
			try {
				DatabaseMetaData md = dc.getConnection().getMetaData();
				md.getDatabaseProductName();
				setAdditional(KeyImpl.init("DatabaseName"),md.getDatabaseProductName());
				setAdditional(KeyImpl.init("DatabaseVersion"),md.getDatabaseProductVersion());
				setAdditional(KeyImpl.init("DriverName"),md.getDriverName());
				setAdditional(KeyImpl.init("DriverVersion"),md.getDriverVersion());
				//setAdditional("url",md.getURL());
				
				setAdditional(KeyConstants._Datasource,dc.getDatasource().getName());
				
				
			} 
			catch (SQLException e) {}
		}
	}

	/**
	 * Constructor of the class
	 * @param message
	 * @param sqle
	 * @param sql
	
	public DatabaseException(String message, SQLException sqle, SQL sql,DatasourceConnection dc) {
		this(message,null,sqle,sql,dc);
	} */
	
	
	/**
	 * Constructor of the class
	 * @param sqle
	 * @param sql
	 */
	public DatabaseException(SQLException sqle, SQL sql,DatasourceConnection dc) {
		this(sqle!=null?sqle.getMessage():null,null,sqle,sql,dc);
	}
	
	/**
	 * Constructor of the class
	 * @param sqle
	 */
	

	@Override
	public CatchBlock getCatchBlock(Config config) {
	    String strSQL=sql==null?"":sql.toString();
	    if(StringUtil.isEmpty(strSQL))strSQL=Caster.toString(getAdditional().get("SQL", ""),"");
		
	    String datasourceName=datasource==null?"":datasource.getName();
		if(StringUtil.isEmpty(datasourceName))datasourceName=Caster.toString(getAdditional().get("DataSource", ""),"");
		
		CatchBlock sct = super.getCatchBlock(config);
		sct.setEL("NativeErrorCode",new Double(errorcode));
		sct.setEL("DataSource",datasourceName);
		sct.setEL("SQLState",sqlstate);
		sct.setEL("Sql",strSQL);
		sct.setEL("queryError",strSQL);
		sct.setEL("where","");
		return sct;
	}
}