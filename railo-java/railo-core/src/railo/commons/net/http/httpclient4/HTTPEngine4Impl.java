package railo.commons.net.http.httpclient4;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpMessage;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import railo.commons.io.IOUtil;
import railo.commons.io.TemporaryStream;
import railo.commons.io.res.Resource;
import railo.commons.lang.ExceptionUtil;
import railo.commons.lang.StringUtil;
import railo.commons.net.http.Entity;
import railo.commons.net.http.HTTPResponse;
import railo.commons.net.http.httpclient4.entity.ByteArrayHttpEntity;
import railo.commons.net.http.httpclient4.entity.EmptyHttpEntity;
import railo.commons.net.http.httpclient4.entity.ResourceHttpEntity;
import railo.commons.net.http.httpclient4.entity.TemporaryStreamHttpEntity;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.PageException;
import railo.runtime.net.http.ReqRspUtil;
import railo.runtime.net.proxy.ProxyData;
import railo.runtime.net.proxy.ProxyDataImpl;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.type.util.CollectionUtil;

public class HTTPEngine4Impl {
	
	/**
	 * does a http get request
	 * @param url
	 * @param username
	 * @param password
	 * @param timeout
	 * @param charset
	 * @param useragent
	 * @param proxyserver
	 * @param proxyport
	 * @param proxyuser
	 * @param proxypassword
	 * @param headers
	 * @return
	 * @throws IOException
	 */
	public static HTTPResponse get(URL url, String username,String password, long timeout,  int maxRedirect,
            String charset, String useragent,
            ProxyData proxy, railo.commons.net.http.Header[] headers) throws IOException {
		HttpGet get = new HttpGet(url.toExternalForm());
		return _invoke(url,get, username, password, timeout,maxRedirect, charset, useragent, proxy, headers,null);
	}

    /**
	 * does a http post request
     * @param url
     * @param username
     * @param password
     * @param timeout
     * @param charset
     * @param useragent
     * @param proxyserver
     * @param proxyport
     * @param proxyuser
     * @param proxypassword
     * @param headers
     * @return
     * @throws IOException
     */
    public static HTTPResponse post(URL url, String username,String password, long timeout,  int maxRedirect,
            String charset, String useragent,
            ProxyData proxy, railo.commons.net.http.Header[] headers) throws IOException {
    	HttpPost post = new HttpPost(url.toExternalForm());
    	return _invoke(url,post, username, password, timeout,maxRedirect, charset, useragent, proxy, headers,null);
    }
    

    

    public static HTTPResponse post(URL url, String username,String password, long timeout, int maxRedirect,
            String charset, String useragent,
            ProxyData proxy, railo.commons.net.http.Header[] headers,Map<String,String> formfields) throws IOException {
    	HttpPost post = new HttpPost(url.toExternalForm());
    	
    	return _invoke(url,post, username, password, timeout,maxRedirect, charset, useragent, proxy, headers,formfields);
    }
    
    
    /**
	 * does a http put request
     * @param url
     * @param username
     * @param password
     * @param timeout
     * @param charset
     * @param useragent
     * @param proxyserver
     * @param proxyport
     * @param proxyuser
     * @param proxypassword
     * @param headers
     * @param body
     * @return
     * @throws IOException
     * @throws PageException 
     */
    public static HTTPResponse put(URL url, String username,String password, long timeout,  int maxRedirect,
    		String mimetype,String charset, String useragent,
            ProxyData proxy, railo.commons.net.http.Header[] headers, Object body) throws IOException {
		HttpPut put= new HttpPut(url.toExternalForm());
		setBody(put,body,mimetype,charset);
        return _invoke(url,put, username, password, timeout, maxRedirect, charset, useragent, proxy, headers,null);
		 
	}
    
    /**
	 * does a http delete request
     * @param url
     * @param username
     * @param password
     * @param timeout
     * @param charset
     * @param useragent
     * @param proxyserver
     * @param proxyport
     * @param proxyuser
     * @param proxypassword
     * @param headers
     * @return
     * @throws IOException
     */
    public static HTTPResponse delete(URL url, String username,String password, long timeout,  int maxRedirect,
            String charset, String useragent,
            ProxyData proxy, railo.commons.net.http.Header[] headers) throws IOException {
    	HttpDelete delete= new HttpDelete(url.toExternalForm());
		return _invoke(url,delete, username, password, timeout, maxRedirect, charset, useragent, proxy, headers,null);    
	}

    /**
	 * does a http head request
     * @param url
     * @param username
     * @param password
     * @param timeout
     * @param charset
     * @param useragent
     * @param proxyserver
     * @param proxyport
     * @param proxyuser
     * @param proxypassword
     * @param headers
     * @return
     * @throws IOException
     */
    public static HTTPResponse head(URL url, String username,String password, long timeout, int maxRedirect,
            String charset, String useragent,
            ProxyData proxy, railo.commons.net.http.Header[] headers) throws IOException {
    	HttpHead head= new HttpHead(url.toExternalForm());
		return _invoke(url,head, username, password, timeout, maxRedirect, charset, useragent, proxy, headers,null);    
	}
    
	

	public static railo.commons.net.http.Header header(String name, String value) {
		return new HeaderImpl(name, value);
	}
	

	private static Header toHeader(railo.commons.net.http.Header header) {
		if(header instanceof Header) return (Header) header;
		if(header instanceof HeaderWrap) return ((HeaderWrap)header).header;
		return new HeaderImpl(header.getName(), header.getValue());
	}
	
	private static HTTPResponse _invoke(URL url,HttpUriRequest request,String username,String password, long timeout, int maxRedirect,
            String charset, String useragent,
            ProxyData proxy, railo.commons.net.http.Header[] headers, Map<String,String> formfields) throws IOException {
    	
    	// TODO HttpConnectionManager manager=new SimpleHttpConnectionManager();//MultiThreadedHttpConnectionManager();
		BasicHttpParams params = new BasicHttpParams();
    	DefaultHttpClient client = createClient(params,maxRedirect);
    	HttpHost hh=new HttpHost(url.getHost(),url.getPort());
    	setHeader(request,headers);
    	if(CollectionUtil.isEmpty(formfields))setContentType(request,charset);
    	setFormFields(request,formfields,charset);
    	setUserAgent(request,useragent);
    	setTimeout(params,timeout);
    	HttpContext context=setCredentials(client,hh, username, password,false);  
    	setProxy(client,request,proxy);
        if(context==null)context = new BasicHttpContext();
        return new HTTPResponse4Impl(url,context,request,client.execute(request,context));
    }
	
	private static void setFormFields(HttpUriRequest request, Map<String, String> formfields, String charset) throws IOException {
		if(!CollectionUtil.isEmpty(formfields)) {
			if(!(request instanceof HttpPost)) throw new IOException("form fields are only suppported for post request");
			HttpPost post=(HttpPost) request;
    		List<NameValuePair> list = new ArrayList<NameValuePair>();
        	Iterator<Entry<String, String>> it = formfields.entrySet().iterator();
        	Entry<String, String> e;
    		while(it.hasNext()){
    			e = it.next();
    			list.add(new BasicNameValuePair(e.getKey(),e.getValue()));
    		}
    		if(StringUtil.isEmpty(charset)) charset=ThreadLocalPageContext.getConfig().getWebCharset();
    		
    		post.setEntity(new org.apache.http.client.entity.UrlEncodedFormEntity(list,charset));
    	}
	}

	public static DefaultHttpClient createClient(BasicHttpParams params, int maxRedirect) {
    	params.setParameter(ClientPNames.HANDLE_REDIRECTS, maxRedirect==0?Boolean.FALSE:Boolean.TRUE);
    	if(maxRedirect>0)params.setParameter(ClientPNames.MAX_REDIRECTS, new Integer(maxRedirect));
    	params.setParameter(ClientPNames.REJECT_RELATIVE_REDIRECT, Boolean.FALSE);
    	return new DefaultHttpClient(params);
	}

	private static void setUserAgent(HttpMessage hm, String useragent) {
        if(useragent!=null)hm.setHeader("User-Agent",useragent);
	}

	private static void setContentType(HttpMessage hm, String charset) {
		if(charset!=null) hm.setHeader("Content-type", "text/html; charset="+charset);
	}

	private static void setHeader(HttpMessage hm,railo.commons.net.http.Header[] headers) {
		addHeader(hm, headers);
	}
	
	private static void addHeader(HttpMessage hm,railo.commons.net.http.Header[] headers) {
		if(headers!=null) {
        	for(int i=0;i<headers.length;i++)
        		hm.addHeader(toHeader(headers[i]));
        }
	}

	public static void setTimeout(HttpParams params, long timeout) {
        if(timeout>0){
        	HttpConnectionParams.setConnectionTimeout(params, (int)timeout);
        	HttpConnectionParams.setSoTimeout(params, (int)timeout);
        }
	}

	public static BasicHttpContext setCredentials(DefaultHttpClient client, HttpHost httpHost, String username,String password, boolean preAuth) {
        // set Username and Password
        if(!StringUtil.isEmpty(username,true)) {
            if(password==null)password="";
            CredentialsProvider cp = client.getCredentialsProvider();
            cp.setCredentials(
                new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT), 
                new UsernamePasswordCredentials(username,password));
            
            
            
            BasicHttpContext httpContext = new BasicHttpContext();
            if(preAuth) {
	            AuthCache authCache = new BasicAuthCache();
	            authCache.put(httpHost, new BasicScheme());
	            httpContext.setAttribute(ClientContext.AUTH_CACHE, authCache);
            }
            return httpContext;
        }
        return null;
	}
	
	public static void setNTCredentials(DefaultHttpClient client, String username,String password, String workStation, String domain) {
        // set Username and Password
        if(!StringUtil.isEmpty(username,true)) {
            if(password==null)password="";
            CredentialsProvider cp = client.getCredentialsProvider();
            cp.setCredentials(
                new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT), 
                new NTCredentials(username,password,workStation,domain));
            //httpMethod.setDoAuthentication( true );
        }
	}

    public static void setBody(HttpEntityEnclosingRequest req, Object body, String mimetype,String charset) throws IOException {
    	if(body!=null)req.setEntity(toHttpEntity(body,mimetype,charset));
	}

	public static void setProxy(DefaultHttpClient client, HttpUriRequest request, ProxyData proxy) {
		// set Proxy
        if(ProxyDataImpl.isValid(proxy)) {
        	HttpHost host = new HttpHost(proxy.getServer(), proxy.getPort()==-1?80:proxy.getPort());
        	client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, host);
            if(!StringUtil.isEmpty(proxy.getUsername())) {
                
                client.getCredentialsProvider().setCredentials(
                        new AuthScope(proxy.getServer(), proxy.getPort()),
                        new UsernamePasswordCredentials(proxy.getUsername(),proxy.getPassword()));
            }
        } 
	}
	
	public static void addCookie(DefaultHttpClient client, String domain, String name, String value, String path, String charset) {
		if(ReqRspUtil.needEncoding(name,false)) name=ReqRspUtil.encode(name, charset);
		if(ReqRspUtil.needEncoding(value,false)) value=ReqRspUtil.encode(value, charset);
		BasicClientCookie cookie = new BasicClientCookie(name, value);
		if(!StringUtil.isEmpty(domain,true))cookie.setDomain(domain);
		if(!StringUtil.isEmpty(path,true))cookie.setPath(path);
		
		client.getCookieStore().addCookie(cookie);
	}

	/**
	 * convert input to  HTTP Entity
	 * @param value
	 * @param mimetype not used for binary input
	 * @param charset not used for binary input
	 * @return
	 * @throws IOException
	 */
	private static HttpEntity toHttpEntity(Object value, String mimetype, String charset) throws IOException {
		if(value instanceof HttpEntity) return (HttpEntity) value;
    	try{
	    	if(value instanceof InputStream) {
	    		return new ByteArrayEntity(IOUtil.toBytes((InputStream)value));
			}
			else if(Decision.isCastableToBinary(value,false)){
				return new ByteArrayEntity(Caster.toBinary(value));
			}
			else {
				return new StringEntity(Caster.toString(value),mimetype,charset);
			}
    	}
    	catch(Exception e){
    		throw ExceptionUtil.toIOException(e);
    	}
    }
	

	public static Entity getEmptyEntity(String contentType) {
		return new EmptyHttpEntity(contentType);
	}

	public static Entity getByteArrayEntity(byte[] barr, String contentType) {
		return new ByteArrayHttpEntity(barr,contentType);
	}

	public static Entity getTemporaryStreamEntity(TemporaryStream ts,String contentType) {
		return new TemporaryStreamHttpEntity(ts,contentType);
	}

	public static Entity getResourceEntity(Resource res, String contentType) {
		return new ResourceHttpEntity(res,contentType);
	}

	
}
