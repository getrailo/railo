
package railo.runtime.net.ftp;




/**
 *  
 */
public final class FTPConnectionImpl implements FTPConnection {
    
    private String name;
    private String server;
    private String username;
    private String password;
    private int port;
    private int timeout;
    private short transferMode;
    private boolean passive;
    private String proxyserver;
    private int proxyport;
    private String proxyuser;
    private String proxypassword;

    /**
     * @param name
     * @param server
     * @param username
     * @param password
     * @param port
     * @param timeout
     * @param transferMode
     * @param passive
     * @param proxyserver
     */
    public FTPConnectionImpl(String name, String server, String username, String password,int port, int timeout, short transferMode,boolean passive, 
    		String proxyserver,int proxyport,String proxyuser, String proxypassword) {
        this.name=name==null?null:name.toLowerCase().trim();
        this.server=server;
        this.username=username;
        this.password=password;
        this.port=port;
        this.timeout=timeout;
        this.transferMode=transferMode;
        this.passive=passive;
        
        this.proxyserver=proxyserver;
        this.proxyport=proxyport;
        this.proxyuser=proxyuser;
        this.proxypassword=proxypassword;
    }
    /**
     * @see railo.runtime.net.ftp.FTPConnection#getName()
     */
    public String getName() {
        return name;
    }
    /**
     * @see railo.runtime.net.ftp.FTPConnection#getPassword()
     */
    public String getPassword() {
        return password;
    }
    /**
     * @see railo.runtime.net.ftp.FTPConnection#getServer()
     */
    public String getServer() {
        return server;
    }
    /**
     * @see railo.runtime.net.ftp.FTPConnection#getUsername()
     */
    public String getUsername() {
        return username;
    }
    /**
     * @see railo.runtime.net.ftp.FTPConnection#hasLoginData()
     */
    public boolean hasLoginData() {
        return server!=null;// && username!=null && password!=null;
    }
    /**
     * @see railo.runtime.net.ftp.FTPConnection#hasName()
     */
    public boolean hasName() {
        return name!=null;
    }
    /**
     * @see railo.runtime.net.ftp.FTPConnection#getPort()
     */
    public int getPort() {
        return port;
    }
    /**
     * @see railo.runtime.net.ftp.FTPConnection#getTimeout()
     */
    public int getTimeout() {
        return timeout;
    }
    /**
     * @see railo.runtime.net.ftp.FTPConnection#getTransferMode()
     */
    public short getTransferMode() {
        return transferMode;
    }
    

	public void setTransferMode(short transferMode) {
		this.transferMode=transferMode;
	}
    
    /**
     * @see railo.runtime.net.ftp.FTPConnection#isPassive()
     */
    public boolean isPassive() {
        return passive;
    }
    /**
     * @see railo.runtime.net.ftp.FTPConnection#loginEquals(railo.runtime.net.ftp.FTPConnection)
     */
    public boolean loginEquals(FTPConnection conn) {
        return 
        	server.equalsIgnoreCase(conn.getServer()) && 
        	username.equals(conn.getUsername()) && 
        	password.equals(conn.getPassword());
    }
    
	/**
	 * @see railo.runtime.net.ftp.FTPConnection#getProxyPassword()
	 */
	public String getProxyPassword() {
		return proxypassword;
	}
	
	/**
	 * @see railo.runtime.net.ftp.FTPConnection#getProxyPort()
	 */
	public int getProxyPort() {
		return proxyport;
	}
	
	/**
	 * @see railo.runtime.net.ftp.FTPConnection#getProxyServer()
	 */
	public String getProxyServer() {
		return proxyserver;
	}
	
	/**
	 * @see railo.runtime.net.ftp.FTPConnection#getProxyUser()
	 */
	public String getProxyUser() {
		return proxyuser;
	}
	
	public boolean equal(Object o){
		if(!(o instanceof FTPConnection)) return false;
		FTPConnection other=(FTPConnection) o;
		
		if(neq(other.getPassword(),getPassword())) return false;
		if(neq(other.getProxyPassword(),getProxyPassword())) return false;
		if(neq(other.getProxyServer(),getProxyServer())) return false;
		if(neq(other.getProxyUser(),getProxyUser())) return false;
		if(neq(other.getServer(),getServer())) return false;
		if(neq(other.getUsername(),getUsername())) return false;
		
		if(other.getPort()!=getPort()) return false;
		if(other.getProxyPort()!=getProxyPort()) return false;
		//if(other.getTimeout()!=getTimeout()) return false;
		if(other.getTransferMode()!=getTransferMode()) return false;
		
		return true;
	}
	
	private boolean neq(String left, String right) {
		if(left==null) left="";
		if(right==null) right="";
		
		return !left.equals(right);
	}
	
}