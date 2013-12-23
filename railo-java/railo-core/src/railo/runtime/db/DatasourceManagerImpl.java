package railo.runtime.db;


import java.sql.Connection;
import java.sql.SQLException;

import railo.runtime.PageContext;
import railo.runtime.PageContextImpl;
import railo.runtime.config.ConfigImpl;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.DatabaseException;
import railo.runtime.exp.DeprecatedException;
import railo.runtime.exp.ExceptionHandler;
import railo.runtime.exp.PageException;
import railo.runtime.exp.PageRuntimeException;
import railo.runtime.orm.ORMDatasourceConnection;
import railo.runtime.orm.ORMSession;

/**
 * this class handle multible db connection, transaction and logging
 */
public final class DatasourceManagerImpl implements DataSourceManager {

	public static final String QOQ_DATASOURCE_NAME = "_queryofquerydb";

	private ConfigImpl config;
	
	boolean autoCommit=true;
	private int isolation=Connection.TRANSACTION_NONE;
	private DatasourceConnection transConn;
    

	/**
	 * constructor of the class
	 * @param pc
	 */
	public DatasourceManagerImpl(ConfigImpl c) {
		this.config=c;
	}

	@Override
	public DatasourceConnection getConnection(PageContext pc,String _datasource, String user, String pass) throws PageException {
		return getConnection(pc,((PageContextImpl)pc).getDataSource(_datasource), user, pass);
	}

	@Override
	public DatasourceConnection getConnection(PageContext pc,DataSource ds, String user, String pass) throws PageException {
		if(autoCommit)
			return config.getDatasourceConnectionPool().getDatasourceConnection(pc,ds,user,pass);
		
		
		pc=ThreadLocalPageContext.get(pc);
		DatasourceConnection dc=((PageContextImpl)pc)._getConnection(ds,user,pass);
		
		// transaction
		//if(!autoCommit) {
            try {
                if(transConn==null) {
                	dc.getConnection().setAutoCommit(false);
					
                    if(isolation!=Connection.TRANSACTION_NONE)
					    dc.getConnection().setTransactionIsolation(isolation);
                    transConn=dc;
    			}
    			else if(!transConn.equals(dc)) {
                	if(QOQ_DATASOURCE_NAME.equalsIgnoreCase(ds.getName())) return dc;
    				throw new DatabaseException(
    						"can't use different connections inside a transaction",null,null,dc);
    			}
                else if(dc.getConnection().getAutoCommit()) {
                    dc.getConnection().setAutoCommit(false);
                }
            } catch (SQLException e) {
               ExceptionHandler.printStackTrace(e);
            }
		//}
		return dc;
	}
	

	public void add(PageContext pc,ORMSession session) throws PageException {
		
		// transaction
		if(!autoCommit) {
            try {
                if(transConn==null) {
                	ORMDatasourceConnection dc=new ORMDatasourceConnection(pc,session);
                	
                    if(isolation!=Connection.TRANSACTION_NONE)
					    dc.getConnection().setTransactionIsolation(isolation);
                    transConn=dc;
    			}
    			else if(!(transConn instanceof ORMDatasourceConnection)){
    				/*if(transConn.getDatasource().equals(session.getEngine().getDataSource())){
    					ORMDatasourceConnection dc=new ORMDatasourceConnection(pc,session);
                    	
                        if(isolation!=Connection.TRANSACTION_NONE)
    					    dc.getConnection().setTransactionIsolation(isolation);
                        transConn=dc;
    				}
    				else*/
    					throw new DatabaseException(
    						"can't use transaction for datasource and orm at the same time",null,null,null);
    			}
            } catch (SQLException e) {
               ExceptionHandler.printStackTrace(e);
            }
		}
	}
	
	@Override
	public void releaseConnection(PageContext pc,DatasourceConnection dc) {
		if(autoCommit) config.getDatasourceConnectionPool().releaseDatasourceConnection(dc);
	}
	
	/*private void releaseConnection(int pid,DatasourceConnection dc) {
		config.getDatasourceConnectionPool().releaseDatasourceConnection(pid,dc);
	}*/
	
	@Override
	public void begin() {
		this.autoCommit=false;
		this.isolation=Connection.TRANSACTION_NONE;		
	}
	
	@Override
	public void begin(String isolation) {
		this.autoCommit=false;
    	
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
    @Override
    public void begin(int isolation) {
    	//print.out("begin:"+autoCommit);
    	this.autoCommit=false;
        this.isolation=isolation;
    }

	@Override
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
	
	@Override
	public void savepoint() throws DatabaseException {
		if(autoCommit)return;
        //autoCommit=true;
		if(transConn!=null) {
			try {
				transConn.getConnection().setSavepoint();
			} 
			catch (SQLException e) {
				throw new DatabaseException(e,transConn);
			}
		}
	}

	@Override
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
	
    @Override
    public boolean isAutoCommit() {
        return autoCommit;
    }

    @Override
    public void end() {
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

	public void remove(DataSource datasource) {
		config.getDatasourceConnectionPool().remove(datasource);
	}

	public void remove(String datasource) {
		throw new PageRuntimeException(new DeprecatedException("method no longer supported!"));
		//config.getDatasourceConnectionPool().remove(datasource);
	}

	public void release() {
		this.transConn=null;
		this.isolation=Connection.TRANSACTION_NONE;
	}

}