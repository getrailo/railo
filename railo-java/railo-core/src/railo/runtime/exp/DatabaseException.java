package railo.runtime.exp;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.config.Config;
import railo.runtime.db.DataSource;
import railo.runtime.db.DatasourceConnection;
import railo.runtime.db.SQL;
import railo.runtime.op.Caster;
import railo.runtime.type.Struct;


/**
 * Database Exception Object
 */



public final class DatabaseException extends PageExceptionImpl {

	private SQL sql;
	private String sqlstate="";
	private int errorcode=-1;
	private DataSource datasource;

	/**
	 * Constructor of the class
	 * @param message error message
	 * @param detail detailed error message
	 * @param sqle
	 * @param sql
	 * @param dc
	 */
	public DatabaseException(String message, String detail, SQLException sqle, SQL sql,DatasourceConnection dc) {
		super(message,"database");
		String sqleMessage=sqle!=null?sqle.getMessage():"";
		this.sql=sql;
		if(dc!=null)datasource=dc.getDatasource();
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
		if(sqle!=null) {
			sqlstate=sqle.getSQLState();
			errorcode=sqle.getErrorCode();
			this.setStackTrace(sqle.getStackTrace());
		}
		if(sql!=null) {
			setAdditional("SQL",sql.toString());
		}
		if(dc!=null) {
			try {
				DatabaseMetaData md = dc.getConnection().getMetaData();
				md.getDatabaseProductName();
				setAdditional("DatabaseName",md.getDatabaseProductName());
				setAdditional("DatabaseVersion",md.getDatabaseProductVersion());
				setAdditional("DriverName",md.getDriverName());
				setAdditional("DriverVersion",md.getDriverVersion());
				//setAdditional("url",md.getURL());
				
				setAdditional("Datasource",dc.getDatasource().getName());
				
				
			} 
			catch (SQLException e) {}
			
		}
	}
	
	/**
	 * Constructor of the class
	 * @param message
	 * @param sqle
	 * @param sql
	 */
	public DatabaseException(String message, SQLException sqle, SQL sql,DatasourceConnection dc) {
		this(message,null,sqle,sql,dc);
	}
	
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
	public DatabaseException(SQLException sqle,DatasourceConnection dc) {
		this(sqle!=null?sqle.getMessage():null,null,sqle,null,dc);
	}

	/**
	 * @see railo.runtime.exp.PageExceptionImpl#getCatchBlock(railo.runtime.config.Config)
	 */
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