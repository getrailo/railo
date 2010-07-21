package railo.runtime.net.http;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;

// MUST synchronisieren mit HTTP Tag createmethod muss hier rein und dann clone ersetzten

/**
 * Utitlities class for HTTP Client
 */
public final class HttpClientUtil {
    
    /**
     * Maximal count of redirects (5)
     */
    public static final short MAX_REDIRECT=15;
    
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
    
    /**
     * Execute a HTTTP Client and follow redirect over different hosts
     * @param client
     * @param method
     * @param doRedirect
     * @return
     * @throws IOException
     * @throws HttpException
     */
    public static HttpMethod execute(HttpClient client, HttpMethod method, boolean doRedirect) throws HttpException, IOException {
    	short count=0;
        method.setFollowRedirects(false);
        
        while(isRedirect(client.executeMethod(method)) && doRedirect && count++ < MAX_REDIRECT) {
        	method=rewrite(method);
        }
        return method;
    }

    /**
     * rewrite request method
     * @param method
     * @return
     * @throws MalformedURLException
     */
    private static HttpMethod rewrite(HttpMethod method) throws MalformedURLException {
        Header location = method.getResponseHeader("location");
        if(location==null) return method;

        HostConfiguration config = method.getHostConfiguration();
        URL url;
        try {
            url = new URL(location.getValue());
        } 
        catch (MalformedURLException e) {
            
            url=new URL(config.getProtocol().getScheme(),
                    config.getHost(),
                    config.getPort(),
                    mergePath(method.getPath(),location.getValue()));
        }
        
        method= clone(method,url);
        
        return method;
    }

    /**
     * FUNKTIONIERT NICHT, HOST WIRD NICHT ﾏBERNOMMEN
     * Clones a http method and sets a new url
     * @param src
     * @param url
     * @return
     */
    public static HttpMethod clone(HttpMethod src, URL url) {
        HttpMethod trg = HttpMethodCloner.clone(src);
        HostConfiguration trgConfig = trg.getHostConfiguration();
        trgConfig.setHost(url.getHost(),url.getPort(),url.getProtocol());
        trg.setPath(url.getPath());
        trg.setQueryString(url.getQuery());
        
        return trg;
    }


    /**
     * checks if status code is a redirect
     * @param status
     * @return is redirect
     */
    private static boolean isRedirect(int status) {
        return 
        	status==STATUS_REDIRECT_FOUND || 
        	status==STATUS_REDIRECT_MOVED_PERMANENTLY ||
        	status==STATUS_REDIRECT_SEE_OTHER;
    }
    
    /**
     * merge to pathes to one
     * @param current
     * @param realPath
     * @return
     * @throws MalformedURLException
     */
    public static String mergePath(String current, String realPath) throws MalformedURLException {
        
        // get current directory
        String currDir;
        if(current==null || current.indexOf('/')==-1)currDir="/";
        else if(current.endsWith("/"))currDir=current;
        else currDir=current.substring(0,current.lastIndexOf('/')+1);
        
        // merge together
        String path;
        if(realPath.startsWith("./"))path=currDir+realPath.substring(2);
        else if(realPath.startsWith("/"))path=realPath;
        else if(!realPath.startsWith("../"))path=currDir+realPath;
        else {
            while(realPath.startsWith("../") || currDir.length()==0) {
                realPath=realPath.substring(3);
                currDir=currDir.substring(0,currDir.length()-1);
                int index = currDir.lastIndexOf('/');
                if(index==-1)throw new MalformedURLException("invalid realpath definition for URL");
                currDir=currDir.substring(0,index+1);
            }
            path=currDir+realPath;
        }
        
        return path;
    }   
}