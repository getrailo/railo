package railo.commons.io.res.type.ftp;



import java.io.IOException;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

import railo.commons.io.res.Resource;
import railo.commons.io.res.ResourceProvider;
import railo.commons.io.res.ResourceProviderPro;
import railo.commons.io.res.Resources;
import railo.commons.io.res.util.ResourceLockImpl;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.SizeOf;
import railo.commons.lang.StringUtil;
import railo.runtime.net.proxy.Proxy;
import railo.runtime.op.Caster;
import railo.runtime.type.Sizeable;

// TODO check connection timeout
public final class FTPResourceProvider implements ResourceProviderPro,Sizeable {
	private String scheme="ftp";
	private final Map clients=new HashMap();
	private int clientTimeout=60000;
	private int socketTimeout=-1;
	private int lockTimeout=20000;
	private int cache=20000;

	private FTPResourceClientCloser closer=null;
	private final ResourceLockImpl lock=new ResourceLockImpl(lockTimeout,true);
	private Map arguments;

	public ResourceProvider init(String scheme, Map arguments) {
		setScheme(scheme);
		
		if(arguments!=null) {
			this.arguments=arguments;
			// client-timeout
			String strTimeout=(String) arguments.get("client-timeout");
			if(strTimeout!=null) {
				clientTimeout=Caster.toIntValue(strTimeout,clientTimeout);
			}
			// socket-timeout
			strTimeout=(String) arguments.get("socket-timeout");
			if(strTimeout!=null) {
				socketTimeout=Caster.toIntValue(strTimeout,socketTimeout);
			}
			// lock-timeout
			strTimeout=(String) arguments.get("lock-timeout");
			if(strTimeout!=null) {
				lockTimeout=Caster.toIntValue(strTimeout,lockTimeout);
			}
			// cache
			String strCache=(String) arguments.get("cache");
			if(strCache!=null) {
				cache=Caster.toIntValue(strCache,cache);
			}
		}
		lock.setLockTimeout(lockTimeout);
		
		return this;
	}
	
	@Override
	public Resource getResource(String path) {
		path=ResourceUtil.removeScheme(scheme,path);
		FTPConnectionData data=new FTPConnectionData();
		path=data.load(path);
		
		return 	new FTPResource(this,data,path);
	}
		
		
	

	FTPResourceClient getClient(FTPConnectionData data) throws IOException {
		
		FTPResourceClient client=(FTPResourceClient) clients.remove(data.key());
		if(client==null) {
			client = new FTPResourceClient(data,cache);
			if(socketTimeout>0)client.setSoTimeout(socketTimeout);
		}
		
		if(!client.isConnected()) { 
			if(data.hasProxyData()) {
				try {
		        	Proxy.start(
		            		data.getProxyserver(), 
		            		data.getProxyport(), 
		            		data.getProxyuser(), 
		            		data.getProxypassword()
		            );
		        	connect(client,data);
		        }
		        finally {
		        	Proxy.end();
		        }
			}
			else {
				connect(client,data);
			}
			
			int replyCode = client.getReplyCode();
			if(replyCode>=400)
				throw new FTPException(replyCode);
		}
		startCloser();
		return client;
	}
		
	private synchronized void startCloser() {
		if(closer==null || !closer.isAlive()) {
			closer=new FTPResourceClientCloser(this);
			closer.start();
		}
		
	}
	private void connect(FTPResourceClient client, FTPConnectionData data) throws SocketException, IOException {
		if(data.port>0)client.connect(data.host,data.port);
		else client.connect(data.host);
		if(!StringUtil.isEmpty(data.username))client.login(data.username,data.password);
	}

	public void returnClient(FTPResourceClient client) {
		if(client==null)return;
		client.touch();
		clients.put(client.getFtpConnectionData().key(), client);
	}

	@Override
	public String getScheme() {
		return scheme;
	}

	public void setScheme(String scheme) {
		if(!StringUtil.isEmpty(scheme))this.scheme=scheme;
	}

	@Override
	public void setResources(Resources resources) {
		//this.resources=resources;
	}
	
	@Override
	public void lock(Resource res) throws IOException {
		lock.lock(res);
	}

	@Override
	public void unlock(Resource res) {
		lock.unlock(res);
	}

	@Override
	public void read(Resource res) throws IOException {
		lock.read(res);
	}
	
	public void clean() {
		Object[] keys = clients.keySet().toArray();
		FTPResourceClient client;
		for(int i=0;i<keys.length;i++) {
			client=(FTPResourceClient) clients.get(keys[i]);
			if(client.getLastAccess()+clientTimeout<System.currentTimeMillis()) {
				//railo.print.ln("disconnect:"+client.getFtpConnectionData().key());
				if(client.isConnected()) {
					try {
						client.disconnect();
					} 
					catch (IOException e) {}
				}
				clients.remove(client.getFtpConnectionData().key());
			}
		}
	}
	
	class FTPResourceClientCloser extends Thread {

		private FTPResourceProvider provider;

		public FTPResourceClientCloser(FTPResourceProvider provider) {
			this.provider=provider;
		}
		
		public void run() {
			//railo.print.ln("closer start");
			do {
				sleepEL();
				provider.clean();
			}
			while(!clients.isEmpty());
			//railo.print.ln("closer stop");
		}

		private void sleepEL() {
			try {
				sleep(provider.clientTimeout);
			} catch (InterruptedException e) {}		
		}
	}
	
	/**
	 * @return the cache
	 */
	public int getCache() {
		return cache;
	}

	@Override
	public boolean isAttributesSupported() {
		return false;
	}

	public boolean isCaseSensitive() {
		return true;
	}

	@Override
	public boolean isModeSupported() {
		return true;
	}

	@Override
	public long sizeOf() {
		return SizeOf.size(lock)+SizeOf.size(clients);
	}

	@Override
	public Map getArguments() {
		return arguments;
	}
	
	@Override
	public char getSeparator() {
		return '/';
	}

}

