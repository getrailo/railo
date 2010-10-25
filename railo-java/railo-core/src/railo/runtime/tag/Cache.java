package railo.runtime.tag;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.oro.text.regex.MalformedPatternException;

import railo.commons.io.IOUtil;
import railo.commons.io.cache.CacheEntry;
import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.Md5;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContextImpl;
import railo.runtime.cache.legacy.CacheItem;
import railo.runtime.cache.legacy.MetaData;
import railo.runtime.config.ConfigImpl;
import railo.runtime.exp.Abort;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.DeprecatedException;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.exp.TemplateException;
import railo.runtime.ext.tag.BodyTagImpl;
import railo.runtime.functions.cache.CacheGet;
import railo.runtime.functions.cache.CachePut;
import railo.runtime.functions.cache.CacheRemove;
import railo.runtime.functions.cache.Util;
import railo.runtime.functions.dateTime.GetHttpTimeString;
import railo.runtime.op.Caster;
import railo.runtime.type.StructImpl;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.dt.DateTimeImpl;
import railo.runtime.type.dt.TimeSpan;
import railo.runtime.type.dt.TimeSpanImpl;
import railo.runtime.writer.CFMLWriterImpl;

/**
* Speeds up page rendering when dynamic content does not have to be retrieved each time a user accesses
*   the page. To accomplish this, cfcache creates temporary files that contain the static HTML returned from
*   a ColdFusion page. You can use cfcache for simple URLs and URLs that contain URL parameters.
*
*
*
**/
public final class Cache extends BodyTagImpl {




	private static final TimeSpan TIMESPAN_FAR_AWAY = new TimeSpanImpl(1000000000,1000000000,1000000000,1000000000); 
	private static final TimeSpan TIMESPAN_0 = new TimeSpanImpl(0,0,0,0); 


	/**  */
	private Resource directory;


	/** Specifies the protocol used to create pages from cache. Either http:// or https://. The default
	** 		is http://. */
	private String protocol;

	/**  */
	private String expireurl;

	/**  */
	private int action=CACHE;

	/** When required for basic authentication, a valid username. */
	private String username;

	/** When required for basic authentication, a valid password. */
	private String password;
    
	private TimeSpan timespan=TIMESPAN_FAR_AWAY;
	private TimeSpan idletime=TIMESPAN_0;
    

	/**  */
	private int port=-1;

	private DateTimeImpl now;


	private String body;
	private String _id;
	private Object id;
	private String name;
	private String key;


	private boolean hasBody;


	private boolean doCaching;


	private CacheItem cacheItem;


	private String cachename;
	private Object value;
	private boolean throwOnError;
	private String metadata;
    
    private static final int CACHE=0;
    private static final int CACHE_SERVER=1;
    private static final int CACHE_CLIENT=2;
    private static final int FLUSH=3;
    private static final int CONTENT=4;

    private static final int GET=5;
    private static final int PUT=6;
    
    /**
    * @see javax.servlet.jsp.tagext.Tag#release()
    */
    public void release()   {
        super.release();
        directory=null;
        username=null;
        password=null;
        protocol=null;
        expireurl=null;
        action=CACHE;
        port=-1;
        timespan=TIMESPAN_FAR_AWAY;
        idletime=TIMESPAN_0;
        body=null;
        hasBody=false;
        id=null;
        key=null;
        body=null;
        doCaching=false;
        cacheItem=null;
        name=null;
        cachename=null;
        throwOnError=false;
        value=null;
        metadata=null;
    }
	
	/**
	 * @deprecated this attribute is deprecated and will ignored in this tag
	 * @param obj
	 * @throws DeprecatedException
	 */
	public void setTimeout(Object obj) throws DeprecatedException {
		throw new DeprecatedException("Cache","timeout");
	}

	/** set the value directory
	*  
	* @param directory value to set
	**/
	public void setDirectory(String directory) throws ExpressionException	{
		this.directory=ResourceUtil.toResourceExistingParent(pageContext, directory);
	}
	public void setCachedirectory(String directory) throws ExpressionException	{
		setDirectory(directory);
	}
	private Resource getDirectory() throws IOException {
		if(directory==null) {
			directory= pageContext.getConfig().getCacheDir();	
        }
        if(!directory.exists())directory.createDirectory(true);
        return directory;
	}
	

	/** set the value protocol
	*  Specifies the protocol used to create pages from cache. Either http:// or https://. The default
	* 		is http://.
	* @param protocol value to set
	**/
	public void setProtocol(String protocol)	{
		if(protocol.endsWith("://"))protocol=protocol.substring(0,protocol.indexOf("://"));
		this.protocol=protocol.toLowerCase();
	}
	
	private String getProtocol()	{
		if(StringUtil.isEmpty(protocol)) {
			return pageContext. getHttpServletRequest().getScheme();
		}
		return protocol;
	}

	/** set the value expireurl
	*  
	* @param expireurl value to set
	**/
	public void setExpireurl(String expireurl)	{
		this.expireurl=expireurl;
	}

	/** set the value action
	*  
	* @param action value to set
	 * @throws ApplicationException 
	**/
	public void setAction(String action) throws ApplicationException	{
        action=action.toLowerCase().trim();
        if(action.equals("get"))              this.action=GET; 
        else if(action.equals("put"))              this.action=PUT; 
        
        
        else if(action.equals("cache"))         this.action=CACHE; 
        else if(action.equals("clientcache"))   this.action=CACHE_CLIENT;
        else if(action.equals("servercache"))   this.action=CACHE_SERVER;
        else if(action.equals("flush"))         this.action=FLUSH;
        else if(action.equals("optimal"))       this.action=CACHE;
        
        else if(action.equals("client-cache"))   this.action=CACHE_CLIENT;
        else if(action.equals("client_cache"))   this.action=CACHE_CLIENT;
        
        else if(action.equals("server-cache"))   this.action=CACHE_SERVER;
        else if(action.equals("server_cache"))   this.action=CACHE_SERVER;
        
        else if(action.equals("content"))   	this.action=CONTENT;
        else if(action.equals("content_cache"))  this.action=CONTENT;
        else if(action.equals("contentcache"))  this.action=CONTENT;
        else if(action.equals("content-cache"))  this.action=CONTENT;
        else throw new ApplicationException("invalid value for attribute action for tag cache ["+action+"], " +
        		"valid actions are [get,put,cache, clientcache, servercache, flush, optimal, contentcache]");
        
        //get: get an object from the cache.
        //put: Add an object to the cache.
        

        
        
	}

	/** set the value username
	*  When required for basic authentication, a valid username.
	* @param username value to set
	**/
	public void setUsername(String username)	{
		this.username=username;
	}
	
	/** set the value password
	*  When required for basic authentication, a valid password.
	* @param password value to set
	**/
	public void setPassword(String password)	{
		this.password=password;
	}
	
	public void setKey(String key)	{
		this.key=key;
	}

	/** set the value port
	*  
	* @param port value to set
	**/
	public void setPort(double port)	{
		this.port=(int)port;
	}
	
	public int getPort() {
		if(port<=0) return pageContext. getHttpServletRequest().getServerPort();
		return port;
	}

    /**
     * @param timespan The timespan to set.
     * @throws PageException 
     */
    public void setTimespan(TimeSpan timespan) {
        this.timespan = timespan;
    }
    
    
    /**
	* @throws PageException 
     * @see javax.servlet.jsp.tagext.Tag#doStartTag()
	*/
	public int doStartTag() throws PageException	{
		now = new DateTimeImpl(pageContext.getConfig());
		try {
	        if(action==CACHE) {
	            doClientCache();
	            doServerCache();
	        }
	        else if(action==CACHE_CLIENT)	doClientCache();
	        else if(action==CACHE_SERVER)	doServerCache();
	        else if(action==FLUSH)			doFlush();
	        else if(action==CONTENT) return doContentCache();
	        else if(action==GET) doGet();
	        else if(action==PUT) doPut();
	    	
			return EVAL_PAGE;
		}
		catch(Exception e) {
			throw Caster.toPageException(e);
		}
	}

	/**
	* @see javax.servlet.jsp.tagext.BodyTag#doInitBody()
	*/
	public void doInitBody()	{
		
	}

	/**
	* @see javax.servlet.jsp.tagext.BodyTag#doAfterBody()
	*/
	public int doAfterBody()	{
		//print.out("doAfterBody");
		if(bodyContent!=null)body=bodyContent.getString();
		return SKIP_BODY;
	}
    
	/**
	* @throws PageException 
	 * @see javax.servlet.jsp.tagext.Tag#doStartTag()
	*/
	

    /**
     * @throws PageException 
     * @see javax.servlet.jsp.tagext.Tag#doEndTag()
	*/
	public int doEndTag() throws PageException	{//print.out("doEndTag"+doCaching+"-"+body);
		if(doCaching && body!=null) {
    		try {
				writeCacheResource(cacheItem, body);
				pageContext.write(body);
			}
    		catch (IOException e) {
				throw Caster.toPageException(e);
			}
		}
		return EVAL_PAGE;
	}

	private void doClientCache() {
        pageContext.setHeader("Last-Modified",GetHttpTimeString.call(pageContext,now));
        
        if(timespan!=null) {
            DateTime expires = getExpiresDate();
            pageContext.setHeader("Expires",GetHttpTimeString.call(pageContext,expires));
        }
    }

	private void doServerCache() throws IOException, PageException {
		if(hasBody)hasBody=!StringUtil.isEmpty(body);
		
        // call via cfcache disable debugger output
            pageContext.getDebugger().setOutput(false);
		
        HttpServletRequest req = pageContext. getHttpServletRequest();
        
        // generate cache resource matching request object
        CacheItem ci = generateCacheResource(req,null,false);
        Resource cacheResource=ci.getResource();
        
        // use cached resource
        if(isOK(cacheResource)){
        	if(pageContext. getHttpServletResponse().isCommitted()) return;
        	
        	InputStream is=null;
        	OutputStream os=null;
        	try {
                IOUtil.copy(is=cacheResource.getInputStream(),os=getOutputStream(),false,false);
            } 
            finally {
                IOUtil.flushEL(os);
                IOUtil.closeEL(is,os);
                ((PageContextImpl)pageContext).getRootOut().setClosed(true);
            }
        	throw new Abort(Abort.SCOPE_REQUEST);
        }
        
        // call page again and
        //MetaData.getInstance(getDirectory()).add(ci.getName(), ci.getRaw());
        
        PageContextImpl pci = (PageContextImpl)pageContext;
        ((CFMLWriterImpl)pci.getRootOut()).doCache(ci);
        	
    }

	private boolean isOK(Resource cacheResource) {
		return cacheResource.exists() && (cacheResource.lastModified()+timespan.getMillis()>=System.currentTimeMillis());
	}

	private int doContentCache() throws IOException {

        // file
        HttpServletRequest req = pageContext. getHttpServletRequest();
        cacheItem = generateCacheResource(req,key,true);
        // use cache
        if(isOK(cacheItem.getResource())){
        	pageContext.write(IOUtil.toString(cacheItem.getResource(),"UTF-8"));
        	doCaching=false;
            return SKIP_BODY;
        }
    	doCaching=true;
    	return EVAL_BODY_BUFFERED;
	}

	private void doGet() throws PageException, IOException {
		required("cache", "id", id);
		required("cache", "name", name);
		String id=Caster.toString(this.id);
		if(metadata==null){
			pageContext.setVariable(name,CacheGet.call(pageContext, id,throwOnError,cachename));	
		}
		else {
			railo.commons.io.cache.Cache cache = 
				Util.getCache(pageContext,cachename,ConfigImpl.CACHE_DEFAULT_OBJECT);
			CacheEntry entry = throwOnError?cache.getCacheEntry(Util.key(id)):cache.getCacheEntry(Util.key(id),null);
			if(entry!=null){
				pageContext.setVariable(name,entry.getValue());
				pageContext.setVariable(metadata,entry.getCustomInfo());
			}
			else {
				pageContext.setVariable(metadata,new StructImpl());
			}
		}
		
		
		
		
		
		
		
		
	}
	private void doPut() throws PageException {
		required("cache", "id", id);
		required("cache", "value", value);
		TimeSpan ts = timespan;
		TimeSpan it = idletime;
		if(ts==TIMESPAN_FAR_AWAY)ts=TIMESPAN_0;
		if(it==TIMESPAN_FAR_AWAY)it=TIMESPAN_0; 
		CachePut.call(pageContext, Caster.toString(id),value,ts,it,cachename);
	}
	

	

	private void doFlush() throws IOException, MalformedPatternException, PageException {
		if(id!=null){
			required("cache", "id", id);
			CacheRemove.call(pageContext, id,throwOnError,cachename);
		}
		if(StringUtil.isEmpty(expireurl)) {
			ResourceUtil.removeChildrenEL(getDirectory());
		}
		else {
			
			Resource dir = getDirectory();
			List names = MetaData.getInstance(dir).get(expireurl);
			Iterator it = names.iterator();
			String name;
			while(it.hasNext()){
				name=(String)it.next();
				//print.out(name);
				if(dir.getRealResource(name).delete());
					
			}
			//ResourceUtil.removeChildrenEL(getDirectory(),(ResourceNameFilter)new ExpireURLFilter(expireurl));
		}
    }


    private CacheItem generateCacheResource(HttpServletRequest req, String key, boolean useId) throws IOException {
    	String filename=req.getServletPath();
        if(!StringUtil.isEmpty(req.getQueryString())) {
        	filename+="?"+req.getQueryString();
        	if(useId)filename+="&cfcache_id="+_id;
        }
        else {
        	if(useId)filename+="?cfcache_id="+_id;
        }
    	if(useId && !StringUtil.isEmpty(key)) filename=key;
    	
    	Resource dir = getDirectory();
    	CacheItem ci=new CacheItem();
    	ci.setDirectory(dir);
    	ci.setRaw(filename);
        if(!StringUtil.isEmpty(req.getContextPath())) filename=req.getContextPath()+filename;
        filename=compressFileName(filename)+".cache";
        ci.setName(filename);
        ci.setResource(dir.getRealResource(filename));
        return ci;
	}

	private String compressFileName(String path) throws IOException {
		return Md5.getDigestAsString(path);
	}

	private void writeCacheResource(CacheItem cacheItem, String result) throws IOException {
		IOUtil.write(cacheItem.getResource(), result,"UTF-8", false); 
		MetaData.getInstance(cacheItem.getDirectory()).add(cacheItem.getName(), cacheItem.getRaw());
	}
	

    private DateTime getExpiresDate() {
		return new DateTimeImpl(pageContext,getExpiresTime(),false);
	}

	private long getExpiresTime() {
		return now.getTime()+(timespan.getMillis());
	}
	
	private OutputStream getOutputStream() throws PageException, IOException {
        try {
        	return ((PageContextImpl)pageContext).getServletOutputStream();
        } 
        catch(IllegalStateException ise) {
            throw new TemplateException("content is already send to user, flush");
        }
    }
	
	
	/**
     * sets if tag has a body or not
     * @param hasBody
     */
    public void hasBody(boolean hasBody) {
       this.hasBody=hasBody;
    }

	/**
	 * @param id the id to set
	 */
	public void set_id(String _id) {
		this._id = _id;
	}
	
	public void setId(Object id) {
		this.id = id;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	public void setCachename(String cachename) {
		this.cachename = cachename;
	}
	
	/**
	 * @param throwOnError the throwOnError to set
	 */
	public void setThrowonerror(boolean throwOnError) {
		this.throwOnError = throwOnError;
	}
	
	public void setValue(Object value) {
		this.value = value;
	}
	
	/**
	 * @param idletime the idletime to set
	 */
	public void setIdletime(TimeSpan idletime) {
		this.idletime = idletime;
	}

	/**
	 * @param metadata the metadata to set
	 */
	public void setMetadata(String metadata) {
		this.metadata = metadata;
	}
}
	