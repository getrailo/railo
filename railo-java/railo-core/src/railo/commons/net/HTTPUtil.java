package railo.commons.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

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

import railo.aprint;
import railo.commons.io.IOUtil;
import railo.commons.lang.StringList;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.PageSource;
import railo.runtime.config.Config;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageException;
import railo.runtime.exp.PageServletException;
import railo.runtime.net.http.HTTPServletRequestWrap;
import railo.runtime.net.http.HttpClientUtil;
import railo.runtime.net.http.HttpServletResponseWrap;
import railo.runtime.net.proxy.ProxyData;
import railo.runtime.net.proxy.ProxyDataImpl;
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
	 public static URL toURL(String strUrl) throws MalformedURLException {
		 return toURL(strUrl,-1);
	 }
	 
	 public static URL toURL(String strUrl,URL defaultValue){
		 try {
			return toURL(strUrl,-1);
		} catch (MalformedURLException e) {
			return defaultValue;
		}
	 }
	 

	 public static String validateURL(String strUrl,String defaultValue){
		 try {
			return toURL(strUrl,-1).toExternalForm();
		} catch (MalformedURLException e) {
			return defaultValue;
		}
	 }
    
    /**
     * cast a string to a url
     * @param strUrl string represent a url
     * @return url from string
     * @throws MalformedURLException
     */

	  public static URL toURL(String strUrl, int port) throws MalformedURLException {
		  URL url;
		  try {
	            url=new URL(strUrl);
	        }
	        catch(MalformedURLException mue) {
	            url=new URL("http://"+strUrl);
	        }
		  
		  return toURL(url, port);
	  }
	 
	 
    private static URL toURL(URL url, int port) throws MalformedURLException {
    	
    	
        
        
        
        
        // file
        String path=url.getPath();
        //String file=url.getFile();
        String query = url.getQuery();
        String ref = url.getRef();
        String user=url.getUserInfo();
        if(port<=0) port=url.getPort();

        // decode path
        if(!StringUtil.isEmpty(path)) {
        	int sqIndex=path.indexOf(';');
        	String q=null;
        	if(sqIndex!=-1) {
        		q=path.substring(sqIndex+1);
        		path=path.substring(0,sqIndex);
        	} 
        	
        	StringBuilder res=new StringBuilder();
        	
        	StringList list = List.toListTrim(path, '/');
        	String str;
        	
        	while(list.hasNext()){
        		str=list.next();
        		//str=URLDecoder.decode(str);
        		
        		if(StringUtil.isEmpty(str)) continue;
        		res.append("/");
        		res.append(escapeQSValue(str));
        	}
        	if(StringUtil.endsWith(path, '/')) res.append('/');      		
        	path=res.toString();
        	
        	if(sqIndex!=-1) {
        		path+=decodeQuery(q,';');
        	}
        	
        }
        
        // decode query	
        query=decodeQuery(query,'?');
       
        
        
        String file=path+query;
        
     // decode ref/anchor	
        if(ref!=null) {
        	file+="#"+escapeQSValue(ref);
        }
        
        // user/pasword
        if(!StringUtil.isEmpty(user)) {
        	int index=user.indexOf(':');
        	if(index!=-1) {
        		user=escapeQSValue(user.substring(0,index))+":"+escapeQSValue(user.substring(index+1));
        	}
        	else user=escapeQSValue(user);
        	
        	String strUrl = getProtocol(url)+"://"+user+"@"+url.getHost();
        	if(port>0)strUrl+=":"+port;
        	strUrl+=file;
        	return new URL(strUrl);
        }
       
       
        
       // port
       if(port<=0) return new URL(url.getProtocol(),url.getHost(),file);
       return new URL(url.getProtocol(),url.getHost(),port,file);
       
       		       
    }
    
    private static String decodeQuery(String query,char startDelimeter) {
    	if(!StringUtil.isEmpty(query)) {
    		StringBuilder res=new StringBuilder();
        	
        	StringList list = List.toList(query, '&');
        	String str;
        	int index;
        	char del=startDelimeter;
        	while(list.hasNext()){
        		res.append(del);
        		del='&';
        		str=list.next();
        		index=str.indexOf('=');
        		if(index==-1)res.append(escapeQSValue(str));
        		else {
        			res.append(escapeQSValue(str.substring(0,index)));
        			res.append('=');
        			res.append(escapeQSValue(str.substring(index+1)));
        		}
        	}
        	query=res.toString();
        }
        else query="";
    	return query;
	}


	public static URI toURI(String strUrl) throws URISyntaxException {
		 return toURI(strUrl,-1);
	 }
    
    public static URI toURI(String strUrl, int port) throws URISyntaxException {
    	
    	//print.o((strUrl));
    	URI uri = new URI(strUrl);
    	
    	String host = uri.getHost();
    	String fragment = uri.getRawFragment();
    	String path = uri.getRawPath();
    	String query= uri.getRawQuery();
    	
    	String scheme = uri.getScheme();
    	String userInfo = uri.getRawUserInfo();
    	if(port<=0) port=uri.getPort();

    
        // decode path
        if(!StringUtil.isEmpty(path)) {
        	
        	int sqIndex=path.indexOf(';');
        	String q=null;
        	if(sqIndex!=-1) {
        		q=path.substring(sqIndex+1);
        		path=path.substring(0,sqIndex);
        	} 
        	
        	
        	StringBuilder res=new StringBuilder();
        	
        	StringList list = List.toListTrim(path, '/');
        	String str;
        	
        	while(list.hasNext()){
        		str=list.next();
        		//str=URLDecoder.decode(str);
        		
        		if(StringUtil.isEmpty(str)) continue;
        		res.append("/");
        		res.append(escapeQSValue(str));
        	}
        	if(StringUtil.endsWith(path, '/')) res.append('/');      		
        	path=res.toString();
        	
        	if(sqIndex!=-1) {
        		path+=decodeQuery(q,';');
        	}
        }
        
        // decode query	
        query=decodeQuery(query,'?');
    
        
        
     // decode ref/anchor	
        if(!StringUtil.isEmpty(fragment)) {
        	fragment=escapeQSValue(fragment);
        }
        
        // user/pasword
        if(!StringUtil.isEmpty(userInfo)) {
        	int index=userInfo.indexOf(':');
        	if(index!=-1) {
        		userInfo=escapeQSValue(userInfo.substring(0,index))+":"+escapeQSValue(userInfo.substring(index+1));
        	}
        	else userInfo=escapeQSValue(userInfo);
        }
        
        /*print.o("- fragment:"+fragment);
    	print.o("- host:"+host);
    	print.o("- path:"+path);
    	print.o("- query:"+query);
    	print.o("- scheme:"+scheme);
    	print.o("- userInfo:"+userInfo);
    	print.o("- port:"+port);
    	print.o("- absolute:"+uri.isAbsolute());
    	print.o("- opaque:"+uri.isOpaque());*/
       	
    	StringBuilder rtn=new StringBuilder();
    	if(scheme!=null) {
    		rtn.append(scheme);
    		rtn.append("://");
    	}
    	if(userInfo!=null) {
    		rtn.append(userInfo);
    		rtn.append("@");
    	}
    	if(host!=null) {
    		rtn.append(host);
    	}
    	if(port>0) {
    		rtn.append(":");
    		rtn.append(port);
    	}
    	if(path!=null) {
    		rtn.append(path);
    	}
    	if(query!=null) {
    		//rtn.append("?");
    		rtn.append(query);
    	}
    	if(fragment!=null) {
    		rtn.append("#");
    		rtn.append(fragment);
    	}
    	
    	return new URI(rtn.toString()); 
    }
    
    private static void test(String str) throws URISyntaxException {
    	//print.o(str);
    	int port=-1;
    	String res;
    	try {
			res=toURL(new URL(str),port).toString();
		} catch (MalformedURLException e) {
			res=toURI(str).toString();
		}
    	String res2 = encode(str);
		
    	if(res.equals(res2)){
    		aprint.o(res);
    	}
    	else {
    		aprint.e(str);
    		aprint.e("- uri:"+res);
    		aprint.e("- enc:"+res2);
    	}
    	
		
    	/*String uri = toURI(str).toString();
    	String url = toURL(str).toString();
    	
    	if(uri.equals(url)){
    		print.o(uri);
    	}
    	else {
    		print.e(str);
    		print.e("uri:"+uri);
    		print.e("url:"+url);
    	}*/
		
	}

	public static void main(String[] args) throws Exception {
		
		// valid urls
		test("http://localhost:8080/jm/test/tags/_http.cfm;jsessionid=48lhqe568il0d?CFID=2fa614d8-9deb-4051-92e9-100ed44fd2df&CFTOKEN=0&jsessionid=48lhqe568il0d");
		test("http://www.railo.ch");
		test("http://www.railo.ch/");
		test("http://www.railo.ch/a.cfm");
		test("http://www.railo.ch/a.cfm?");
		test("http://www.railo.ch/a.cfm?test=1");
		test("http://www.railo.ch/a.cfm?test=1&");
		test("http://www.railo.ch:80/a.cfm?test=1&");
		test("http://hans@www.railo.ch:80/a.cfm?test=1&");
		test("http://hans:peter@www.railo.ch:80/a.cfm?test=1&x");
		test("http://hans:peter@www.railo.ch:80/a.cfm?test=1&x#");
		test("http://hans:peter@www.railo.ch:80/a.cfm?test=1&x#xx");
		
		test("http://www.railo.ch");
		test("http://www.railo.ch/");
		test("http://www.railo.ch/Š.cfm");
		test("http://www.railo.ch/Š.cfm?");
		test("http://www.railo.ch/Š.cfm?testŠ=Š");
		test("http://www.railo.ch/Š.cfm?testŠ=Š&");
		test("http://www.railo.ch:80/Š.cfm?testŠ=Š&");
		test("http://hŠns@www.railo.ch:80/Š.cfm?testŠ=Š&");
		test("http://hŠns:pŸter@www.railo.ch:80/Š.cfm?testŠ=Š&x");
		test("http://hŠns:pŸter@www.railo.ch:80/Š.cfm?testŠ=Š&x#");
		test("http://hŠns:pŸter@www.railo.ch:80/Š.cfm?testŠ=Š&x#Ÿ");
		
		test("/");
		test("/a.cfm");
		test("/a.cfm?");
		test("/a.cfm?test=1");
		test("/a.cfm?test=1&");
		test("/a.cfm?test=1&");
		test("/a.cfm?test=1&");
		test("/a.cfm?test=1&x");
		test("/a.cfm?test=1&x#");
		test("/a.cfm?test=1&x#xx");
		

		test("/");
		test("/Š.cfm");
		test("/Š.cfm?");
		test("/Š.cfm?testš=1Ÿ");
		test("/Š.cfm?testš=1Ÿ&");
		test("/Š.cfm?testš=1Ÿ&");
		test("/Š.cfm?testš=1Ÿ&");
		test("/Š.cfm?testš=1Ÿ&x");
		test("/Š.cfm?testš=1Ÿ&x#");
		test("/Š.cfm?testš=1Ÿ&x#xxŠ");
		//test("http://hašns:gehešim@www.example.org:80/dšemo/example.cgi?lanšd=de&stadt=aa#geschiŠchte");
		//print.o(toURI("http://www.railo.ch/tešst.cfm?do=pšhoto.view&id=289#commentAdd"));
		//print.o(toURI("/test.cfm?do=photo.view&id=289#commentAdd"));
		//print.o(toURI("http://localhost/testingapp/index.cfm?do=photo.view&id=289#commentAdd"));
	}
    
    


	private static String getProtocol(URI uri) {
    	String p=uri.getRawSchemeSpecificPart();
    	if(p==null) return null;
		if(p.indexOf('/')==-1) return p;
		if(p.indexOf("https")!=-1) return "https";
		if(p.indexOf("http")!=-1) return "http";
		return p;
	}
    
    private static String getProtocol(URL url) {
		String p=url.getProtocol().toLowerCase();
		if(p.indexOf('/')==-1) return p;
		if(p.indexOf("https")!=-1) return "https";
		if(p.indexOf("http")!=-1) return "http";
		return p;
	}


	/*public static void main(String[] args) throws MalformedURLException {
    	print.o(toURL("http://www.railo.ch/index.cfm#susi"));
    	print.o(toURL("http://www.railo.ch/index.cfm#šŠŸ"));
    	print.o(toURL("http://hans:geheim@www.example.org:80/demo/example.cgi?land=de&stadt=aa#geschichte"));
    	print.o(toURL("http://hašns:gehešim@www.example.org/dšemo/example.cgi?lanšd=de&stadt=aa#geschiŠchte"));
		// 
	}*/

    

    
    private static String escapeQSValue(String str) {
    	if(!URLEncoder.needEncoding(str)) return str;
    	
    	Config config = ThreadLocalPageContext.getConfig();
    	if(config!=null){
    		try {
    			return URLEncoder.encode(str,config.getWebCharset());
    		} 
    		catch (UnsupportedEncodingException e) {}
    	}
    	return URLEncoder.encode(str);
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

	public static String optimizeRealPath(PageContext pc,String realPath) {
		int index;
		String requestURI=realPath,queryString=null;
		if((index=realPath.indexOf('?'))!=-1){
			requestURI=realPath.substring(0,index);
			queryString=realPath.substring(index+1);
		}
		PageSource ps = pc.getRelativePageSource(requestURI);
		requestURI=ps.getFullRealpath();
		if(queryString!=null) return requestURI+"?"+queryString;
		return requestURI;
	}

	public static void forward(PageContext pc,String realPath) throws ServletException, IOException {
		ServletContext context = pc.getServletContext();
		realPath=HTTPUtil.optimizeRealPath(pc,realPath);
		
		try{
			pc.getHttpServletRequest().setAttribute("railo.forward.request_uri", realPath);
			
        	RequestDispatcher disp = context.getRequestDispatcher(realPath);
        	if(disp==null)
    			throw new PageServletException(new ApplicationException("Page "+realPath+" not found"));
            
        	//populateRequestAttributes();
        	disp.forward(removeWrap(pc.getHttpServletRequest()),pc.getHttpServletResponse());
		}
        finally{
        	ThreadLocalPageContext.register(pc);
        }
	}
	
	/*public static void include(PageContext pc,String realPath) throws ServletException,IOException  {
		HttpServletRequest req = pc.getHttpServletRequest();
		HttpServletResponse rsp = pc.getHttpServletResponse();
		realPath=optimizeRealPath(pc,realPath);
		
		RequestDispatcher disp = pc.getHttpServletRequest().getRequestDispatcher(realPath);
        try{
        	((PageContextImpl)pc).getRootOut().getServletOutputStream();
        	print.out("include:"+realPath);
        	disp.include(req,rsp);
        }
        finally{
        	ThreadLocalPageContext.register(pc);
        }
	}*/
	

	
	public static ServletRequest removeWrap(ServletRequest req) {
		while(req instanceof HTTPServletRequestWrap)
			return ((HTTPServletRequestWrap)req).getOriginalRequest();
		return req;
	}
	

	public static void include(PageContext pc,String realPath) throws ServletException,IOException  {
		include(pc, pc.getHttpServletRequest(),pc.getHttpServletResponse(),realPath);
	}

	public static void include(PageContext pc,ServletRequest req, ServletResponse rsp, String realPath) throws ServletException,IOException  {
		realPath=optimizeRealPath(pc,realPath);
		boolean inline=HttpServletResponseWrap.get();
		//print.out(rsp+":"+pc.getResponse());
		RequestDispatcher disp = getRequestDispatcher(pc,realPath);
		
		if(inline)	{
			//RequestDispatcher disp = getRequestDispatcher(pc,realPath);
			disp.include(req,rsp);
			return;
		}
		
		try	{
			ByteArrayOutputStream baos=new ByteArrayOutputStream();
			HttpServletResponseWrap hsrw=new HttpServletResponseWrap(pc.getHttpServletResponse(),baos);
			HttpServletResponseWrap.set(true);
			
			//RequestDispatcher disp = getRequestDispatcher(pc,realPath);
			
        	disp.include(req,hsrw);
	        if(!hsrw.isCommitted())hsrw.flushBuffer();
	        pc.write(IOUtil.toString(baos.toByteArray(), hsrw.getCharacterEncoding()));
        }
        finally{
        	HttpServletResponseWrap.release();
        	ThreadLocalPageContext.register(pc);
        }
	}

	private static RequestDispatcher getRequestDispatcher(PageContext pc,String realPath) throws PageServletException {
		RequestDispatcher disp = pc.getServletContext().getRequestDispatcher(realPath);
    	if(disp==null) throw new PageServletException(new ApplicationException("Page "+realPath+" not found"));
    	return disp;
	}
	
	
	// MUST create a copy from toURL and rename toURI and rewrite for URI, pherhaps it is possible to merge them somehow
	public static String encode(String realpath) {
    	
        
		int qIndex=realpath.indexOf('?');
		
		if(qIndex==-1) return realpath;
		
		String file=realpath.substring(0,qIndex);
		String query=realpath.substring(qIndex+1);
		int sIndex=query.indexOf('#');
		
		String anker=null;
		if(sIndex!=-1){
			//print.o(sIndex);
			anker=query.substring(sIndex+1);
			query=query.substring(0,sIndex);
		}
		
		StringBuilder res=new StringBuilder(file);
    	
		
        // query
        if(!StringUtil.isEmpty(query)){
        	
        	StringList list = List.toList(query, '&');
        	String str;
        	int index;
        	char del='?';
        	while(list.hasNext()){
        		res.append(del);
        		del='&';
        		str=list.next();
        		index=str.indexOf('=');
        		if(index==-1)res.append(escapeQSValue(str));
        		else {
        			res.append(escapeQSValue(str.substring(0,index)));
        			res.append('=');
        			res.append(escapeQSValue(str.substring(index+1)));
        		}
        	}	
        }
       
        // anker
        if(anker!=null) {
        	res.append('#');
        	res.append(escapeQSValue(anker));
        }
       
        
       return res.toString();		       
    }


	public static int getPort(URL url) {
		if(url.getPort()!=-1) return url.getPort();
		if("https".equalsIgnoreCase(url.getProtocol()))
			return 443;
		return 80;
	}

	
	/**
	 * return the length of a file defined by a url.
	 * @param dataUrl
	 * @return
	 */
	public static long length(URL url) {
		long length=0;
		
		// check response header "content-length"
		ProxyData pd=ProxyDataImpl.NO_PROXY;
		try {
			HttpMethod http = HTTPUtil.head(url, null, null, -1,null, "Railo", pd.getServer(), pd.getPort(),pd.getUsername(), pd.getPassword(),null);
			Header cl = http.getResponseHeader("content-length");
			if(cl!=null)	{
				length=Caster.toIntValue(cl.getValue(),-1);
				if(length!=-1) return length;
			}
		} 
		catch (IOException e) {}
		
		// get it for size
		try {
			HttpMethod http = HTTPUtil.invoke(url, null, null, -1,null, "Railo", pd.getServer(), pd.getPort(),pd.getUsername(), pd.getPassword(),null);
			InputStream is = http.getResponseBodyAsStream();
			byte[] buffer = new byte[1024];
	        int len;
	        length=0;
	        while((len = is.read(buffer)) !=-1){
	          length+=len;
	        }
		} 
		catch (IOException e) {}
		return length;
	}
	
	
}