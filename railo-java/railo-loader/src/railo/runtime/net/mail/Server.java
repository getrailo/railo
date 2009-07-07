package railo.runtime.net.mail;

/**
 * DTO of a single Mailserver
 */
public interface Server {

	public static final int DEFAULT_PORT = 25;
	
    /**
     * @return Returns the password.
     */
    public abstract String getPassword();

    /**
     * @return Returns the port.
     */
    public abstract int getPort();

    /**
     * @return Returns the server.
     */
    public abstract String getHostName();

    /**
     * @return Returns the username.
     */
    public abstract String getUsername();

    /**
     * @return if has a authenatication or not
     */
    public abstract boolean hasAuthentication();

    /**
     * @return clone the DataSource as ReadOnly
     */
    public abstract Server cloneReadOnly();

    /**
     * @return Returns the readOnly.
     */
    public abstract boolean isReadOnly();

    /**
     * verify the server properties 
     * @return is ok
     * @throws SMTPException 
     */
    public abstract boolean verify() throws SMTPException;

    
	/**
	 * @return is tls
	 */
	public abstract boolean isTLS();

	/**
	 * @return is ssl
	 */
	public abstract boolean isSSL();

}