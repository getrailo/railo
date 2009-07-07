package railo.runtime.config;

import java.io.Serializable;

import railo.runtime.net.proxy.ProxyData;

public interface RemoteClient extends Serializable {

	/**
	 * @return the url
	 */
	public String getUrl();

	/**
	 * @return the serverUsername
	 */
	public String getServerUsername();

	/**
	 * @return the serverPassword
	 */
	public String getServerPassword();

	/**
	 * @return the proxyData
	 */
	public ProxyData getProxyData();

	/**
	 * @return the type
	 */
	public String getType();

	/**
	 * @return the adminPassword
	 */
	public String getAdminPassword();

	/**
	 * @return the securityKey
	 */
	public String getSecurityKey();

	public String getAdminPasswordEncrypted();

	public String getLabel();

	public String getUsage();

	public boolean hasUsage(String usage);

	public String getId(Config config);
	
// TODO doc
}
