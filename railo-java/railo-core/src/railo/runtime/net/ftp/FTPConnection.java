package railo.runtime.net.ftp;

/**
 * represent a ftp connection
 */
public interface FTPConnection {

    /**
     * @return Returns the name.
     */
    public abstract String getName();

    /**
     * @return Returns the password.
     */
    public abstract String getPassword();

    /**
     * @return Returns the server.
     */
    public abstract String getServer();

    /**
     * @return Returns the username.
     */
    public abstract String getUsername();

    /**
     * @return returns if has logindata or not
     */
    public abstract boolean hasLoginData();

    /**
     * @return has name
     */
    public abstract boolean hasName();

    /**
     * @return Returns the port.
     */
    public abstract int getPort();

    /**
     * @return Returns the timeout.
     */
    public abstract int getTimeout();

    /**
     * @return Returns the transferMode.
     */
    public abstract short getTransferMode();

    /**
     * @return Returns the passive.
     */
    public abstract boolean isPassive();

    /**
     * @param conn
     * @return has equal login
     */
    public abstract boolean loginEquals(FTPConnection conn);

    /**
     * @return Returns the proxyserver.
     */
    public String getProxyServer();
    
	public int getProxyPort();

	/**
	 * return the proxy username
	 * @return proxy username
	 */
	public String getProxyUser();

    
	/**
	 * return the proxy password
	 * @return proxy password
	 */
	public String getProxyPassword();
	
}