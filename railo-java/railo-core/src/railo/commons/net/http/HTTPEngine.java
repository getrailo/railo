package railo.commons.net.http;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import railo.commons.io.TemporaryStream;
import railo.commons.io.res.Resource;
import railo.commons.net.http.httpclient3.HTTPEngine3Impl;
import railo.commons.net.http.httpclient4.HTTPEngine4Impl;
import railo.commons.net.http.httpclient4.HeaderImpl;
import railo.runtime.net.proxy.ProxyData;
import railo.runtime.type.util.CollectionUtil;

public class HTTPEngine {
	
	private static final boolean use4=true;

    /**
     * Field <code>ACTION_POST</code>
     */
    public static final short ACTION_POST=0;
    
    /**
     * Field <code>ACTION_GET</code>
     */
    public static final short ACTION_GET=1;

	/**
	 * Field <code>STATUS_OK</code>
	 */
	public static final int STATUS_OK=200;
	//private static final String NO_MIMETYPE="Unable to determine MIME type of file.";

	public static final int MAX_REDIRECT = 15;
	
    
    /**
     * Constant value for HTTP Status Code "moved Permanently 301"
     */
    public static final int STATUS_REDIRECT_MOVED_PERMANENTLY=301;
    /**
     * Constant value for HTTP Status Code "Found 302"
     */
    public static final int STATUS_REDIRECT_FOUND=302;
    /**
     * Constant value for HTTP Status Code "see other 303"
     */
    public static final int STATUS_REDIRECT_SEE_OTHER=303;
	
	

	
	public static HTTPResponse get(URL url) throws IOException { 
        if(use4) return HTTPEngine4Impl.get(url, null, null, -1,MAX_REDIRECT, null, null, null, null);
		return HTTPEngine3Impl.get(url, null, null, -1,MAX_REDIRECT, null, null, null, null);
    }
    
    public static HTTPResponse post(URL url) throws IOException {
    	if(use4) return HTTPEngine4Impl.post(url, null, null, -1,MAX_REDIRECT, null, null, null, null);
    	return HTTPEngine3Impl.post(url, null, null, -1,MAX_REDIRECT, null, null, null, null,null);
    }
	
	public static HTTPResponse get(URL url, String username, String password, long timeout, int maxRedirect,
	        String charset, String useragent,ProxyData proxy, Header[] headers) throws IOException { 
		if(use4) return HTTPEngine4Impl.get(url, username, password, timeout, maxRedirect, charset, useragent, proxy, headers);
		return HTTPEngine3Impl.get(url, username, password, timeout, maxRedirect, charset, useragent, proxy, headers);
    }

	public static HTTPResponse post(URL url, String username, String password, long timeout, int maxRedirect,
            String charset, String useragent, ProxyData proxy, Map<String,String> headers, Map<String,String> params) throws IOException {
        	if(use4) return HTTPEngine4Impl.post(url, username, password, timeout, maxRedirect, charset, useragent, proxy, toHeaders(headers),params);
        	return HTTPEngine3Impl.post(url, username, password, timeout, maxRedirect, charset, useragent, proxy, toHeaders(headers),params);
        }
    
    public static HTTPResponse head(URL url, String username, String password, int timeout, int maxRedirect,
        String charset, String useragent,ProxyData proxy, Header[] headers) throws IOException {
    	if(use4) return HTTPEngine4Impl.head(url, username, password, timeout, maxRedirect, charset, useragent, proxy, headers);
    	return HTTPEngine3Impl.head(url, username, password, timeout, maxRedirect, charset, useragent, proxy, headers);
	}
    
	public static HTTPResponse put(URL url, String username, String password, int timeout, int maxRedirect,
		String mimetype,String charset, String useragent,ProxyData proxy, Header[] headers, Object body) throws IOException {
		if(use4) return HTTPEngine4Impl.put(url, username, password, timeout, maxRedirect, mimetype,charset, useragent, proxy, headers,body);     
		return HTTPEngine3Impl.put(url, username, password, timeout, maxRedirect, charset, useragent, proxy, headers,body);     
	}
    
    public static HTTPResponse delete(URL url, String username, String password, int timeout, int maxRedirect,
        String charset, String useragent,ProxyData proxy, Header[] headers) throws IOException {
    	if(use4) return HTTPEngine4Impl.delete(url, username, password, timeout, maxRedirect, charset, useragent, proxy, headers);
    	return HTTPEngine3Impl.delete(url, username, password, timeout, maxRedirect, charset, useragent, proxy, headers);
	}

	public static Header header(String name, String value) {
		if(use4) return HTTPEngine4Impl.header(name, value);
		return HTTPEngine3Impl.header(name, value);
	}

	public static Entity getEmptyEntity(String contentType) {
		if(use4) return HTTPEngine4Impl.getEmptyEntity(contentType);
		return HTTPEngine3Impl.getEmptyEntity(contentType);
	}
	
	public static Entity getByteArrayEntity(byte[] barr, String contentType) {
		if(use4) return HTTPEngine4Impl.getByteArrayEntity(barr,contentType);
		return HTTPEngine3Impl.getByteArrayEntity(barr,contentType);
	}
	
	public static Entity getTemporaryStreamEntity(TemporaryStream ts, String contentType) {
		if(use4) return HTTPEngine4Impl.getTemporaryStreamEntity(ts,contentType);
		return HTTPEngine3Impl.getTemporaryStreamEntity(ts,contentType);
	}
	
	public static Entity getResourceEntity(Resource res, String contentType) {
		if(use4) return HTTPEngine4Impl.getResourceEntity(res,contentType);
		return HTTPEngine3Impl.getResourceEntity(res,contentType);
	}
	
    private static Header[] toHeaders(Map<String, String> headers) {
    	if(CollectionUtil.isEmpty(headers)) return null;
    	Header[] rtn=new Header[headers.size()];
    	Iterator<Entry<String, String>> it = headers.entrySet().iterator();
    	Entry<String, String> e;
    	int index=0;
    	while(it.hasNext()){
    		e = it.next();
    		rtn[index++]=new HeaderImpl(e.getKey(),e.getValue());
    	}
    	return rtn;
	}
	
	
}
