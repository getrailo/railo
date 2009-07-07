package railo.runtime.net.proxy;

import java.io.Serializable;

public interface ProxyData extends Serializable {
	
	public void release();
	
	/**
	 * @return the password
	 */
	public String getPassword();
	
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password);
	
	/**
	 * @return the port
	 */
	public int getPort();
	
	/**
	 * @param port the port to set
	 */
	public void setPort(int port);
	
	/**
	 * @return the server
	 */
	public String getServer();
	
	/**
	 * @param server the server to set
	 */
	public void setServer(String server);
	
	/**
	 * @return the username
	 */
	public String getUsername();
	
	/**
	 * @param username the username to set
	 */
	public void setUsername(String username);
}
