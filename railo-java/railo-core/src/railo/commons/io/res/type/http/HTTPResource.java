package railo.commons.io.res.type.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpMethod;

import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.ResourceProvider;
import railo.commons.io.res.util.ReadOnlyResourceSupport;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.StringUtil;
import railo.commons.net.HTTPUtil;
import railo.runtime.net.proxy.ProxyData;
import railo.runtime.net.proxy.ProxyDataImpl;
import railo.runtime.op.Caster;


public class HTTPResource extends ReadOnlyResourceSupport {
	
	
	private final HTTPResourceProvider provider;
	private final HTTPConnectionData data;
	private final String path;
	private final String name;
	private HttpMethod http;
	

	public HTTPResource(HTTPResourceProvider provider, HTTPConnectionData data) {
		this.provider=provider;
		this.data=data;

		String[] pathName=ResourceUtil.translatePathName(data.path);
		this.path=pathName[0];
		this.name=pathName[1];

	}

	private HttpMethod getHttpMethod(boolean create) throws IOException {
		if(create || http==null) {
			//URL url = HTTPUtil.toURL("http://"+data.host+":"+data.port+"/"+data.path);
			URL url = new URL(provider.getProtocol(),data.host,data.port,data.path);
			// TODO Support for proxy
			ProxyData pd=data.hasProxyData()?data.proxyData:ProxyDataImpl.NO_PROXY;
				
			
			http = HTTPUtil.invoke(url, data.username, data.password, data.timeout,null, data.userAgent, 
					pd.getServer(), pd.getPort(),pd.getUsername(), pd.getPassword(),
					null);
		}
		return http;
	}

	private int getStatusCode() throws IOException {
		if(http==null) {
			URL url = new URL(provider.getProtocol(),data.host,data.port,data.path);
			ProxyData pd=data.hasProxyData()?data.proxyData:ProxyDataImpl.NO_PROXY;
			
			return HTTPUtil.head(url, data.username, data.password, provider.getSocketTimeout(), 
					null, data.userAgent, 
					pd.getServer(), pd.getPort(),pd.getUsername(), pd.getPassword(),
					null).getStatusCode();
		}
		return http.getStatusCode();
	}

	public boolean exists() {
		try {
			provider.read(this);
			int code = getStatusCode();//getHttpMethod().getStatusCode();
			return code!=404;
		}
		catch (IOException e) {
			return false;
		}
	}
	
	public int statusCode() {
		try {
			provider.read(this);
			return getHttpMethod(false).getStatusCode();
		} catch (IOException e) {
			return 0;
		}
	}

	public InputStream getInputStream() throws IOException {
		ResourceUtil.checkGetInputStreamOK(this);
		//provider.lock(this);
		provider.read(this);
		HttpMethod method = getHttpMethod(true);
		try {
			return IOUtil.toBufferedInputStream(method.getResponseBodyAsStream());
		} 
		catch (IOException e) {
			//provider.unlock(this);
			throw e;
		}
	}

	/**
	 * @see railo.commons.io.res.Resource#getName()
	 */
	public String getName() {
		return name;
	}

	/**
	 * @see railo.commons.io.res.Resource#getParent()
	 */
	public String getParent() {
		if(isRoot()) return null;
		return provider.getProtocol().concat("://").concat(data.key()).concat(path.substring(0,path.length()-1));
	}

	private boolean isRoot() {
		return StringUtil.isEmpty(name);
	}

	/**
	 * @see railo.commons.io.res.Resource#getParentResource()
	 */
	public Resource getParentResource() {
		if(isRoot()) return null;
		return new HTTPResource(provider,
				new HTTPConnectionData(data.username,data.password,data.host,data.port,path,data.proxyData,data.userAgent));
	}

	/**
	 * @see railo.commons.io.res.Resource#getPath()
	 */
	public String getPath() {
		return provider.getProtocol().concat("://").concat(data.key()).concat(path).concat(name);
	}

	/**
	 * @see railo.commons.io.res.Resource#getRealResource(java.lang.String)
	 */
	public Resource getRealResource(String realpath) {
		realpath=ResourceUtil.merge(path.concat(name), realpath);
		if(realpath.startsWith("../"))return null;
		return new HTTPResource(provider,new HTTPConnectionData(data.username,data.password,data.host,data.port,realpath,data.proxyData,data.userAgent));
	}

	/**
	 * @see railo.commons.io.res.Resource#getResourceProvider()
	 */
	public ResourceProvider getResourceProvider() {
		return provider;
	}

	/**
	 * @see railo.commons.io.res.Resource#isAbsolute()
	 */
	public boolean isAbsolute() {
		return true;
	}

	/**
	 * @see railo.commons.io.res.Resource#isDirectory()
	 */
	public boolean isDirectory() {
		return false;
	}

	/**
	 * @see railo.commons.io.res.Resource#isFile()
	 */
	public boolean isFile() {
		return exists();
	}

	/**
	 * @see railo.commons.io.res.Resource#isReadable()
	 */
	public boolean isReadable() {
		return exists();
	}

	public long lastModified() {
		int last=0;
		try {
			Header cl=getHttpMethod(false).getResponseHeader("last-modified");
			if(cl!=null && exists()) last=Caster.toIntValue(cl.getValue(),0);
		}
		catch (IOException e) {}
		return last;
	}

	public long length() {
		try {
			if(!exists()) return 0;
			//Header content length
			Header cl=getHttpMethod(false).getResponseHeader("content-length");
			if(cl!=null)	{
				int length=Caster.toIntValue(cl.getValue(),-1);
				if(length!=-1) return length;
			}
			
			provider.read(this);
			HttpMethod method = getHttpMethod(true);
			InputStream is = method.getResponseBodyAsStream();
			byte[] buffer = new byte[1024];
	        int len;
	        int length=0;
	        while((len = is.read(buffer)) !=-1){
	          length+=len;
	        }
			return length;
			
			
		} catch (IOException e) {
			return 0;
		}
	}

	public Resource[] listResources() {
		return null;
	}

	public void setProxyData(ProxyData pd) {
		this.http=null;
		this.data.setProxyData(pd);
	}

	public void setUserAgent(String userAgent) {
		this.http=null;
		this.data.userAgent=userAgent;
	}

	public void setTimeout(int timeout) {
		this.http=null;
		data.timeout=timeout;
	}

}
