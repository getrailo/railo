package railo.commons.io.res.type.s3;

import java.io.IOException;
import java.util.Map;

import railo.commons.io.res.Resource;
import railo.commons.io.res.ResourceLock;
import railo.commons.io.res.ResourceProvider;
import railo.commons.io.res.Resources;
import railo.commons.io.res.util.ResourceUtilImpl;
import railo.commons.lang.types.RefInteger;
import railo.loader.engine.CFMLEngineFactory;
import railo.loader.util.Util;
import railo.runtime.util.Cast;
import railo.runtime.util.HTTPUtil;
import railo.runtime.util.ResourceUtil;

public final class S3ResourceProvider implements ResourceProvider {
	

	private static Cast cast;
	
	private int socketTimeout=-1;
	private int lockTimeout=20000;
	private int cache=20000;
	private ResourceLock lock;
	private String scheme="s3";
	private Map arguments;

	private ResourceUtil _resourceUtil;
	private HTTPUtil httpUtil;

	

	
	

	/* *
	 * @return the httpUtil
	 * /
	public HTTPUtil getHttpUtil() {
		if(httpUtil==null)httpUtil=CFMLEngineFactory. getInstance().getHTTPUtil();
		return httpUtil;
	}*/
	

	/*public ResourceUtil getResourceUtil() {
		if(_resourceUtil==null)
			_resourceUtil=CFMLEngineFactory. getInstance().getResourceUtil();
		return _resourceUtil;
	}*/
	


	/*private ResourceLock getLock() {
		if(_lock==null){
			_lock=getResourceUtil().createResourceLock(lockTimeout,true);
			_lock.setLockTimeout(lockTimeout);
		}
		return _lock;
	}*/
	
	/**
	 * initalize ram resource
	 * @param scheme
	 * @param arguments
	 * @return RamResource
	 */
	public ResourceProvider init(String scheme,Map arguments) {
		if(!Util.isEmpty(scheme))this.scheme=scheme;
		
		if(arguments!=null) {
			this.arguments=arguments;
			// socket-timeout
			String strTimeout = (String) arguments.get("socket-timeout");
			if(strTimeout!=null) {
				socketTimeout=toIntValue(strTimeout,socketTimeout);
			}
			// lock-timeout
			strTimeout=(String) arguments.get("lock-timeout");
			if(strTimeout!=null) {
				lockTimeout=toIntValue(strTimeout,lockTimeout);
			}
			// cache
			String strCache=(String) arguments.get("cache");
			if(strCache!=null) {
				cache=toIntValue(strCache,cache);
			}
		}
		
		return this;
	}

	private int toIntValue(String str, int defaultValue) {
		try{
			return Integer.parseInt(str);
		}
		catch(Throwable t){
			return defaultValue;
		}
	}


	/**
	 * @see railo.commons.io.res.ResourceProvider#getScheme()
	 */
	public String getScheme() {
		return scheme;
	}
	
	public Resource getResource(String path) {
		path=railo.commons.io.res.util.ResourceUtil.removeScheme(scheme, path);
		S3 s3 = new S3();
		RefInteger storage=new RefIntegerImpl(S3.STORAGE_UNKNOW);
		path=load(s3,storage,path);
		
		return new S3Resource(s3,storage.toInt(),this,path);
	}

	public String load(S3 s3,RefInteger storage, String path) {
		String accessKeyId = null;
		String secretAccessKey = null;
		String host = null;
		//int port = 21;
		
		//print.out("raw:"+path);
		
		int atIndex=path.indexOf('@');
		int slashIndex=path.indexOf('/');
		if(slashIndex==-1){
			slashIndex=path.length();
			path+="/";
		}
		int index;
		
		// key/id
		if(atIndex!=-1) {
			index=path.indexOf(':');
			if(index!=-1 && index<atIndex) {
				accessKeyId=path.substring(0,index);
				secretAccessKey=path.substring(index+1,atIndex);
				index=secretAccessKey.indexOf(':');
				if(index!=-1) {
					String strStorage=secretAccessKey.substring(index+1).trim().toLowerCase();
					secretAccessKey=secretAccessKey.substring(0,index);
					//print.out("storage:"+strStorage);
					storage.setValue(S3.toIntStorage(strStorage, S3.STORAGE_UNKNOW));
				}
			}
			else accessKeyId=path.substring(0,atIndex);
		}
		path=prettifyPath(path.substring(atIndex+1));
		index=path.indexOf('/');
		if(index==-1){
			host=path;
			path="/";
		}
		else {
			host=path.substring(0,index);
			path=path.substring(index);
		}
		
		s3.setHost(host);
		s3.setSecretAccessKey(secretAccessKey);
		s3.setAccessKeyId(accessKeyId);
		
		return path;
	}
	
	private static String prettifyPath(String path) {
		path=path.replace('\\','/');
		return Util.replace(path, "//", "/", false);
		// TODO /aaa/../bbb/
	}
	
	/**
	 * @see railo.commons.io.res.ResourceProvider#isAttributesSupported()
	 */
	public boolean isAttributesSupported() {
		return false;
	}

	/**
	 * @see railo.commons.io.res.ResourceProvider#isCaseSensitive()
	 */
	public boolean isCaseSensitive() {
		return true;
	}

	/**
	 * @see railo.commons.io.res.ResourceProvider#isModeSupported()
	 */
	public boolean isModeSupported() {
		return false;
	}

	/**
	 * @see railo.commons.io.res.ResourceProvider#lock(railo.commons.io.res.Resource)
	 */
	public void lock(Resource res) throws IOException {
		lock.lock(res);
	}

	/**
	 * @see railo.commons.io.res.ResourceProvider#read(railo.commons.io.res.Resource)
	 */
	public void read(Resource res) throws IOException {
		lock.read(res);
	}

	public void setResources(Resources res) {
		lock=res.createResourceLock(lockTimeout,true);
	}

	/**
	 * @see railo.commons.io.res.ResourceProvider#unlock(railo.commons.io.res.Resource)
	 */
	public void unlock(Resource res) {
		lock.unlock(res);
	}

	/**
	 * @return the socketTimeout
	 */
	public int getSocketTimeout() {
		return socketTimeout;
	}

	/**
	 * @return the lockTimeout
	 */
	public int getLockTimeout() {
		return lockTimeout;
	}

	/**
	 * @return the cache
	 */
	public int getCache() {
		return cache;
	}

	/**
	 * @see railo.commons.io.res.ResourceProvider#getArguments()
	 */
	public Map getArguments() {
		return arguments;
	}

	
	class RefIntegerImpl implements RefInteger{


	    private int value;

	    /**
	     * @param value
	     */
	    public RefIntegerImpl(int value) {
	        this.value=value;
	    }
	    public RefIntegerImpl() {
	    }
	    
	    /**
	     * @param value
	     */
	    public void setValue(int value) {
	        this.value = value;
	    }
	    
	    /**
	     * operation plus
	     * @param value
	     */
	    public void plus(int value) {
	        this.value+=value;
	    }
	    
	    /**
	     * operation minus
	     * @param value
	     */
	    public void minus(int value) {
	        this.value-=value;
	    }

	    /**
	     * @return returns value as integer
	     */
	    public Integer toInteger() {
	        return new Integer(value);
	    }
	    /**
	     * @return returns value as integer
	     */
	    public Double toDouble() {
	        return new Double(value);
	    }
	    

		/**
		 * @see railo.commons.lang.types.RefInteger#toDoubleValue()
		 */
		public double toDoubleValue() {
			return value;
		}
		
		/**
		 * @see railo.commons.lang.types.RefInteger#toInt()
		 */
		public int toInt() {
			return value;
		}
	    
	    
	    /**
	     * @see java.lang.Object#toString()
	     */
	    public String toString() {
	        return String.valueOf(value);
	    }
		
	}

}
