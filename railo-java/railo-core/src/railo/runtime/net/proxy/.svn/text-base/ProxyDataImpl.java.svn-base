package railo.runtime.net.proxy;

import railo.commons.lang.StringUtil;

public class ProxyDataImpl implements ProxyData {
	
	public static final ProxyData NO_PROXY = new ProxyDataImpl();
	
	private String server;
	private int port=-1;
	private String username;
	private String password;
	

	public ProxyDataImpl(String server, int port, String username, String password) {
		if(!StringUtil.isEmpty(server,true))this.server = server;
		if(port>0)this.port = port;
		if(!StringUtil.isEmpty(username,true))this.username = username;
		if(!StringUtil.isEmpty(password,true))this.password = password;
	}
	public ProxyDataImpl() {}

	public void release() {
		server=null;
		port=-1;
		username=null;
		password=null;
	}
	
	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}
	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}
	/**
	 * @return the server
	 */
	public String getServer() {
		return server;
	}
	/**
	 * @param server the server to set
	 */
	public void setServer(String server) {
		this.server = server;
	}
	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}
}
