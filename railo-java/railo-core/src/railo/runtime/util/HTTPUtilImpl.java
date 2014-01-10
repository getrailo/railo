package railo.runtime.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import railo.commons.net.URLDecoder;
import railo.commons.net.URLEncoder;
import railo.commons.net.http.HTTPEngine;
import railo.commons.net.http.HTTPResponse;
import railo.commons.net.http.Header;
import railo.runtime.net.proxy.ProxyDataImpl;

public class HTTPUtilImpl implements HTTPUtil {
	


	private static HTTPUtil instance=new HTTPUtilImpl();

	private HTTPUtilImpl(){}
	
	public static HTTPUtil getInstance() {
		return instance;
	}

	/**
	 * @see railo.runtime.util.HTTPUtil#decode(java.lang.String, java.lang.String)
	 */
	public String decode(String str, String charset)throws UnsupportedEncodingException {
		return URLDecoder.decode(str, charset,false);
	}

	/**
	 * @see railo.runtime.util.HTTPUtil#delete(java.net.URL, java.lang.String, java.lang.String, int, java.lang.String, java.lang.String, java.lang.String, int, java.lang.String, java.lang.String, railo.commons.net.http.Header[])
	 */
	public HTTPResponse delete(URL url, String username, String password,
			int timeout, String charset, String useragent, String proxyserver,
			int proxyport, String proxyuser, String proxypassword,
			Header[] headers) throws IOException {
		return HTTPEngine.delete(url, username, password, timeout,HTTPEngine.MAX_REDIRECT, charset, useragent, ProxyDataImpl.getInstance(proxyserver, proxyport, proxyuser, proxypassword), headers);
	}

	/**
	 * @param str
	 * @param charset
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public String encode(String str, String charset)throws UnsupportedEncodingException {
		return URLEncoder.encode(str, charset);
	}

	/**
	 * @see railo.runtime.util.HTTPUtil#head(java.net.URL, java.lang.String, java.lang.String, int, java.lang.String, java.lang.String, java.lang.String, int, java.lang.String, java.lang.String, railo.commons.net.http.Header[])
	 */
	public HTTPResponse head(URL url, String username, String password,
			int timeout, String charset, String useragent, String proxyserver,
			int proxyport, String proxyuser, String proxypassword,
			Header[] headers) throws IOException {
		return HTTPEngine.head(url, username, password, timeout,HTTPEngine.MAX_REDIRECT, charset, useragent, ProxyDataImpl.getInstance(proxyserver, proxyport, proxyuser, proxypassword), headers);
	}

	/**
	 * @see railo.runtime.util.HTTPUtil#get(java.net.URL, java.lang.String, java.lang.String, int, java.lang.String, java.lang.String, java.lang.String, int, java.lang.String, java.lang.String, railo.commons.net.http.Header[])
	 */
	public HTTPResponse get(URL url, String username, String password,
			int timeout, String charset, String useragent, String proxyserver,
			int proxyport, String proxyuser, String proxypassword,
			Header[] headers) throws IOException {
		return HTTPEngine.get(url, username, password, timeout,HTTPEngine.MAX_REDIRECT, charset, useragent, ProxyDataImpl.getInstance(proxyserver, proxyport, proxyuser, proxypassword), headers);
	}

	/**
	 * @see railo.runtime.util.HTTPUtil#put(java.net.URL, java.lang.String, java.lang.String, int, java.lang.String, java.lang.String, java.lang.String, int, java.lang.String, java.lang.String, railo.commons.net.http.Header[], java.lang.Object)
	 */
	public HTTPResponse put(URL url, String username, String password,
			int timeout, String charset, String useragent, String proxyserver,
			int proxyport, String proxyuser, String proxypassword,
			Header[] headers, Object body) throws IOException {
		return put(url, username, proxypassword, timeout, null, charset, useragent, proxyserver, proxyport, proxyuser, proxypassword, headers, body);
	}
	
	// FUTURE add to interface
	public HTTPResponse put(URL url, String username, String password,
			int timeout, String mimetype, String charset, String useragent, String proxyserver,
			int proxyport, String proxyuser, String proxypassword,
			Header[] headers, Object body) throws IOException {
		return HTTPEngine.put(url, username, password, timeout,HTTPEngine.MAX_REDIRECT, mimetype, charset, useragent, ProxyDataImpl.getInstance(proxyserver, proxyport, proxyuser, proxypassword), headers, body);
	}

	@Override
	public URL toURL(String strUrl, int port) throws MalformedURLException {
		return toURL(strUrl, port, true);
	}
	
	public URL toURL(String strUrl, int port, boolean encodeIfNecessary) throws MalformedURLException {
		return railo.commons.net.HTTPUtil.toURL(strUrl, port,encodeIfNecessary);
	}

	
	
	/**
	 * @see railo.commons.net.HTTPUtil#toURL(java.lang.String)
	 */
	public URL toURL(String strUrl) throws MalformedURLException {
		return railo.commons.net.HTTPUtil.toURL(strUrl,true);
	}
	
	public URI toURI(String strUrl) throws URISyntaxException {
		return railo.commons.net.HTTPUtil.toURI(strUrl);
	}
	
	public URI toURI(String strUrl, int port) throws URISyntaxException {
		return railo.commons.net.HTTPUtil.toURI(strUrl,port);
	}


}
