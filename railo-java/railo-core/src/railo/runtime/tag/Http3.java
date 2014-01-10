package railo.runtime.tag;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.EntityEnclosingMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.OptionsMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.methods.TraceMethod;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.util.EncodingUtil;

import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.StringUtil;
import railo.commons.net.HTTPUtil;
import railo.commons.net.URLEncoder;
import railo.commons.net.http.HTTPEngine;
import railo.commons.net.http.httpclient3.RailoStringPart;
import railo.commons.net.http.httpclient3.ResourcePart;
import railo.commons.net.http.httpclient3.ResourcePartSource;
import railo.runtime.config.Config;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.HTTPException;
import railo.runtime.exp.NativeException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.tag.BodyTagImpl;
import railo.runtime.net.http.ReqRspUtil;
import railo.runtime.net.proxy.ProxyData;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.text.csv.CSVParser;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Query;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.util.ArrayUtil;
import railo.runtime.type.util.KeyConstants;
import railo.runtime.type.util.ListUtil;
import railo.runtime.util.URLResolver;

// MUST change behavor of mltiple headers now is a array, it das so?

/**
* Lets you execute HTTP POST and GET operations on files. Using cfhttp, you can execute standard 
*   GET operations and create a query object from a text file. POST operations lets you upload MIME file 
*   types to a server, or post cookie, formfield, URL, file, or CGI variables directly to a specified server.
*
*
*
* 
**/
public final class Http3 extends BodyTagImpl implements Http {
	

    /**
     * maximum redirect count (5)
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
    

    public static final int STATUS_REDIRECT_TEMPORARY_REDIRECT = 307;


    	
    
    

	private static final short METHOD_GET=0;
	private static final short METHOD_POST=1;
	private static final short METHOD_HEAD=2;
	private static final short METHOD_PUT=3;
	private static final short METHOD_DELETE=4;
	private static final short METHOD_OPTIONS=5;
	private static final short METHOD_TRACE=6;
	
	private static final String NO_MIMETYPE="Unable to determine MIME type of file.";
	
	private static final int STATUS_OK=200;
	
	private static final short GET_AS_BINARY_NO=0;
	private static final short GET_AS_BINARY_YES=1;
	private static final short GET_AS_BINARY_AUTO=2;

	private static final Key ERROR_DETAIL = KeyImpl.intern("errordetail");
	private static final Key STATUSCODE = KeyImpl.intern("statuscode");
	private static final Key STATUS_CODE = KeyImpl.intern("status_code");
	private static final Key STATUS_TEXT = KeyImpl.intern("status_text");
	private static final Key HTTP_VERSION = KeyImpl.intern("http_version");
	

	private static final Key MIME_TYPE = KeyImpl.intern("mimetype");
	private static final Key CHARSET = KeyImpl.intern("charset");
	private static final Key FILE_CONTENT = KeyImpl.intern("filecontent");
	private static final Key HEADER = KeyImpl.intern("header");
	private static final Key TEXT = KeyImpl.intern("text");
	private static final Key EXPLANATION = KeyImpl.intern("explanation");
	private static final Key RESPONSEHEADER = KeyImpl.intern("responseheader");
	private static final Key SET_COOKIE = KeyImpl.intern("set-cookie");

	
	
	
	static {
	    //Protocol myhttps = new Protocol("https", new EasySSLProtocolSocketFactory(), 443);
	    //Protocol.registerProtocol("https", new Protocol("https", new EasySSLProtocolSocketFactory(), 443));
	}
	

    private ArrayList<HttpParamBean> params=new ArrayList<HttpParamBean>();
	
	
	/** When required by a server, a valid password. */
	private String password;

	/** Required for creating a query. Options are a tab or comma. Default is a comma. */
	private char delimiter=',';

	/** Yes or No. Default is No. For GET and POST operations, if Yes, page reference returned into the 
	** 	fileContent internal variable has its internal URLs fully resolved, including port number, so that 
	** 	links remain intact. */
	private boolean resolveurl;

	/** A value, in seconds. When a URL timeout is specified in the browser */
	private long timeout=-1;

	/** Host name or IP address of a proxy server. */
	private String proxyserver;

	/** The filename to be used for the file that is accessed. For GET operations, defaults to the name 
	** 	pecified in url. Enter path information in the path attribute. */
	private String strFile;

	/** The path to the directory in which a file is to be stored. If a path is not specified in a POST 
	** 	or GET operation, a variable is created (cfhttp.fileContent) that you can use to display the results 
	** 	of the POST operation in a cfoutput. */
	private String strPath;

	/** Boolean indicating whether to throw an exception that can be caught by using the cftry and 
	** 	cfcatch tags. The default is NO. */
	private boolean throwonerror;

	/** set the charset for the call. */
	private String charset=null;

	/** The port number on the proxy server from which the object is requested. Default is 80. When 
	** 	used with resolveURL, the URLs of retrieved documents that specify a port number are automatically 
	** 	resolved to preserve links in the retrieved document. */
	private int proxyport=80;

	/** Specifies the column names for a query when creating a query as a result of a cfhttp GET. */
	private String[] columns;

	/** The port number on the server from which the object is requested. Default is 80. When used with 
	** 	resolveURL, the URLs of retrieved documents that specify a port number are automatically resolved to 
	** 	preserve links in the retrieved document. If a port number is specified in the url attribute, the port
	** 	value overrides the value of the port attribute. */
	private int port=-1;

	/** User agent request header. */
	private String useragent="Railo (CFML Engine)";

	/** Required for creating a query. Indicates the start and finish of a column. Should be 
	** 	appropriately escaped when embedded in a column. For example, if the qualifier is a double quotation 
	** 	mark, it should be escaped as """". If there is no text qualifier in the file, specify it as " ". 
	** 	Default is the double quotation mark ("). */
	private char textqualifier='"';

	/** When required by a server, a valid username. */
	private String username;

	/** Full URL of the host name or IP address of the server on which the file resides. The URL must be
	** 	an absolute URL, including the protocol (http or https) and hostname. It may optionally contain a port
	** 	number. Port numbers specified in the url attribute override the port attribute. */
	private String url;

	/** Boolean indicating whether to redirect execution or stop execution.*/
	private boolean redirect=true;


	/** The name to assign to a query if the a query is constructed from a file. */
	private String name;

	/** GET or POST. Use GET to download a text or binary file or to create a query from the contents 
	** 	of a text file. Use POST to send information to a server page or a CGI program for processing. POST 
	** 	requires the use of a cfhttpparam tag. */
	private short method=METHOD_GET;

	//private boolean hasBody=false;
	
	private boolean firstrowasheaders=true;

	private String proxyuser=null;
	private String proxypassword="";
	private boolean multiPart=false;
	private String multiPartType=MultipartRequestEntityFlex.MULTIPART_FORM_DATA;
	
	private short getAsBinary=GET_AS_BINARY_NO;
    private String result="cfhttp";
    
    private boolean addtoken=false;

	
	@Override
	public void release()	{
		super.release();
	    params.clear();
		password=null;
		delimiter=',';
		resolveurl=false;
		timeout=-1L;
		proxyserver=null;
		proxyport=80;
		proxyuser=null;
		proxypassword="";
		strFile=null;
		throwonerror=false;
		charset=null;
		columns=null;
		port=-1;
		useragent="Railo (CFML Engine)";
		textqualifier='"';
		username=null;
		url=null;
		redirect=true;
		strPath=null;
		name=null;
		method=METHOD_GET;
		//hasBody=false;
		firstrowasheaders=true;
		
		getAsBinary=GET_AS_BINARY_NO;
		multiPart=false;
		multiPartType=MultipartRequestEntityFlex.MULTIPART_FORM_DATA;
        result="cfhttp";
        addtoken=false;
	}
	
	/**
	 * @param firstrowasheaders
	 */
	public void setFirstrowasheaders(boolean firstrowasheaders)	{
		this.firstrowasheaders=firstrowasheaders;
	}

	/** set the value password
	*  When required by a server, a valid password.
	* @param password value to set
	**/
	public void setPassword(String password)	{
		this.password=password;
	}
	/** set the value password
	*  When required by a proxy server, a valid password.
	* @param proxypassword value to set
	**/
	public void setProxypassword(String proxypassword)	{
		this.proxypassword=proxypassword;
	}

	/** set the value delimiter
	*  Required for creating a query. Options are a tab or comma. Default is a comma.
	* @param delimiter value to set
	**/
	public void setDelimiter(String delimiter)	{
		this.delimiter=delimiter.length()==0?',':delimiter.charAt(0);
	}

	/** set the value resolveurl
	*  Yes or No. Default is No. For GET and POST operations, if Yes, page reference returned into the 
	* 	fileContent internal variable has its internal URLs fully resolved, including port number, so that 
	* 	links remain intact.
	* @param resolveurl value to set
	**/
	public void setResolveurl(boolean resolveurl)	{
		this.resolveurl=resolveurl;
	}

	/** set the value timeout
	* @param timeout value to set
	 * @throws ExpressionException 
	**/
	public void setTimeout(double timeout) throws ExpressionException	{
		if(timeout<0)
			throw new ExpressionException("invalid value ["+Caster.toString(timeout)+"] for attribute timeout, value must be a positive integer greater or equal than 0");
		
	    long requestTimeout = pageContext.getRequestTimeout();
	    long _timeout=(long)(timeout*1000D);
	    this.timeout=requestTimeout<_timeout?requestTimeout:_timeout;
		//print.out("this.timeout:"+this.timeout);
	}

	/** set the value proxyserver
	*  Host name or IP address of a proxy server.
	* @param proxyserver value to set
	**/
	public void setProxyserver(String proxyserver)	{
		this.proxyserver=proxyserver;
	}
	
	/** set the value proxyport
	*  The port number on the proxy server from which the object is requested. Default is 80. When 
	* 	used with resolveURL, the URLs of retrieved documents that specify a port number are automatically 
	* 	resolved to preserve links in the retrieved document.
	* @param proxyport value to set
	**/
	public void setProxyport(double proxyport)	{
		this.proxyport=(int)proxyport;
	}

	/** set the value file
	*  The filename to be used for the file that is accessed. For GET operations, defaults to the name 
	* 	pecified in url. Enter path information in the path attribute.
	* @param file value to set
	**/
	public void setFile(String file)	{
		this.strFile=file;
	}

	/** set the value throwonerror
	*  Boolean indicating whether to throw an exception that can be caught by using the cftry and 
	* 	cfcatch tags. The default is NO.
	* @param throwonerror value to set
	**/
	public void setThrowonerror(boolean throwonerror)	{
		this.throwonerror=throwonerror;
	}

	/** set the value charset
	*  set the charset for the call.
	* @param charset value to set
	**/
	public void setCharset(String charset)	{
		this.charset=charset;
	}

	/** set the value columns
	* @param columns value to set
	 * @throws PageException
	**/
	public void setColumns(String columns) throws PageException	{
		this.columns=ListUtil.toStringArray(ListUtil.listToArrayRemoveEmpty(columns,","));
	}

	/** set the value port
	*  The port number on the server from which the object is requested. Default is 80. When used with 
	* 	resolveURL, the URLs of retrieved documents that specify a port number are automatically resolved to 
	* 	preserve links in the retrieved document. If a port number is specified in the url attribute, the port
	* 	value overrides the value of the port attribute.
	* @param port value to set
	**/
	public void setPort(double port)	{
		this.port=(int) port;
	}

	/** set the value useragent
	*  User agent request header.
	* @param useragent value to set
	**/
	public void setUseragent(String useragent)	{
		this.useragent=useragent;
	}

	/** set the value textqualifier
	*  Required for creating a query. Indicates the start and finish of a column. Should be 
	* 	appropriately escaped when embedded in a column. For example, if the qualifier is a double quotation 
	* 	mark, it should be escaped as """". If there is no text qualifier in the file, specify it as " ". 
	* 	Default is the double quotation mark (").
	* @param textqualifier value to set
	**/
	public void setTextqualifier(String textqualifier)	{
		this.textqualifier=textqualifier.length()==0?'"':textqualifier.charAt(0);
	}

	/** set the value username
	*  When required by a proxy server, a valid username.
	* @param proxyuser value to set
	**/
	public void setProxyuser(String proxyuser)	{
		this.proxyuser=proxyuser;
	}

	/** set the value username
	*  When required by a server, a valid username.
	* @param username value to set
	**/
	public void setUsername(String username)	{
		this.username=username;
	}

	/** set the value url
	*  Full URL of the host name or IP address of the server on which the file resides. The URL must be
	* 	an absolute URL, including the protocol (http or https) and hostname. It may optionally contain a port
	* 	number. Port numbers specified in the url attribute override the port attribute.
	* @param url value to set
	**/
	public void setUrl(String url)	{
		this.url=url;
	}

	/** set the value redirect
	* @param redirect value to set
	**/
	public void setRedirect(boolean redirect)	{
		this.redirect=redirect;
	}

	/** set the value path
	*  The path to the directory in which a file is to be stored. If a path is not specified in a POST 
	* 	or GET operation, a variable is created (cfhttp.fileContent) that you can use to display the results 
	* 	of the POST operation in a cfoutput.
	* @param path value to set
	**/
	public void setPath(String path)	{
		this.strPath=path;
	}

	/** set the value name
	*  The name to assign to a query if the a query is constructed from a file.
	* @param name value to set
	**/
	public void setName(String name)	{
		this.name=name;
	}

	/** set the value method
	*  GET or POST. Use GET to download a text or binary file or to create a query from the contents 
	* 	of a text file. Use POST to send information to a server page or a CGI program for processing. POST 
	* 	requires the use of a cfhttpparam tag.
	* @param method value to set
	 * @throws ApplicationException
	**/
	public void setMethod(String method) throws ApplicationException	{
	    method=method.toLowerCase().trim();
	    if(method.equals("post")) this.method=METHOD_POST;
	    else if(method.equals("get")) this.method=METHOD_GET;
	    else if(method.equals("head")) this.method=METHOD_HEAD;
	    else if(method.equals("delete")) this.method=METHOD_DELETE;
	    else if(method.equals("put")) this.method=METHOD_PUT;
	    else if(method.equals("trace")) this.method=METHOD_TRACE;
	    else if(method.equals("options")) this.method=METHOD_OPTIONS;
	    else throw new ApplicationException("invalid method type ["+(method.toUpperCase())+"], valid types are POST,GET,HEAD,DELETE,PUT,TRACE,OPTIONS");
	}


	@Override
	public int doStartTag()	{
		if(addtoken) {
			setParam("cookie","cfid",pageContext.getCFID());
			setParam("cookie","cftoken",pageContext.getCFToken());
			String jsessionid = pageContext.getJSessionId();
			if(jsessionid!=null)setParam("cookie","jsessionid",jsessionid);
		}
		
		return EVAL_BODY_INCLUDE;
	}

	private void setParam(String type, String name, String value) {
		HttpParamBean hpb = new HttpParamBean();
		hpb.setType(type);
		hpb.setName(name);
		hpb.setValue(value);
		setParam(hpb);
	}

	@Override
	public int doEndTag() throws PageException {
	    Struct cfhttp=new StructImpl();
		cfhttp.setEL(ERROR_DETAIL,"");
		pageContext.setVariable(result,cfhttp);

		// because commons 
		PrintStream out = System.out;
        try {
        	//System.setOut(new PrintStream(DevNullOutputStream.DEV_NULL_OUTPUT_STREAM));
             _doEndTag(cfhttp);
             return EVAL_PAGE;
        } 
        catch (IOException e) {
            throw Caster.toPageException(e);
        }
        finally {
        	System.setOut(out);
        }

	}

	
	
	private void _doEndTag(Struct cfhttp) throws PageException, IOException	{
		HttpConnectionManager manager=new SimpleHttpConnectionManager();//MultiThreadedHttpConnectionManager();
		HttpClient client = new HttpClient(manager);
		HttpMethod httpMethod=createMethod(pageContext.getConfig(),this,client,url,port);
		try {
		
/////////////////////////////////////////// EXECUTE /////////////////////////////////////////////////
		Executor e = new Executor(this,client,httpMethod,redirect);
		if(timeout<0){
			try{
				e.execute();
			}
			
			catch(Throwable t){
				if(!throwonerror){
					setUnknownHost(cfhttp, t);
					return;
				}
				throw toPageException(t);
				
			}
		}
		else {
			e.start();
			try {
				synchronized(this){//print.err(timeout);
					this.wait(timeout);
				}
			} catch (InterruptedException ie) {
				throw Caster.toPageException(ie);
			}
			if(e.t!=null){
				if(!throwonerror){
					setUnknownHost(cfhttp,e.t);
					return;
				}
				throw toPageException(e.t);	
			}
			
			httpMethod=e.httpMethod;
			
			
			if(!e.done){
				httpMethod.abort();
				if(throwonerror)
					throw new HTTPException("408 Request Time-out","a timeout occurred in tag http",408,"Time-out",null);
				setRequestTimeout(cfhttp);	
				return;
				//throw new ApplicationException("timeout");	
			}
		}
		httpMethod=e.httpMethod;
/////////////////////////////////////////// EXECUTE /////////////////////////////////////////////////
		int status = httpMethod.getStatusCode();
		
		String responseCharset=charset;
	// Write Response Scope
		//String rawHeader=httpMethod.getStatusLine().toString();
			String mimetype=null;
			String contentEncoding=null;
			
		// status code
			cfhttp.set(STATUSCODE,((httpMethod.getStatusCode()+" "+httpMethod.getStatusText()).trim()));
			cfhttp.set(STATUS_CODE,new Double(httpMethod.getStatusCode()));
			cfhttp.set(STATUS_TEXT,(httpMethod.getStatusText()));
			cfhttp.set(HTTP_VERSION,(httpMethod.getStatusLine().getHttpVersion()));
			
		//responseHeader
			Header[] headers = httpMethod.getResponseHeaders();
			StringBuffer raw=new StringBuffer(httpMethod.getStatusLine().toString()+" ");
			Struct responseHeader = new StructImpl();
			Array setCookie = new ArrayImpl();
			
	        for(int i=0;i<headers.length;i++) {
	        	Header header=headers[i];
	        	//print.ln(header);
		        
	        	raw.append(header+" ");
	        	if(header.getName().equalsIgnoreCase("Set-Cookie"))
	        		setCookie.append(header.getValue());
	        	else {
	        	    //print.ln(header.getName()+"-"+header.getValue());
	        		Object value=responseHeader.get(KeyImpl.getInstance(header.getName()),null);
	        		if(value==null) responseHeader.set(KeyImpl.getInstance(header.getName()),header.getValue());
	        		else {
	        		    Array arr=null;
	        		    if(value instanceof Array) {
	        		        arr=(Array) value;
	        		    }
	        		    else {
	        		        arr=new ArrayImpl();
	        		        responseHeader.set(KeyImpl.getInstance(header.getName()),arr);
	        		        arr.appendEL(value);
	        		    }
	        		    arr.appendEL(header.getValue());
	        		}
	        	}
	        	
	        	// Content-Type
	        	if(header.getName().equalsIgnoreCase("Content-Type")) {
	        		mimetype=header.getValue();
		    	    if(mimetype==null)mimetype=NO_MIMETYPE;
	        	}
	        	
	        	// Content-Encoding
        		if(header.getName().equalsIgnoreCase("Content-Encoding")) {
        			contentEncoding=header.getValue();
        		}
	        	
	        }
	        cfhttp.set(RESPONSEHEADER,responseHeader);
	        responseHeader.set(STATUS_CODE,new Double(httpMethod.getStatusCode()));
	        responseHeader.set(EXPLANATION,(httpMethod.getStatusText()));
	        if(setCookie.size()>0)responseHeader.set(SET_COOKIE,setCookie);
	        
	    // is text 
	        boolean isText=
	        	mimetype == null ||  
	        	mimetype == NO_MIMETYPE || HTTPUtil.isTextMimeType(mimetype);
	        	
	        
	       
	        cfhttp.set(TEXT,Caster.toBoolean(isText));
	        
	    // mimetype charset
	        //boolean responseProvideCharset=false;
	        if(!StringUtil.isEmpty(mimetype)){
		        if(isText) {
		        	String[] types=HTTPUtil.splitMimeTypeAndCharset(mimetype,null);
		        	if(types!=null) {
			        	if(types[0]!=null)cfhttp.set(MIME_TYPE,types[0]);
			        	if(types[1]!=null)cfhttp.set(CHARSET,types[1]);
		        	}
	                
		        }
		        else cfhttp.set(MIME_TYPE,mimetype);
	        }
	        else cfhttp.set(MIME_TYPE,NO_MIMETYPE);

	    // File
	        Resource file=null;
	        
	        if(strFile!=null && strPath!=null) {
	            file=ResourceUtil.toResourceNotExisting(pageContext, strPath).getRealResource(strFile);
	        }
	        else if(strFile!=null) {
	            file=ResourceUtil.toResourceNotExisting(pageContext, strFile);
	        }
	        else if(strPath!=null) {
	            file=ResourceUtil.toResourceNotExisting(pageContext, strPath);
	            //Resource dir = file.getParentResource();
	            if(file.isDirectory()){
	            	file=file.getRealResource(httpMethod.getURI().getName());
	            }
	            
	        }
	        if(file!=null)pageContext.getConfig().getSecurityManager().checkFileLocation(file);
	        
	        
	        // filecontent
	        //try {
	        //print.ln(">> "+responseCharset);

		    InputStream is=null;
		    if(isText && getAsBinary!=GET_AS_BINARY_YES) {
		    	String str;
                try {
                	is = httpMethod.getResponseBodyAsStream();
                    if(is!=null &&isGzipEncoded(contentEncoding))
                    	is = new GZIPInputStream(is);
                        	
                    try {
                    	str = is==null?"":IOUtil.toString(is,responseCharset);
                    }
                    catch (UnsupportedEncodingException uee) {
                    	str = IOUtil.toString(is,(Charset)null);
                    }
                }
                catch (IOException ioe) {
                	throw Caster.toPageException(ioe);
                }
                finally {
                	IOUtil.closeEL(is);
                }
                    
                if(str==null)str="";
		        if(resolveurl){
		        	//URI uri = httpMethod.getURI();
		        	if(e.redirectURL!=null)url=e.redirectURL.toExternalForm();
		        	str=new URLResolver().transform(str,new URL(url),false);
		        }
		        cfhttp.set(FILE_CONTENT,str);
		        try {
		        	if(file!=null){
		        		IOUtil.write(file,str,pageContext.getConfig().getWebCharset(),false);
                    }
                } 
		        catch (IOException e1) {}
		        
		        if(name!=null) {
                    Query qry = CSVParser.toQuery( str, delimiter, textqualifier, columns, firstrowasheaders  );
                    pageContext.setVariable(name,qry);
		        }
		    }
		    // Binary
		    else {
		    	byte[] barr=null;
		        if(isGzipEncoded(contentEncoding)){
		        	is = new GZIPInputStream(httpMethod.getResponseBodyAsStream());
		        	try {
		        		barr = IOUtil.toBytes(is);
					} 
		        	catch (IOException t) {
		        		throw Caster.toPageException(t);
					}
					finally{
						IOUtil.closeEL(is);
					}
		        }
		        else {
		        	try {
		        		barr = httpMethod.getResponseBody();
					} 
		        	catch (IOException t) {
		        		throw Caster.toPageException(t);
					}
		        }
		        	
		        cfhttp.set(FILE_CONTENT,barr);
		        
		        if(file!=null) {
		        	try {
		        		if(barr!=null)IOUtil.copy(new ByteArrayInputStream(barr),file,true);
		        	} 
		        	catch (IOException ioe) {
                		throw Caster.toPageException(ioe);
		        	}
		        }   
		    }
	        
	    // header		
	        cfhttp.set(HEADER,raw.toString());
	       
	        if(status!=STATUS_OK){
	            cfhttp.setEL(ERROR_DETAIL,httpMethod.getStatusCode()+" "+httpMethod.getStatusText());
	            if(throwonerror){
	            	int code=httpMethod.getStatusCode();
	            	String text=httpMethod.getStatusText();
	            	String msg=code+" "+text;
	            	throw new HTTPException(msg,null,code,text,null);
	            }
	        }
		}
		finally {
			releaseConnection(httpMethod);
		}
	    
	}

	private PageException toPageException(Throwable t) {
		PageException pe = Caster.toPageException(t);
		if(pe instanceof NativeException) {
			((NativeException) pe).setAdditional(KeyConstants._url, url);
		}
		return pe;
	}

	private void setUnknownHost(Struct cfhttp,Throwable t) {
		cfhttp.setEL(CHARSET,"");
		cfhttp.setEL(ERROR_DETAIL,"Unknown host: "+t.getMessage());
		cfhttp.setEL(FILE_CONTENT,"Connection Failure");
		cfhttp.setEL(HEADER,"");
		cfhttp.setEL(MIME_TYPE,"Unable to determine MIME type of file.");
		cfhttp.setEL(RESPONSEHEADER,new StructImpl());
		cfhttp.setEL(STATUSCODE,"Connection Failure. Status code unavailable.");
		cfhttp.setEL(TEXT,Boolean.TRUE);
	}

	private void setRequestTimeout(Struct cfhttp) {
		cfhttp.setEL(CHARSET,"");
		cfhttp.setEL(ERROR_DETAIL,"");
		cfhttp.setEL(FILE_CONTENT,"Connection Timeout");
		cfhttp.setEL(HEADER,"");
		cfhttp.setEL(MIME_TYPE,"Unable to determine MIME type of file.");
		cfhttp.setEL(RESPONSEHEADER,new StructImpl());
		cfhttp.setEL(STATUSCODE,"408 Request Time-out");
		cfhttp.setEL(STATUS_CODE,new Double(408));
		cfhttp.setEL(STATUS_TEXT,"Request Time-out");
		cfhttp.setEL(TEXT,Boolean.TRUE);
	}

	/*private static HttpMethod execute(Http http, HttpClient client, HttpMethod httpMethod, boolean redirect) throws PageException {
		try {
			// Execute Request
			short count=0;
	        URL lu;
	        
	        while(isRedirect(client.executeMethod(httpMethod)) && redirect && count++ < MAX_REDIRECT) { 
	        	lu=locationURL(httpMethod);
	        	httpMethod=createMethod(http,client,lu.toExternalForm(),-1);
	        }
        } 
		catch (IOException e) {
        	PageException pe = Caster.toPageException(e);
			if(pe instanceof NativeException) {
				((NativeException) pe).setAdditional("url", HTTPUtil.toURL(httpMethod));
			}
			throw pe;
        }
		return httpMethod;
	}*/

	public static void releaseConnection(HttpMethod httpMethod) {
		httpMethod.releaseConnection();
		//manager.closeIdleConnections(0);
	}

	static URL locationURL(HttpMethod method) throws MalformedURLException, ExpressionException {
        Header location = method.getResponseHeader("location");
        
        if(location==null) throw new ExpressionException("missing location header definition");
        
        
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
            
        return url;
    }
	

	static HttpMethod createMethod(Config cw,Http3 http, HttpClient client, String url, int port) throws PageException, UnsupportedEncodingException {
		String _charset=http.charset;
		if(StringUtil.isEmpty(_charset,true)) _charset=cw.getWebCharset();
		else _charset=_charset.trim();
		
		
		HttpMethod httpMethod;
		HostConfiguration config = client.getHostConfiguration();
		HttpState state = client.getState();
		
		String[] arrQS=new String[0];
	// check if has fileUploads	
		boolean doUploadFile=false;
		for(int i=0;i<http.params.size();i++) {
			if((http.params.get(i)).getType().equals("file")) {
				doUploadFile=true;
				break;
			}
		}	
	
	// parse url (also query string)
		URL _url=null;
		try {
			_url = HTTPUtil.toURL(url,port,true);
			url=_url.toExternalForm();
			
			
		} catch (MalformedURLException mue) {
			throw Caster.toPageException(mue);
		}
		
		
	// QS
		String strQS=_url.getQuery();
		if(strQS!=null) {
			arrQS=ListUtil.toStringArray(ListUtil.listToArray(ListUtil.trim(strQS,"&"),"&"));
		}
		
	// select best matching method (get,post, post multpart (file))

		boolean isBinary = false;
		boolean doMultiPart=doUploadFile || http.multiPart;
		PostMethod post=null;
		EntityEnclosingMethod eem=null;
		
		
		if(http.method==METHOD_GET) {
			httpMethod=new GetMethod(url);
		}
		else if(http.method==METHOD_HEAD) {
		    httpMethod=new HeadMethod(url);
		}
		else if(http.method==METHOD_DELETE) {
			isBinary=true;
		    httpMethod=new DeleteMethod(url);
		}
		else if(http.method==METHOD_PUT) {
			isBinary=true;
		    httpMethod=eem=new PutMethod(url);
		    
		}
		else if(http.method==METHOD_TRACE) {
			isBinary=true;
		    httpMethod=new TraceMethod(url);
		}
		else if(http.method==METHOD_OPTIONS) {
			isBinary=true;
		    httpMethod=new OptionsMethod(url);
		}
		else {
			isBinary=true;
			post=new PostMethod(url);
			httpMethod=eem=post;
		}
		// content type
		if(StringUtil.isEmpty(http.charset))http.charset=http.pageContext.getConfig().getWebCharset();
		
	
		boolean hasForm=false;
		boolean hasBody=false;
		boolean hasContentType=false;
	// Set http params
		ArrayList<NameValuePair> listQS=new ArrayList<NameValuePair>();
		ArrayList<Part> parts=new ArrayList<Part>();
		int len=http.params.size();
		StringBuilder acceptEncoding=new StringBuilder();
		for(int i=0;i<len;i++) {
			HttpParamBean param=http.params.get(i);
			String type=param.getType();
		// URL
			if(type.equals("url")) {
				listQS.add(new NameValuePair(translateEncoding(param.getName(), http.charset),translateEncoding(param.getValueAsString(), http.charset)));
			}
		// Form
			else if(type.equals("formfield") || type.equals("form")) {
				hasForm=true;
				if(http.method==METHOD_GET) throw new ApplicationException("httpparam type formfield can't only be used, when method of the tag http equal post");
				if(post!=null){
					if(doMultiPart){
						parts.add(new RailoStringPart(param.getName(),param.getValueAsString(),_charset));
					}
					else post.addParameter(new NameValuePair(param.getName(),param.getValueAsString()));
				}
				//else if(multi!=null)multi.addParameter(param.getName(),param.getValueAsString());
			}
		// CGI
			else if(type.equals("cgi")) {
				if(param.getEncoded())
				    httpMethod.addRequestHeader(
                            translateEncoding(param.getName(),http.charset),
                            translateEncoding(param.getValueAsString(),http.charset));
                else
                    httpMethod.addRequestHeader(param.getName(),param.getValueAsString());
			}
        // Header
            else if(type.startsWith("head")) {
            	if(param.getName().equalsIgnoreCase("content-type")) hasContentType=true;
            	
            	if(param.getName().equalsIgnoreCase("Accept-Encoding")) {
            		acceptEncoding.append(headerValue(param.getValueAsString()));
            		acceptEncoding.append(", ");
            	}
            	else httpMethod.addRequestHeader(param.getName(),headerValue(param.getValueAsString()));
            }
		// Cookie
			else if(type.equals("cookie")) {
				Cookie c=toCookie(_url.getHost(),param.getName(),param.getValueAsString(),_charset);
				c.setPath("/");
				client.getState().addCookie(c);
			}
		// File
			else if(type.equals("file")) {
				hasForm=true;
				if(http.method==METHOD_GET) throw new ApplicationException("httpparam type file can't only be used, when method of the tag http equal post");
				if(doMultiPart) {
					try {
						parts.add(new ResourcePart(param.getName(),new ResourcePartSource(param.getFile()),getContentType(param),_charset));
					} 
					catch (FileNotFoundException e) {
						throw new ApplicationException("can't upload file, path is invalid",e.getMessage());
					}
				}
			}
		// XML
			else if(type.equals("xml")) {
				hasBody=true;
				hasContentType=true;
				httpMethod.addRequestHeader("Content-type", "text/xml; charset="+http.charset);
			    //post.setRequestBody(new NameValuePair [] {new NameValuePair(translateEncoding(param.getName(), charset),translateEncoding(param.getValue(), charset))});
				if(eem==null)throw new ApplicationException("type xml is only supported for type post and put");
			    eem.setRequestBody(param.getValueAsString());
			}
		// Body
			else if(type.equals("body")) {
				hasBody=true;
				if(eem==null)throw new ApplicationException("type body is only supported for type post and put");
			    Object value = param.getValue();
			    
			    if(value instanceof InputStream) {
					eem.setRequestEntity(new InputStreamRequestEntity((InputStream)value,"application/octet-stream"));
				}
				else if(Decision.isCastableToBinary(value,false)){
					eem.setRequestEntity(new ByteArrayRequestEntity(Caster.toBinary(value)));
				}
				else {
					eem.setRequestEntity(new StringRequestEntity(param.getValueAsString()));
				}
			}
            else {
                throw new ApplicationException("invalid type ["+type+"]");
            }
		    
		}
		
		httpMethod.setRequestHeader("Accept-Encoding",acceptEncoding.append("gzip").toString());
		
		
		
		// multipart
		if(doMultiPart && eem!=null) {
			hasContentType=true;
			boolean doIt=true;
			if(!http.multiPart && parts.size()==1){
				Part part = parts.get(0);
				/* jira 1513
				  if(part instanceof ResourcePart){
					ResourcePart rp = (ResourcePart) part;
					eem.setRequestEntity(new ResourceRequestEntity(rp.getResource(),rp.getContentType()));
					doIt=false;
				}
				else */
					if(part instanceof RailoStringPart){
					RailoStringPart sp = (RailoStringPart) part;
					try {
						eem.setRequestEntity(new StringRequestEntity(sp.getValue(),sp.getContentType(),sp.getCharSet()));
					} catch (IOException e) {
						throw Caster.toPageException(e);
					}
					doIt=false;
				}
			}
			if(doIt)
				eem.setRequestEntity(new MultipartRequestEntityFlex(parts.toArray(new Part[parts.size()]), eem.getParams(),http.multiPartType));
		}
		
		
		
		if(hasBody && hasForm)
			throw new ApplicationException("mixing httpparam  type file/formfield and body/XML is not allowed");
	
		if(!hasContentType) {
			if(isBinary) {
				if(hasBody) httpMethod.addRequestHeader("Content-type", "application/octet-stream");
				else httpMethod.addRequestHeader("Content-type", "application/x-www-form-urlencoded; charset="+http.charset);
			}
			else {
				if(hasBody)
					httpMethod.addRequestHeader("Content-type", "text/html; charset="+http.charset ); 
			}
		}
		
		
		// set User Agent
			httpMethod.setRequestHeader("User-Agent",http.useragent);
		
	// set timeout
		if(http.timeout>0L)client.setConnectionTimeout((int)http.timeout);
		// for 3.0 client.getParams().setConnectionManagerTimeout(timeout);
		
	// set Query String
		//NameValuePair[] qsPairs=new NameValuePair[arrQS.length+listQS.size()];
		java.util.List<NameValuePair> listPairs=new ArrayList<NameValuePair>();
		
		//int count=0;
		// QS from URL
		for(int i=0;i<arrQS.length;i++) {
			if(StringUtil.isEmpty(arrQS[i])) continue;
			
			String[] pair=ListUtil.toStringArray(ListUtil.listToArray(arrQS[i],'='));
			if(ArrayUtil.isEmpty(pair)) continue;
			
			String name=pair[0];
			String value=pair.length>1?pair[1]:null;
			listPairs.add(new NameValuePair(name,value));
		}
		
		// QS from http Param
		len=listQS.size();
		for(int i=0;i<len;i++) {
			listPairs.add(listQS.get(i));
		}
		
		// set to method
		String qs = toQueryString(listPairs.toArray(new NameValuePair[listPairs.size()]));
		if(!StringUtil.isEmpty(qs))
			httpMethod.setQueryString(qs);

		
	// set Username and Password
		if(http.username!=null) {
			if(http.password==null)http.password="";
			//client.getState().setAuthenticationPreemptive(true);
			client.getState().setCredentials(null,null,new UsernamePasswordCredentials(http.username, http.password));
			httpMethod.setDoAuthentication( true );
			client.getState().setAuthenticationPreemptive(true);
			
		}
	
	// set Proxy
		if(StringUtil.isEmpty(http.proxyserver) && http.pageContext.getConfig().isProxyEnableFor(_url.getHost())) { 

			ProxyData pd = http.pageContext.getConfig().getProxyData();
			http.proxyserver=pd==null?null:pd.getServer();
			http.proxyport=pd==null?0:pd.getPort();
			http.proxyuser=pd==null?null:pd.getUsername();
			http.proxypassword=pd==null?null:pd.getPassword();
		}
		if(!StringUtil.isEmpty(http.proxyserver)) {
            config.setProxy(http.proxyserver,http.proxyport);
            if(!StringUtil.isEmpty(http.proxyuser)) {
                state.setProxyCredentials(null,null,new UsernamePasswordCredentials(http.proxyuser,http.proxypassword));
            }
        }

		

		httpMethod.setFollowRedirects(false);
	    return httpMethod;
	}
	
	private static Cookie toCookie(String domain, String name, String value, String charset) {
		if(!ReqRspUtil.needEncoding(name,false)) name=ReqRspUtil.encode(name, charset);
		if(!ReqRspUtil.needEncoding(value,false)) value=ReqRspUtil.encode(value, charset);
		
		return new Cookie(domain, name, value);
	}

	private static String headerValue(String value) {
		if(value==null) return null;
		value=value.trim();
		int len=value.length();
		char c;
		for(int i=0;i<len;i++){
			c=value.charAt(i);
			if(c=='\n' || c=='\r') return value.substring(0,i);
		}
		return value;
	}

	private static String toQueryString(NameValuePair[] qsPairs) {
		StringBuffer sb=new StringBuffer();
        for(int i=0;i<qsPairs.length;i++) {
            if(sb.length()>0)sb.append('&');
            sb.append(qsPairs[i].getName());
            if(qsPairs[i].getValue()!=null){
            	sb.append('=');
            	sb.append(qsPairs[i].getValue());
            }
        }
        return sb.toString();
    }

    private static String translateEncoding(String str, String charset) throws UnsupportedEncodingException {
    	if(!ReqRspUtil.needEncoding(str,false)) return str;
    	return URLEncoder.encode(str,charset);
    }

    @Override
	public void doInitBody()	{
		
	}

	@Override
	public int doAfterBody()	{
		return SKIP_BODY;
	}

	/**
	 * sets if has body or not
	 * @param hasBody
	 */
	public void hasBody(boolean hasBody) {
	    
	}

	/**
	 * @param param
	 */
	public void setParam(HttpParamBean param) {
		params.add(param);
		
	}
	
	
    /**
     * @param getAsBinary The getasbinary to set.
     */
    public void setGetasbinary(String getAsBinary) {
    	// TODO support never, wird das verwendet?
        getAsBinary=getAsBinary.toLowerCase().trim();
        if(getAsBinary.equals("yes") || getAsBinary.equals("true")) 		this.getAsBinary=GET_AS_BINARY_YES;
        else if(getAsBinary.equals("no") || getAsBinary.equals("false")) 	this.getAsBinary=GET_AS_BINARY_NO;
        else if(getAsBinary.equals("auto")) 								this.getAsBinary=GET_AS_BINARY_AUTO;
    }

    /**
     * @param multipart The multipart to set.
     */
    public void setMultipart(boolean multiPart) {
        this.multiPart = multiPart;
    }

    /**
     * @param multipart The multipart to set.
     * @throws ApplicationException 
     */
    public void setMultiparttype(String multiPartType) throws ApplicationException {
    	if(StringUtil.isEmpty(multiPartType))return;
    	multiPartType=multiPartType.trim().toLowerCase();
    	
    	if("form-data".equals(multiPartType)) 	this.multiPartType=MultipartRequestEntityFlex.MULTIPART_FORM_DATA;
    	//else if("related".equals(multiPartType)) 		this.multiPartType=MultipartRequestEntityFlex.MULTIPART_RELATED;
    	else
			throw new ApplicationException("invalid value for attribute multiPartType ["+multiPartType+"]",
					"attribute must have one of the folloing values [form-data]");
			
    }

    /**
     * @param result The result to set.
     */
    public void setResult(String result) {
        this.result = result;
    }

	/**
	 * @param addtoken the addtoken to set
	 */
	public void setAddtoken(boolean addtoken) {
		this.addtoken = addtoken;
	}
	
	/**
     * checks if status code is a redirect
     * @param status
     * @return is redirect
     */
    
	static boolean isRedirect(int status) {
    	return 
        	status==STATUS_REDIRECT_FOUND || 
        	status==STATUS_REDIRECT_MOVED_PERMANENTLY ||
        	status==STATUS_REDIRECT_SEE_OTHER ||
        	status==STATUS_REDIRECT_TEMPORARY_REDIRECT;
    	
    	
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
    
	private static String getContentType(HttpParamBean param) {
		String mimeType=param.getMimeType();
		if(StringUtil.isEmpty(mimeType,true)) {
			mimeType=ResourceUtil.getMimeType(param.getFile(), ResourceUtil.MIMETYPE_CHECK_EXTENSION+ResourceUtil.MIMETYPE_CHECK_HEADER, null);
		}
		return mimeType;
	}

	public static boolean isGzipEncoded(String contentEncoding) {
		return !StringUtil.isEmpty(contentEncoding) && StringUtil.indexOfIgnoreCase(contentEncoding, "gzip")!=-1;
	}

	public static Object getOutput(InputStream is, String contentType, String contentEncoding, boolean closeIS) {
		if(StringUtil.isEmpty(contentType))contentType="text/html";
		
		// Gzip
		if(Http3.isGzipEncoded(contentEncoding)){
			try {
				is=new GZIPInputStream(is);
			} 
			catch (IOException e) {}
		}
		
		try {
			// text
			if(HTTPUtil.isTextMimeType(contentType)) {
				String[] tmp = HTTPUtil.splitMimeTypeAndCharset(contentType,null);
				//String mimetype=tmp[0];
				String charset=tmp[1];
				
				if(StringUtil.isEmpty(charset,true)) {
					Config config = ThreadLocalPageContext.getConfig();
					if(config!=null)charset=config.getWebCharset();
				}
				
				try {
					return IOUtil.toString(is, charset);
				} catch (IOException e) {}
			}
			// Binary
			else {
				try {
					return IOUtil.toBytes(is);
				} 
				catch (IOException e) {}
			}
		}
		finally{
			if(closeIS)IOUtil.closeEL(is);
		}

		return "";
	}
	
}

class MultipartRequestEntityFlex extends MultipartRequestEntity {
	
	
	public static final String MULTIPART_RELATED = "multipart/related";
	public static final String MULTIPART_FORM_DATA = "multipart/form-data";
	private String multipartType;
	
	
	/**
	 * Constructor of the class
	 * @param parts
	 * @param params
	 * @param multipartType use constant MultipartRequestEntityFlex.MULTIPART_FORM_DATA or MultipartRequestEntityFlex.MULTIPART_RELATED
	 */
	public MultipartRequestEntityFlex(Part[] parts, HttpMethodParams params,String multipartType) {
		super(parts, params);
		this.multipartType=multipartType;
	}
	
	@Override
	public String getContentType() {
	   StringBuilder builder = new StringBuilder(multipartType);
	   builder.append("; boundary=");
	   builder.append(EncodingUtil.getAsciiString(getMultipartBoundary()));

	   return builder.toString();
	   
	   //return super.getContentType();
	}
}

class Executor extends Thread {
	
	 final Http3 http;
	 final HttpClient client;
	 HttpMethod httpMethod;
	 final boolean redirect;
	 Throwable t;
	 boolean done;
	URL redirectURL;

	public Executor(Http3 http, HttpClient client,HttpMethod httpMethod,boolean redirect) {
		this.http=http;
		this.client=client;
		this.httpMethod=httpMethod;
		this.redirect=redirect;
	}
	

	public void run(){
		try {
			execute();
			done=true;
			synchronized(http){
				http.notify();
			}
		} catch (Throwable t) {
			this.t=t;
		}
	}
	
	public void execute() throws IOException, PageException	{
		// Execute Request 
		
		short count=0;
        URL lu;
        while(Http3.isRedirect(client.executeMethod(httpMethod)) && redirect && count++ < HTTPEngine.MAX_REDIRECT) { 
        	lu=Http3.locationURL(httpMethod);
        	redirectURL=lu;
        	HttpMethod oldHttpMethod = httpMethod;
        	httpMethod=Http3.createMethod(ThreadLocalPageContext.getConfig(),http,client,lu.toExternalForm(),-1);
        	Http3.releaseConnection(oldHttpMethod);
        }
        
	}
}