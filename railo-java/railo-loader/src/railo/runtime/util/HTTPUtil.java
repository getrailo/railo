package railo.runtime.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.RequestEntity;

import railo.runtime.exp.PageException;

public interface HTTPUtil {

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
     
    /**
     * make a http requst to given url 
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
     * @return resulting inputstream
     * @throws IOException
     */
    public HttpMethod get(URL url, String username, String password, int timeout, 
            String charset, String useragent,
            String proxyserver, int proxyport, String proxyuser, 
            String proxypassword, Header[] headers) throws IOException;
    
    
    public HttpMethod put(URL url, String username, String password, int timeout, 
            String charset, String useragent,
            String proxyserver, int proxyport, String proxyuser, 
            String proxypassword, Header[] headers, RequestEntity body) throws IOException ;
    
    public HttpMethod delete(URL url, String username, String password, int timeout, 
            String charset, String useragent,
            String proxyserver, int proxyport, String proxyuser, 
            String proxypassword, Header[] headers) throws IOException ;

    public HttpMethod head(URL url, String username, String password, int timeout, 
            String charset, String useragent,
            String proxyserver, int proxyport, String proxyuser, 
            String proxypassword, Header[] headers) throws IOException ;

    
	public RequestEntity toRequestEntity(Object value) throws PageException;
    
    /**
     * cast a string to a url
     * @param strUrl string represent a url
     * @return url from string
     * @throws MalformedURLException
     */
    public URL toURL(String strUrl, int port) throws MalformedURLException;

    
    /**
     * cast a string to a url
     * @param strUrl string represent a url
     * @return url from string
     * @throws MalformedURLException
     */
    public URL toURL(String strUrl) throws MalformedURLException;

	public Object toURL(HttpMethod httpMethod);
	
	/**
	 * translate a string in the URLEncoded Format
	 * @param str String to translate
	 * @param charset charset used for translation
	 * @return encoded String
	 * @throws UnsupportedEncodingException
	 */
	public String encode(String str, String charset) throws UnsupportedEncodingException;
	
	/**
	 * translate a url encoded string to a regular string
	 * @param str encoded string
	 * @param charset charset used
	 * @return raw string
	 * @throws UnsupportedEncodingException
	 */
	public String decode(String str, String charset) throws UnsupportedEncodingException;
}
