package railo.commons.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.EntityEnclosingMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;

import railo.commons.lang.StringList;
import railo.commons.lang.StringUtil;
import railo.runtime.exp.PageException;
import railo.runtime.net.http.HttpClientUtil;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.type.List;

/**
 * 
 */
public final class HTTPUtil {

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
     * @param headers
     * @return
     * @throws IOException
     */
    public static HttpMethod invoke(URL url, String username, String password, long timeout, 
            String charset, String useragent,
            String proxyserver, int proxyport, String proxyuser, 
            String proxypassword, Header[] headers) throws IOException {

        HttpClient client = new HttpClient();
        HttpMethod httpMethod=new GetMethod(url.toExternalForm());
        HostConfiguration config = client.getHostConfiguration();
        
        HttpState state = client.getState();
        
        setHeader(httpMethod,headers);
        setContentType(httpMethod,charset);
        setUserAgent(httpMethod,useragent);
        setTimeout(client,timeout);
        setCredentials(client,httpMethod,username,password);  
        setProxy(config,state,proxyserver,proxyport,proxyuser,proxypassword);
        
        /*if(followRedirects!=null){
        	client.executeMethod(httpMethod);
        }
        else */
        	httpMethod = HttpClientUtil.execute(client,httpMethod,true);
        
        return httpMethod;
    }
    
    /**
     * cast a string to a url
     * @param strUrl string represent a url
     * @return url from string
     * @throws MalformedURLException
     */
    public static URL toURL(String strUrl, int port) throws MalformedURLException {
    	return toURL(strUrl, port, false);
    }
    public static URL toURL(String strUrl, int port,boolean strict) throws MalformedURLException {
    	
        URL url;
        try {
            url=new URL(strUrl);
        }
        catch(MalformedURLException mue) {
            url=new URL("http://"+strUrl);
        }
        
        // file
        String file=url.getFile();
        String query = url.getQuery();
        if(!StringUtil.isEmpty(query)){
        	StringBuffer res=new StringBuffer(url.getPath());
        	
        	StringList list = List.toList(query, '&');
        	String str;
        	int index;
        	char del='?';
        	while(list.hasNext()){
        		res.append(del);
        		del='&';
        		str=list.next();
        		index=str.indexOf('=');
        		if(index==-1)res.append(escapeQSValue(str,strict));
        		else {
        			res.append(escapeQSValue(str.substring(0,index),strict));
        			res.append('=');
        			res.append(escapeQSValue(str.substring(index+1),strict));
        		}
        	}
        	file=res.toString();
        }
        
       // port
       if(port<=0) { 
    	   port=url.getPort();
    	   if(port<=0) {
    		   if(url.getProtocol().equalsIgnoreCase("https")) port=443;
    		   else port=80;
    	   }
       }
       
       return new URL(url.getProtocol(),url.getHost(),port,file);
       
       		       
    }
    
    private static Object escapeQSValue(String str, boolean strict) {
    	
    	if(str.indexOf('=')!=-1)str=StringUtil.replace(str, "=", "%3D", false);
    	if(strict && str.indexOf('/')!=-1)str=StringUtil.replace(str, "/", "%2F", false);
    	return str;
	}

	public static HttpMethod put(URL url, String username, String password, int timeout, 
            String charset, String useragent,
            String proxyserver, int proxyport, String proxyuser, 
            String proxypassword, Header[] headers, RequestEntity body) throws IOException {
		
		
		HttpClient client = new HttpClient();
		PutMethod httpMethod=new PutMethod(url.toExternalForm());
        HostConfiguration config = client.getHostConfiguration();
        
        HttpState state = client.getState();
        
        setHeader(httpMethod,headers);
        setContentType(httpMethod,charset);
        setUserAgent(httpMethod,useragent);
        setTimeout(client,timeout);
        setCredentials(client,httpMethod,username,password);    
        setProxy(config,state,proxyserver,proxyport,proxyuser,proxypassword);
        setBody(httpMethod,body);
        
        
        return HttpClientUtil.execute(client,httpMethod,true);
         
	}
    
    public static HttpMethod delete(URL url, String username, String password, int timeout, 
            String charset, String useragent,
            String proxyserver, int proxyport, String proxyuser, 
            String proxypassword, Header[] headers) throws IOException {
		
		
		HttpClient client = new HttpClient();
		DeleteMethod httpMethod=new DeleteMethod(url.toExternalForm());
        HostConfiguration config = client.getHostConfiguration();
        
        HttpState state = client.getState();
        
        setHeader(httpMethod,headers);
        setContentType(httpMethod,charset);
        setUserAgent(httpMethod,useragent);
        setTimeout(client,timeout);
        setCredentials(client,httpMethod,username,password);    
        setProxy(config,state,proxyserver,proxyport,proxyuser,proxypassword);
        
        
        return HttpClientUtil.execute(client,httpMethod,true);
         
	}

    public static HttpMethod head(URL url, String username, String password, int timeout, 
            String charset, String useragent,
            String proxyserver, int proxyport, String proxyuser, 
            String proxypassword, Header[] headers) throws IOException {
		
		
		HttpClient client = new HttpClient();
		HeadMethod httpMethod=new HeadMethod(url.toExternalForm());
        HostConfiguration config = client.getHostConfiguration();
        
        HttpState state = client.getState();
        
        setHeader(httpMethod,headers);
        setContentType(httpMethod,charset);
        setUserAgent(httpMethod,useragent);
        setTimeout(client,timeout);
        setCredentials(client,httpMethod,username,password);    
        setProxy(config,state,proxyserver,proxyport,proxyuser,proxypassword);
        
        
        return HttpClientUtil.execute(client,httpMethod,true);
         
	}

    

    private static void setBody(EntityEnclosingMethod httpMethod, RequestEntity body) {
        // body
        if(body!=null)httpMethod.setRequestEntity(body);
	}

	private static void setProxy(HostConfiguration config, HttpState state, String proxyserver,int proxyport, String proxyuser, String proxypassword) {

        // set Proxy
            if(!StringUtil.isEmpty(proxyserver)) {
                config.setProxy(proxyserver,proxyport);
                if(!StringUtil.isEmpty(proxyuser)) {
                    if(proxypassword==null)proxypassword="";
                    state.setProxyCredentials(null,null,new UsernamePasswordCredentials(proxyuser,proxypassword));
                }
            } 
	}

	private static void setCredentials(HttpClient client, HttpMethod httpMethod, String username,String password) {
        // set Username and Password
            if(username!=null) {
                if(password==null)password="";
                client.getState().setCredentials(null,null,new UsernamePasswordCredentials(username, password));
                httpMethod.setDoAuthentication( true );
            }
	}

	private static void setTimeout(HttpClient client, long timeout) {
        if(timeout>0){
        	
        	client.setConnectionTimeout((int)timeout);
        	client.setTimeout((int)timeout);
        }
	}

	private static void setUserAgent(HttpMethod httpMethod, String useragent) {
        if(useragent!=null)httpMethod.setRequestHeader("User-Agent",useragent);
	}

	private static void setContentType(HttpMethod httpMethod, String charset) {
    	if(charset!=null)httpMethod.addRequestHeader("Content-type", "text/html; charset="+charset );
	}

	private static void setHeader(HttpMethod httpMethod,Header[] headers) {
    	if(headers!=null) {
        	for(int i=0;i<headers.length;i++)
        		httpMethod.addRequestHeader(headers[i].getName(), headers[i].getValue());
        }
	}

	public static RequestEntity toRequestEntity(Object value) throws PageException {
    	if(value instanceof RequestEntity) return (RequestEntity) value;
    	else if(value instanceof InputStream) {
			return new InputStreamRequestEntity((InputStream)value,"application/octet-stream");
		}
		else if(Decision.isCastableToBinary(value,false)){
			return new ByteArrayRequestEntity(Caster.toBinary(value));
		}
		else {
			return new StringRequestEntity(Caster.toString(value));
		}
    }
    
	
	public static URL removeRef(URL url) throws MalformedURLException{
		int port=url.getPort();
		if(port==80 && url.getProtocol().equalsIgnoreCase("http"))
			port=-1;
		else if(port==443 && url.getProtocol().equalsIgnoreCase("https"))
			port=-1;
		
		
		
		URL u=new URL(url.getProtocol(),url.getHost(),port,url.getFile());
		return u;
	}
	
	public static String removeRef(String url) throws MalformedURLException{
		return removeRef(new URL(url)).toExternalForm();
	}
	
    /**
     * cast a string to a url
     * @param strUrl string represent a url
     * @return url from string
     * @throws MalformedURLException
     */
	 public static URL toURL(String strUrl) throws MalformedURLException {
		 return toURL(strUrl,false);
	 }
			
    public static URL toURL(String strUrl,boolean strict) throws MalformedURLException {
		return toURL(strUrl,-1,strict);
    }

	public static URL toURL(HttpMethod httpMethod) {
		HostConfiguration config = httpMethod.getHostConfiguration();
		
		try {
			String qs = httpMethod.getQueryString();
			if(StringUtil.isEmpty(qs))
				return new URL(config.getProtocol().getScheme(),config.getHost(),config.getPort(),httpMethod.getPath());
			return new URL(config.getProtocol().getScheme(),config.getHost(),config.getPort(),httpMethod.getPath()+"?"+qs);
		} catch (MalformedURLException e) {
			return null;
		}
	}

	
	
	
}