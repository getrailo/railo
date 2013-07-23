package railo.runtime.config;


import railo.runtime.crypt.CFMXCompat;
import railo.runtime.exp.PageException;
import railo.runtime.functions.other.Encrypt;
import railo.runtime.net.proxy.ProxyData;
import railo.runtime.net.rpc.client.RPCClient;
import railo.runtime.op.Caster;
import railo.runtime.spooler.remote.RemoteClientTask;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.util.KeyConstants;
import railo.runtime.type.util.ListUtil;

public class RemoteClientImpl implements RemoteClient {

	private String url;
	private String serverUsername;
	private String serverPassword;
	private ProxyData proxyData;
	private String type;
	private String adminPassword;
	private String securityKey;
	private String label;
	private String usage;
	private String id;

	public RemoteClientImpl(String label,String type, String url, String serverUsername, String serverPassword,String adminPassword, ProxyData proxyData, String securityKey,String usage) {
		this.label = label;
		this.url = url;
		this.serverUsername = serverUsername;
		this.serverPassword = serverPassword;
		this.proxyData = proxyData;
		this.type = type;
		this.adminPassword = adminPassword;
		this.securityKey = securityKey;
		this.usage = usage;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @return the serverUsername
	 */
	public String getServerUsername() {
		return serverUsername;
	}

	/**
	 * @return the serverPassword
	 */
	public String getServerPassword() {
		return serverPassword;
	}

	/**
	 * @return the proxyData
	 */
	public ProxyData getProxyData() {
		return proxyData;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return the adminPassword
	 */
	public String getAdminPassword() {
		return adminPassword;
	}

	/**
	 * @return the securityKey
	 */
	public String getSecurityKey() {
		return securityKey;
	}

	public String getAdminPasswordEncrypted() {
		try {
			return Encrypt.invoke( getAdminPassword(), getSecurityKey(), CFMXCompat.ALGORITHM_NAME, "uu", null, 0 );
		} 
		catch (PageException e) {
			return null;
		}
	}

	public String getLabel() {
		return label;
	}

	public String getUsage() {
		return usage;
	}

	public boolean hasUsage(String usage) {
		return ListUtil.listFindNoCaseIgnoreEmpty(this.usage,usage,',')!=-1 ;
	}

	public String getId(Config config) {

		if(id!=null) return id;
		
		Struct attrColl = new StructImpl();
		attrColl.setEL(KeyConstants._action, "getToken");
		
		Struct args = new StructImpl();
		args.setEL(KeyConstants._type, getType());
		args.setEL(RemoteClientTask.PASSWORD, getAdminPasswordEncrypted());
		args.setEL(RemoteClientTask.CALLER_ID, "undefined");
		args.setEL(RemoteClientTask.ATTRIBUTE_COLLECTION, attrColl);
		
		
		
		try {
			RPCClient rpc = RemoteClientTask.getRPCClient(this);
			Object result = rpc.callWithNamedValues(config, "invoke", args);
			return id=ConfigImpl.getId(securityKey, Caster.toString(result,null),false, null);
			
		} 
		catch (Throwable t) {t.printStackTrace();
			return null;
		}
	}
	

}
