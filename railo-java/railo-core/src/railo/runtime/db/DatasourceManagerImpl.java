package railo.runtime.db;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
	private Map<DataSource,DatasourceConnection> transConns=new HashMap<DataSource,DatasourceConnection>();
	//private DatasourceConnection transConn;
	

	/**
	 * constructor of the class
	 * @param pc
	 */
	public DatasourceManagerImpl(ConfigImpl c) {
		this.config=c;
	}
	
	private DatasourceConnection getTDC(DataSource ds) {
		return transConns.get(ds);
	}
	

	@Override
	public DatasourceConnection getConnection(PageContext pc,String _datasource, String user, String pass) throws PageException {
		return getConnection(pc,pc.getDataSource(_datasource), user, pass);
	}

	@Override
	public DatasourceConnection getConnection(PageContext pc,DataSource ds, String user, String pass) throws PageException {
		if(autoCommit)
			return config.getDatasourceConnectionPool().getDatasourceConnection(pc,ds,user,pass);
		
		
		pc=ThreadLocalPageContext.get(pc);
		DatasourceConnection newDC = ((PageContextImpl)pc)._getConnection(ds,user,pass);

		// transaction
		//if(!autoCommit) {
            try {
            	DatasourceConnection existingDC = getTDC(ds);
            	if(existingDC==null) {
                	newDC.getConnection().setAutoCommit(false);
					
                    if(isolation!=Connection.TRANSACTION_NONE)
                    	newDC.getConnection().setTransactionIsolation(isolation);
                    transConns.put(ds, newDC);
    			}
    			else if(!existingDC.equals(newDC)) {
                	if(QOQ_DATASOURCE_NAME.equalsIgnoreCase(ds.getName())) return newDC;
                	releaseConnection(pc, newDC);
    				throw new DatabaseException(
    						"can't use different connections to the same datasource inside a single transaction",null,null,newDC);
    			}
                else if(newDC.getConnection().getAutoCommit()) {
                	newDC.getConnection().setAutoCommit(false);
                }
            } catch (SQLException e) {
               ExceptionHandler.printStackTrace(e);
            }
		//}
		return newDC;
	}
	

	public void add(PageContext pc,ORMSession session) throws PageException {
		DataSource[] sources = session.getDataSources();
		for(int i=0;i<sources.length;i++){
			_add(pc,session,sources[i]);
		}
		
	}

	private void _add(PageContext pc,ORMSession session, DataSource ds) throws PageException {
		
		// transaction
		if(!autoCommit) {
			ORMDatasourceConnection newDC = new ORMDatasourceConnection(pc,session,ds);
        	
			try {
            	DatasourceConnection existingDC = getTDC(ds);
            	if(existingDC==null) {
                	if(isolation!=Connection.TRANSACTION_NONE)
                		newDC.getConnection().setTransactionIsolation(isolation);
                    transConns.put(ds, newDC);
                    
    			}
    			else if(!existingDC.equals(newDC)) {
    				releaseConnection(pc,newDC);
                	throw new DatabaseException(
    						"can't use different connections to the same datasource inside a single transaction",null,null,newDC);
    			}
                else if(newDC.getConnection().getAutoCommit()) {
                	newDC.getConnection().setAutoCommit(false);
                }
            } catch (SQLException e) {
               ExceptionHandler.printStackTrace(e);
            }
		}
	}
	
	@Override
	public void releaseConnection(PageContext pc,DatasourceConnection dc) {
		if(autoCommit) config.getDatasourceConnectionPool().releaseDatasourceConnection(config,dc,false);
	}
	
	
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
		if(autoCommit || transConns.size()==0)return;
		
		Iterator<DatasourceConnection> it = this.transConns.values().iterator();
		DatasourceConnection dc=null;
		try {
			while(it.hasNext()){
				dc= it.next();
				dc.getConnection().rollback();
			}
		} 
		catch (SQLException e) {
			throw new DatabaseException(e,dc);
		}
	}
	
	@Override
	public void savepoint() throws DatabaseException {
		if(autoCommit || transConns.size()==0)return;
		
		Iterator<DatasourceConnection> it = this.transConns.values().iterator();
		DatasourceConnection dc=null;
		try {
			while(it.hasNext()){
				dc= it.next();
				dc.getConnection().setSavepoint();
			}
		} 
		catch (SQLException e) {
			throw new DatabaseException(e,dc);
		}
	}

	@Override
	public void commit() throws DatabaseException {
		if(autoCommit || transConns.size()==0)return ;
		
		Iterator<DatasourceConnection> it = this.transConns.values().iterator();
		DatasourceConnection dc=null;
		try {
			while(it.hasNext()){
				dc= it.next();
				dc.getConnection().commit();
			}
		} 
		catch (SQLException e) {
			throw new DatabaseException(e,dc);
		}
	}
	
    @Override
    public boolean isAutoCommit() {
        return autoCommit;
    }

    @Override
    public void end() {
        autoCommit=true;
        if(transConns.size()>0) {
        	Iterator<DatasourceConnection> it = this.transConns.values().iterator();
        	DatasourceConnection dc;
    		while(it.hasNext()){
    			dc = it.next();
	        	try {
	            	dc.getConnection().setAutoCommit(true);
	            } 
	            catch (SQLException e) {
	                ExceptionHandler.printStackTrace(e);
	            }
    		}
            transConns.clear();
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
		transConns.clear();
		this.isolation=Connection.TRANSACTION_NONE;
	}

}