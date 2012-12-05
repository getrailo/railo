package railo.commons.io.res.type.http;

import java.io.IOException;
import java.util.Map;

import railo.commons.io.res.Resource;
import railo.commons.io.res.ResourceProvider;
import railo.commons.io.res.Resources;
import railo.commons.io.res.util.ResourceLockImpl;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.op.Caster;

public class HTTPResourceProvider implements ResourceProvider {


	private int lockTimeout=20000;
	private final ResourceLockImpl lock=new ResourceLockImpl(lockTimeout,false);
	private String scheme="http";
	private int clientTimeout=30000;
	private int socketTimeout=20000;
	private Map arguments;

	public String getScheme() {
		return scheme;
	}

	public String getProtocol() {
		return scheme;
	}

	public void setScheme(String scheme) {
		if(!StringUtil.isEmpty(scheme))this.scheme=scheme;
	}

	public ResourceProvider init(String scheme, Map arguments) {
		setScheme(scheme);
		
		if(arguments!=null) {
			this.arguments=arguments;
			// client-timeout
			String strTimeout=(String) arguments.get("client-timeout");
			if(strTimeout!=null) {
				clientTimeout = Caster.toIntValue(strTimeout,clientTimeout);
			}
			// socket-timeout
			strTimeout=(String) arguments.get("socket-timeout");
			if(strTimeout!=null) {
				socketTimeout=Caster.toIntValue(strTimeout,socketTimeout);
			}
			// lock-timeout
			strTimeout = (String) arguments.get("lock-timeout");
			if(strTimeout!=null) {
				lockTimeout=Caster.toIntValue(strTimeout,lockTimeout);
			}
		}
		lock.setLockTimeout(lockTimeout);
		return this;
	}
	

	@Override
	public Resource getResource(String path) {
		
		int indexQ=path.indexOf('?');
		if(indexQ!=-1){
			int indexS=path.lastIndexOf('/');
			while((indexS=path.lastIndexOf('/'))>indexQ){
				path=path.substring(0,indexS)+"%2F"+path.substring(indexS+1);	
			}
		}
		
		path=ResourceUtil.translatePath(ResourceUtil.removeScheme(scheme,path),false,false);
		
		return new HTTPResource(this,new HTTPConnectionData(path,getSocketTimeout()));
	}

	public boolean isAttributesSupported() {
		return false;
	}

	public boolean isCaseSensitive() {
		return false;
	}

	public boolean isModeSupported() {
		return false;
	}

	public void setResources(Resources resources) {
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

	/**
	 * @return the clientTimeout
	 */
	public int getClientTimeout() {
		return clientTimeout;
	}

	/**
	 * @return the lockTimeout
	 */
	public int getLockTimeout() {
		return lockTimeout;
	}

	/**
	 * @return the socketTimeout
	 */
	public int getSocketTimeout() {
		return socketTimeout;
	}


	@Override
	public Map getArguments() {
		return arguments;
	}
}
