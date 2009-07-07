package railo.runtime.db;


import java.sql.Connection;
import java.sql.SQLException;

import railo.runtime.PageContext;
import railo.runtime.PageContextImpl;
import railo.runtime.config.ConfigImpl;
import railo.runtime.exp.DatabaseException;
import railo.runtime.exp.ExceptionHandler;
import railo.runtime.exp.PageException;

/**
 * this class handle multible db connection, transaction and logging
 */
public final class DatasourceManagerImpl implements DataSourceManager {
	
	private ConfigImpl config;
	
	boolean autoCommit=true;
	private int isolation;
	private DatasourceConnection transConn;
    

	/**
	 * constructor of the class
	 * @param pc
	 */
	public DatasourceManagerImpl(ConfigImpl c) {
		this.config=c;
	}

	/**
	 *
	 * @see DataSourceManager#getConnection(PageContext pc,java.lang.String, java.lang.String, java.lang.String)
	 */

	public DatasourceConnection getConnection(PageContext pc,String _datasource, String user, String pass) throws PageException {
		
		DatasourceConnection dc;
		if(pc!=null && !autoCommit)
			dc=((PageContextImpl)pc).getConnection(_datasource,user,pass);
		else
			dc=config.getDatasourceConnectionPool().getDatasourceConnection(config.getDataSource(_datasource),user,pass);
		// transaction
		if(!autoCommit) {
            try {
                if(transConn==null) {
                	dc.getConnection().setAutoCommit(false);
					
                    if(isolation!=Connection.TRANSACTION_NONE)
					    dc.getConnection().setTransactionIsolation(isolation);
                    transConn=dc;
    			}
    			else if(!transConn.equals(dc)) {
                	if("_queryofquerydb".equalsIgnoreCase(_datasource)) return dc;
    				throw new DatabaseException(
    						"can't connect different datasource or same with other username/password",null,null,dc);
    			}
                else if(dc.getConnection().getAutoCommit()) {
                    dc.getConnection().setAutoCommit(false);
                }
            } catch (SQLException e) {
               ExceptionHandler.printStackTrace(e);
            }
		}
		return dc;
	}
	

	/**
	 * @see railo.runtime.db.DataSourceManager#releaseConnection(railo.runtime.db.DatasourceConnection)
	 */
	public void releaseConnection(PageContext pc,DatasourceConnection dc) {
		if(autoCommit || pc==null)
			config.getDatasourceConnectionPool().releaseDatasourceConnection(dc);
	}
	
	/*private void releaseConnection(int pid,DatasourceConnection dc) {
		config.getDatasourceConnectionPool().releaseDatasourceConnection(pid,dc);
	}*/
	
	/**
	 *
	 * @see DataSourceManager#begin()
	 */
	public void begin() {
		this.autoCommit=false;
		this.isolation=Connection.TRANSACTION_NONE;		
	}
	
	/**
	 *
	 * @see DataSourceManager#begin(java.lang.String)
	 */
	public void begin(String isolation) {
		this.autoCommit=false;
    	//print.out("begin2:"+autoCommit);
		
		if(isolation.equalsIgnoreCase("read_uncommitted"))
		    this.isolation=Connection.TRANSACTION_READ_UNCOMMITTED;
		else if(isolation.equalsIgnoreCase("read_committed"))
		    this.isolation=Connection.TRANSACTION_READ_COMMITTED;
		else if(isolation.equalsIgnoreCase("repeatable_read"))
		    this.isolation=Connection.TRANSACTION_REPEATABLE_READ;
		else if(isolation.equalsIgnoreCase("serializable"))
		    this.isolation=Connection.TRANSACTION_SERIALIZABLE;
		else 
		    this.isolation=Connection.TRANSACTION_NONE;
        
	}
    /**
	 *
	 * @see DataSourceManager#begin(int)
	 */
    public void begin(int isolation) {
    	//print.out("begin:"+autoCommit);
        this.autoCommit=false;
        this.isolation=isolation;
    }

	/**
	 *
	 * @see DataSourceManager#rollback()
	 */
	public void rollback() throws DatabaseException {
		if(autoCommit)return;
        //autoCommit=true;
		if(transConn!=null) {
			try {
				transConn.getConnection().rollback();
				//transConn.setAutoCommit(true);
			} 
			catch (SQLException e) {
				throw new DatabaseException(e,transConn);
			}
			//transConn=null;
		}
	}

	/**
	 *
	 * @see DataSourceManager#commit()
	 */
	public void commit() throws DatabaseException {
        //print.out("commit:"+autoCommit);
        if(autoCommit)return ;
        //autoCommit=true;
		if(transConn!=null) {
			try {
				transConn.getConnection().commit();
				//transConn.setAutoCommit(true);
			} 
			catch (SQLException e) {
				throw new DatabaseException(e,transConn);
			}
			//transConn=null;
		}
	}
	
    /**
	 *
	 * @see DataSourceManager#isAutoCommit()
	 */
    public boolean isAutoCommit() {
        return autoCommit;
    }

    /**
	 *
	 * @see DataSourceManager#end()
	 */
    public void end() {
        //print.out("end:"+autoCommit);
        autoCommit=true;
        if(transConn!=null) {
            try {
            	transConn.getConnection().setAutoCommit(true);
            } 
            catch (SQLException e) {
                ExceptionHandler.printStackTrace(e);
            }
            transConn=null;
        }
    }

	public void remove(String datasource) {
		config.getDatasourceConnectionPool().remove(datasource);
	}

}