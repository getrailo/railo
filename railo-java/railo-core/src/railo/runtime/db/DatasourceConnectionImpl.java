package railo.runtime.db;

import java.sql.Connection;

/**
 * wrap for datasorce and connection from it
 */
public final class DatasourceConnectionImpl implements DatasourceConnection {
    
    private Connection connection;
    private DataSource datasource;
    private long time;
	private String username;
	private String password;
	private int transactionIsolationLevel=-1;
	private int requestId=-1;

    /**
     * @param connection
     * @param datasource
     * @param pass  
     * @param user 
     */
    public DatasourceConnectionImpl(Connection connection, DataSource datasource, String username, String password) {
        this.connection = connection;
        this.datasource = datasource;
        this.time=System.currentTimeMillis();
        this.username = username;
        this.password = password;
        
        if(username==null) {
        	this.username=datasource.getUsername();
        	this.password=datasource.getPassword();
        }
        if(this.password==null)this.password="";
		
    }
    
    /**
     * @see railo.runtime.db.DatasourceConnection#getConnection()
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * @see railo.runtime.db.DatasourceConnection#getDatasource()
     */
    public DataSource getDatasource() {
        return datasource;
    }

    /**
     * @see railo.runtime.db.DatasourceConnection#isTimeout()
     */
    public boolean isTimeout() {
        int timeout=datasource.getConnectionTimeout();
        if(timeout<1)timeout=1;
        timeout*=60000;      
        return (time+timeout)<System.currentTimeMillis();
    }

	public DatasourceConnection using() {
		time=System.currentTimeMillis();
		return this;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if(this==obj) return true;
		if(!(obj instanceof DatasourceConnectionImpl)) return false;
		DatasourceConnectionImpl other=(DatasourceConnectionImpl) obj;
		
		if(!datasource.equals(other.datasource)) return false;
		//print.out(username+".equals("+other.username+") && "+password+".equals("+other.password+")");
		return username.equals(other.username) && password.equals(other.password);
	}

	/**
	 * @return the transactionIsolationLevel
	 */
	public int getTransactionIsolationLevel() {
		return transactionIsolationLevel;
	}

	
	public int getRequestId() {
		return requestId;
	}
	public void setRequestId(int requestId) {
		this.requestId=requestId;
	}
}