package railo.commons.io.res.type.s3;

import java.io.IOException;
import java.util.Map;

import railo.commons.io.res.Resource;
import railo.commons.io.res.ResourceLock;
import railo.commons.io.res.ResourceProvider;
import railo.commons.io.res.Resources;
import railo.commons.lang.StringUtil;
import railo.commons.lang.types.RefInteger;
import railo.commons.lang.types.RefIntegerImpl;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.net.s3.Properties;
import railo.runtime.util.ApplicationContextPro;

public final class S3ResourceProvider implements ResourceProvider {
	
	
	private int socketTimeout=-1;
	private int lockTimeout=20000;
	private int cache=20000;
	private ResourceLock lock;
	private String scheme="s3";
	private Map arguments;

	

	
	/**
	 * initalize ram resource
	 * @param scheme
	 * @param arguments
	 * @return RamResource
	 */
	public ResourceProvider init(String scheme,Map arguments) {
		if(!StringUtil.isEmpty(scheme))this.scheme=scheme;
		
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
		
		
		//path=loadWithOldPattern(s3,storage,path);
		path=loadWithNewPattern(s3,storage,path);
		
		return new S3Resource(s3,storage.toInt(),this,path,true);
	}

	
	public static String loadWithNewPattern(S3 s3,RefInteger storage, String path) {
		Properties prop=((ApplicationContextPro)ThreadLocalPageContext.get().getApplicationContext()).getS3();
		
		int defaultLocation = prop.getDefaultLocation();
		storage.setValue(defaultLocation);
		String accessKeyId = prop.getAccessKeyId();
		String secretAccessKey = prop.getSecretAccessKey();
		
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
					storage.setValue(S3.toIntStorage(strStorage, defaultLocation));
				}
			}
			else accessKeyId=path.substring(0,atIndex);
		}
		path=prettifyPath(path.substring(atIndex+1));
		index=path.indexOf('/');
		s3.setHost(prop.getHost());
		if(index==-1){
			if(path.equalsIgnoreCase(S3Constants.HOST) || path.equalsIgnoreCase(prop.getHost())){
				s3.setHost(path);
				path="/";
			}
		}
		else {
			String host=path.substring(0,index);
			if(host.equalsIgnoreCase(S3Constants.HOST) || host.equalsIgnoreCase(prop.getHost())){
				s3.setHost(host);
				path=path.substring(index);
			}
		}
		
		
		s3.setSecretAccessKey(secretAccessKey);
		s3.setAccessKeyId(accessKeyId);
		
		return path;
	}

	/*public static void main(String[] args) {
		// s3://bucket/x/y/sample.txt
		// s3://accessKeyId:awsSecretKey@bucket/x/y/sample.txt
		String secretAccessKey="R/sOy3hgimrI8D9c0lFHchoivecnOZ8LyVmJpRFQ";
		String accessKeyId="1DHC5C5FVD7YEPR4DBG2";
		
		Properties prop=new Properties();
		prop.setAccessKeyId(accessKeyId);
		prop.setSecretAccessKey(secretAccessKey);
		
		
		test("s3://"+accessKeyId+":"+secretAccessKey+"@s3.amazonaws.com/dudi/peter.txt");
		test("s3://"+accessKeyId+":"+secretAccessKey+"@dudi/peter.txt");
		test("s3:///dudi/peter.txt");
		test("s3://dudi/peter.txt");
		
		
	}
	
	
	
	private static void test(String path) {

		Properties prop=new Properties();
		prop.setAccessKeyId("123456");
		prop.setSecretAccessKey("abcdefghji");
		
		
		
		String scheme="s3";
		path=railo.commons.io.res.util.ResourceUtil.removeScheme(scheme, path);
		S3 s3 = new S3();
		RefInteger storage=new RefIntegerImpl(S3.STORAGE_UNKNOW);
		path=loadWithNewPattern(s3,prop,storage,path);
		

		print.o(s3);
		print.o(path);
	}*/

	private static String prettifyPath(String path) {
		path=path.replace('\\','/');
		return StringUtil.replace(path, "//", "/", false);
		// TODO /aaa/../bbb/
	}
	
	
	

	public static String loadWithOldPattern(S3 s3,RefInteger storage, String path) {
		
		
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

	
	

}
