package railo.runtime.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import railo.commons.net.http.HTTPResponse;
import railo.commons.net.http.Header;

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
    public HTTPResponse get(URL url, String username, String password, int timeout, 
            String charset, String useragent,
            String proxyserver, int proxyport, String proxyuser, 
            String proxypassword, Header[] headers) throws IOException;
    
    
    public HTTPResponse put(URL url, String username, String password, int timeout, 
            String charset, String useragent,
            String proxyserver, int proxyport, String proxyuser, 
            String proxypassword, Header[] headers, Object body) throws IOException ;
    
    public HTTPResponse delete(URL url, String username, String password, int timeout, 
            String charset, String useragent,
            String proxyserver, int proxyport, String proxyuser, 
            String proxypassword, Header[] headers) throws IOException ;

    public HTTPResponse head(URL url, String username, String password, int timeout, 
            String charset, String useragent,
            String proxyserver, int proxyport, String proxyuser, 
            String proxypassword, Header[] headers) throws IOException ;

    
	//public RequestEntity toRequestEntity(Object value) throws PageException;
    
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

	public URI toURI(String strUrl) throws URISyntaxException;
	
	public URI toURI(String strUrl, int port) throws URISyntaxException;
	
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
