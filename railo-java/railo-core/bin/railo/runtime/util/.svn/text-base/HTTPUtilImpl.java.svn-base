package railo.runtime.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.RequestEntity;

import railo.commons.net.URLDecoder;
import railo.commons.net.URLEncoder;
import railo.runtime.exp.PageException;

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
		return URLDecoder.decode(str, charset);
	}

	/**
	 * @see railo.commons.net.HTTPUtil#delete(java.net.URL, java.lang.String, java.lang.String, int, java.lang.String, java.lang.String, java.lang.String, int, java.lang.String, java.lang.String, org.apache.commons.httpclient.Header[])
	 */
	public HttpMethod delete(URL url, String username, String password,
			int timeout, String charset, String useragent, String proxyserver,
			int proxyport, String proxyuser, String proxypassword,
			Header[] headers) throws IOException {
		return railo.commons.net.HTTPUtil.delete(url, username, password, timeout, charset, useragent, proxyserver, proxyport, proxyuser, proxypassword, headers);
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
	 * @see railo.commons.net.HTTPUtil#head(java.net.URL, java.lang.String, java.lang.String, int, java.lang.String, java.lang.String, java.lang.String, int, java.lang.String, java.lang.String, org.apache.commons.httpclient.Header[])
	 */
	public HttpMethod head(URL url, String username, String password,
			int timeout, String charset, String useragent, String proxyserver,
			int proxyport, String proxyuser, String proxypassword,
			Header[] headers) throws IOException {
		return railo.commons.net.HTTPUtil.head(url, username, password, timeout, charset, useragent, proxyserver, proxyport, proxyuser, proxypassword, headers);
	}

	/**
	 * @see railo.commons.net.HTTPUtil#invoke(java.net.URL, java.lang.String, java.lang.String, int, java.lang.String, java.lang.String, java.lang.String, int, java.lang.String, java.lang.String, org.apache.commons.httpclient.Header[])
	 */
	public HttpMethod get(URL url, String username, String password,
			int timeout, String charset, String useragent, String proxyserver,
			int proxyport, String proxyuser, String proxypassword,
			Header[] headers) throws IOException {
		return railo.commons.net.HTTPUtil.invoke(url, username, password, timeout, charset, useragent, proxyserver, proxyport, proxyuser, proxypassword, headers);
	}

	/**
	 * @see railo.commons.net.HTTPUtil#put(java.net.URL, java.lang.String, java.lang.String, int, java.lang.String, java.lang.String, java.lang.String, int, java.lang.String, java.lang.String, org.apache.commons.httpclient.Header[], org.apache.commons.httpclient.methods.RequestEntity)
	 */
	public HttpMethod put(URL url, String username, String password,
			int timeout, String charset, String useragent, String proxyserver,
			int proxyport, String proxyuser, String proxypassword,
			Header[] headers, RequestEntity body) throws IOException {
		return railo.commons.net.HTTPUtil.put(url, username, password, timeout, charset, useragent, proxyserver, proxyport, proxyuser, proxypassword, headers, body);
	}

	/**
	 * @see railo.commons.net.HTTPUtil#toRequestEntity(java.lang.Object)
	 */
	public RequestEntity toRequestEntity(Object value) throws PageException {
		return railo.commons.net.HTTPUtil.toRequestEntity(value);
	}

	/**
	 * @see railo.commons.net.HTTPUtil#toURL(java.lang.String, int)
	 */
	public URL toURL(String strUrl, int port) throws MalformedURLException {
		return railo.commons.net.HTTPUtil.toURL(strUrl, port);
	}

	/**
	 * @see railo.commons.net.HTTPUtil#toURL(java.lang.String)
	 */
	public URL toURL(String strUrl) throws MalformedURLException {
		return railo.commons.net.HTTPUtil.toURL(strUrl);
	}

	/**
	 * @see railo.commons.net.HTTPUtil#toURL(org.apache.commons.httpclient.HttpMethod)
	 */
	public Object toURL(HttpMethod httpMethod) {
		return railo.commons.net.HTTPUtil.toURL(httpMethod);
	}

}
