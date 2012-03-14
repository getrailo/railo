package railo.runtime;


import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTag;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TryCatchFinally;

import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternMatcherInput;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;

import railo.print;
import railo.commons.io.BodyContentStack;
import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.SizeOf;
import railo.commons.lang.StringList;
import railo.commons.lang.StringUtil;
import railo.commons.lang.types.RefBoolean;
import railo.commons.lang.types.RefBooleanImpl;
import railo.commons.lock.KeyLock;
import railo.commons.lock.Lock;
import railo.commons.net.HTTPUtil;
import railo.intergral.fusiondebug.server.FDSignal;
import railo.runtime.component.ComponentLoader;
import railo.runtime.config.Config;
import railo.runtime.config.ConfigImpl;
import railo.runtime.config.ConfigServerImpl;
import railo.runtime.config.ConfigWeb;
import railo.runtime.config.ConfigWebImpl;
import railo.runtime.config.Constants;
import railo.runtime.db.DataSource;
import railo.runtime.db.DataSourceManager;
import railo.runtime.db.DatasourceConnection;
import railo.runtime.db.DatasourceConnectionPool;
import railo.runtime.db.DatasourceManagerImpl;
import railo.runtime.debug.DebugEntry;
import railo.runtime.debug.Debugger;
import railo.runtime.debug.DebuggerImpl;
import railo.runtime.dump.DumpUtil;
import railo.runtime.engine.ExecutionLog;
import railo.runtime.err.ErrorPage;
import railo.runtime.err.ErrorPageImpl;
import railo.runtime.err.ErrorPagePool;
import railo.runtime.exp.Abort;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.CasterException;
import railo.runtime.exp.ExceptionHandler;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.MissingIncludeException;
import railo.runtime.exp.PageException;
import railo.runtime.exp.PageExceptionBox;
import railo.runtime.exp.PageServletException;
import railo.runtime.functions.dynamicEvaluation.Serialize;
import railo.runtime.interpreter.CFMLExpressionInterpreter;
import railo.runtime.interpreter.VariableInterpreter;
import railo.runtime.listener.AppListenerSupport;
import railo.runtime.listener.ApplicationContext;
import railo.runtime.listener.ApplicationListener;
import railo.runtime.listener.ClassicApplicationContext;
import railo.runtime.listener.ModernAppListenerException;
import railo.runtime.monitor.RequestMonitor;
import railo.runtime.net.ftp.FTPPool;
import railo.runtime.net.ftp.FTPPoolImpl;
import railo.runtime.net.http.HTTPServletRequestWrap;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.orm.ORMConfiguration;
import railo.runtime.orm.ORMEngine;
import railo.runtime.orm.ORMSession;
import railo.runtime.query.QueryCache;
import railo.runtime.rest.Result;
import railo.runtime.security.Credential;
import railo.runtime.security.CredentialImpl;
import railo.runtime.tag.Login;
import railo.runtime.tag.TagHandlerPool;
import railo.runtime.type.Array;
import railo.runtime.type.Collection;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.Iterator;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Query;
import railo.runtime.type.SVArray;
import railo.runtime.type.Sizeable;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.UDF;
import railo.runtime.type.it.KeyIterator;
import railo.runtime.type.ref.Reference;
import railo.runtime.type.ref.VariableReference;
import railo.runtime.type.scope.Application;
import railo.runtime.type.scope.Argument;
import railo.runtime.type.scope.ArgumentImpl;
import railo.runtime.type.scope.CGI;
import railo.runtime.type.scope.CGIImpl;
import railo.runtime.type.scope.Client;
import railo.runtime.type.scope.Cluster;
import railo.runtime.type.scope.Cookie;
import railo.runtime.type.scope.CookieImpl;
import railo.runtime.type.scope.Form;
import railo.runtime.type.scope.FormImpl;
import railo.runtime.type.scope.Local;
import railo.runtime.type.scope.LocalNotSupportedScope;
import railo.runtime.type.scope.Request;
import railo.runtime.type.scope.RequestImpl;
import railo.runtime.type.scope.Scope;
import railo.runtime.type.scope.ScopeContext;
import railo.runtime.type.scope.ScopeFactory;
import railo.runtime.type.scope.ScopeSupport;
import railo.runtime.type.scope.Server;
import railo.runtime.type.scope.Session;
import railo.runtime.type.scope.Threads;
import railo.runtime.type.scope.URL;
import railo.runtime.type.scope.URLForm;
import railo.runtime.type.scope.URLImpl;
import railo.runtime.type.scope.Undefined;
import railo.runtime.type.scope.UndefinedImpl;
import railo.runtime.type.scope.UrlFormImpl;
import railo.runtime.type.scope.Variables;
import railo.runtime.type.scope.VariablesImpl;
import railo.runtime.util.VariableUtil;
import railo.runtime.util.VariableUtilImpl;
import railo.runtime.writer.CFMLWriter;
import railo.runtime.writer.DevNullBodyContent;

/**
 * page context for every page object. 
 * the PageContext is a jsp page context expanded by cold fusion functionality.
 * for example you have the method getSession to get jsp combatible session object (HTTPSession)
 *  and with sessionScope() you get CFML combatible session object (Struct,Scope).
 */
public final class PageContextImpl extends PageContext implements Sizeable {
	
	private static final RefBoolean DUMMY_BOOL = new RefBooleanImpl(false);
	private static final Key CFCATCH = KeyImpl.intern("cfcatch");
	private static final Key CATCH = KeyImpl.intern("catch");
	private static final Key CFTHREAD = KeyImpl.intern("cfthread");
	private static final Key ERROR = KeyImpl.intern("error");
	private static final Key CFERROR = KeyImpl.intern("cferror");
	
	private static int counter=0;
	
	/** 
	 * Field <code>pathList</code>
	 */
    private LinkedList<UDF> udfs=new LinkedList<UDF>();
    private LinkedList<PageSource> pathList=new LinkedList<PageSource>();
    private LinkedList<PageSource> includePathList=new LinkedList<PageSource>();
	
	/**
	 * Field <code>executionTime</code>
	 */
	protected int executionTime=0;
	
	private HTTPServletRequestWrap req;
	private HttpServletResponse rsp;
	private HttpServlet servlet;
	
	private JspWriter writer;
    private JspWriter forceWriter;
    private BodyContentStack bodyContentStack;
    private DevNullBodyContent devNull;
    
	private ConfigWebImpl config;
	//private DataSourceManager manager;
	//private CFMLCompilerImpl compiler;
	
	// Scopes
	private ScopeContext scopeContext;
    private Variables variablesRoot=new VariablesImpl();//ScopeSupport(false,"variables",Scope.SCOPE_VARIABLES);
    private Variables variables=variablesRoot;//new ScopeSupport("variables",Scope.SCOPE_VARIABLES);
    private UndefinedImpl undefined;
	
    private URLImpl _url=new URLImpl();
	private FormImpl _form=new FormImpl();
	
	private URLForm urlForm=new UrlFormImpl(_form,_url);
	private URL url;
	private Form form;
	
	
	private RequestImpl request=new RequestImpl();
	private CGIImpl cgi=new CGIImpl();	
	private Argument argument=new ArgumentImpl();
    private static LocalNotSupportedScope localUnsupportedScope=LocalNotSupportedScope.getInstance();
	private Local local=localUnsupportedScope;
	private Session session;
	private Server server;
	private Cluster cluster;
	private CookieImpl cookie=new CookieImpl();
	private Client client;
	private Application application;

    private DebuggerImpl debugger=new DebuggerImpl();
	private long requestTimeout=-1;
	private short enablecfoutputonly=0;
	private int outputState;
	private String cfid;
	private String cftoken;

	private int id;
	private int requestId;
	
	
	private boolean psq;
	private Locale locale;
	private TimeZone timeZone;
	
    // Pools
    private ErrorPagePool errorPagePool=new ErrorPagePool();
	private TagHandlerPool tagHandlerPool;
	private FTPPool ftpPool=new FTPPoolImpl();
	private QueryCache queryCache;

	private Component activeComponent;
	private UDF activeUDF;
    //private ComponentScope componentScope=new ComponentScope(this);
	
	private Credential remoteUser;
    
    protected VariableUtilImpl variableUtil=new VariableUtilImpl();

    private PageException exception;
    private PageSource base;

    ApplicationContext applicationContext;
    ApplicationContext defaultApplicationContext;

    private ScopeFactory scopeFactory=new ScopeFactory();

    private Tag parentTag=null;
    private Tag currentTag=null;
    private Thread thread;
    private long startTime;
	private boolean isCFCRequest;
	
	private DatasourceManagerImpl manager;
	private Struct threads;
	private boolean hasFamily=false;
	//private CFMLFactoryImpl factory;
	private PageContextImpl parent;
	private Map<String,DatasourceConnection> conns=new HashMap<String,DatasourceConnection>();
	private boolean fdEnabled;
	private ExecutionLog execLog;
	private boolean useSpecialMappings;
	

	private ORMSession ormSession;
	private boolean isChild;
	private boolean gatewayContext;
	private String serverPassword;

	public long sizeOf() {
		
		return 
		SizeOf.size(pathList)+
		SizeOf.size(includePathList)+
		SizeOf.size(executionTime)+
		SizeOf.size(writer)+
		SizeOf.size(forceWriter)+
		SizeOf.size(bodyContentStack)+
		SizeOf.size(variables)+
		SizeOf.size(url)+
		SizeOf.size(form)+
		SizeOf.size(_url)+
		SizeOf.size(_form)+
		SizeOf.size(request)+

		SizeOf.size(argument)+
		SizeOf.size(local)+
		SizeOf.size(cookie)+
		SizeOf.size(debugger)+
		SizeOf.size(requestTimeout)+
		SizeOf.size(enablecfoutputonly)+
		SizeOf.size(outputState)+
		SizeOf.size(cfid)+
		SizeOf.size(cftoken)+
		SizeOf.size(id)+
		SizeOf.size(psq)+
		SizeOf.size(locale)+
		SizeOf.size(errorPagePool)+
		SizeOf.size(tagHandlerPool)+
		SizeOf.size(ftpPool)+
		SizeOf.size(activeComponent)+
		SizeOf.size(activeUDF)+
		SizeOf.size(remoteUser)+
		SizeOf.size(exception)+
		SizeOf.size(base)+
		SizeOf.size(applicationContext)+
		SizeOf.size(defaultApplicationContext)+
		SizeOf.size(parentTag)+
		SizeOf.size(currentTag)+
		SizeOf.size(startTime)+
		SizeOf.size(isCFCRequest)+
		SizeOf.size(conns)+
		SizeOf.size(serverPassword)+
		SizeOf.size(ormSession);
	}
	
	

	/** 
	 * default Constructor
	 * @param factoryImpl 
	 * @param scopeContext
	 * @param config Configuration of the Cold Fusion Container
	 * @param compiler CFML Compiler
	 * @param queryCache Query Cache Object
	 * @param id identity of the pageContext
	 */
	public PageContextImpl(ScopeContext scopeContext, ConfigWebImpl config, QueryCache queryCache,int id,HttpServlet servlet) {
		// must be first because is used after
		tagHandlerPool=config.getTagHandlerPool();
        this.servlet=servlet;
		this.id=id;
		//this.factory=factory;
		
        bodyContentStack=new BodyContentStack();
        devNull=bodyContentStack.getDevNullBodyContent();
        
        this.config=config;
        manager=new DatasourceManagerImpl(config);
        
	    this.scopeContext=scopeContext;
        undefined=
        	new UndefinedImpl(this,config.getScopeCascadingType());
        
        
		//this.compiler=compiler;
        //tagHandlerPool=config.getTagHandlerPool();
		this.queryCache=queryCache;
		server=ScopeContext.getServerScope(this);
		
		defaultApplicationContext=new ClassicApplicationContext(config,"",true);
		
	}

	/**
	 * @see javax.servlet.jsp.PageContext#initialize(javax.servlet.Servlet, javax.servlet.ServletRequest, javax.servlet.ServletResponse, java.lang.String, boolean, int, boolean)
	 */
	public void initialize(
			Servlet servlet, 
			ServletRequest req, 
			ServletResponse rsp, 
			String errorPageURL, 
			boolean needsSession, 
			int bufferSize, 
			boolean autoFlush) throws IOException, IllegalStateException, IllegalArgumentException {
	   initialize(
			   (HttpServlet)servlet,
			   (HttpServletRequest)req,
			   (HttpServletResponse)rsp,
			   errorPageURL,
			   needsSession,
			   bufferSize,
			   autoFlush,false);
	}
	
	/**
	 * initialize a existing page context
	 * @param servlet
	 * @param req
	 * @param rsp
	 * @param errorPageURL
	 * @param needsSession
	 * @param bufferSize
	 * @param autoFlush
	 */
	public PageContextImpl initialize(
			 HttpServlet servlet, 
			 HttpServletRequest req, 
			 HttpServletResponse rsp, 
			 String errorPageURL, 
			 boolean needsSession, 
			 int bufferSize, 
			 boolean autoFlush,
			 boolean isChild) {
		requestId=counter++;
		rsp.setContentType("text/html; charset=UTF-8");
		
		this.isChild=isChild;
		
        //rsp.setHeader("Connection", "close");
        applicationContext=defaultApplicationContext;
        
        startTime=System.currentTimeMillis();
        thread=Thread.currentThread();
        
        isCFCRequest = StringUtil.endsWithIgnoreCase(req.getServletPath(),"."+config.getCFCExtension());
        
        this.req=new HTTPServletRequestWrap(req);
        this.rsp=rsp;
        this.servlet=servlet;

         // Writers
         bodyContentStack.init(req,rsp,config.isSuppressWhitespace(),config.closeConnection(),config.isShowVersion(),config.contentLength(),config.allowCompression());
		 writer=bodyContentStack.getWriter();
         forceWriter=writer;
         
		 // Scopes
         server=ScopeContext.getServerScope(this);
         if(hasFamily) {
        	 variablesRoot=new VariablesImpl();
        	 variables=variablesRoot;
        	 request=new RequestImpl();
        	 _url=new URLImpl();
        	 _form=new FormImpl();
        	 urlForm=new UrlFormImpl(_form,_url);
        	 undefined=
             	new UndefinedImpl(this,config.getScopeCascadingType());
        	 
        	 hasFamily=false;
         }
         else if(variables==null) {
        	 variablesRoot=new VariablesImpl();
        	 variables=variablesRoot;
         }
         request.initialize(this);
         
		 if(config.mergeFormAndURL()) {
			 url=urlForm;
			 form=urlForm;
		 }
		 else {
			 url=_url;
			 form=_form;
		 }
         //url.initialize(this);
		 //form.initialize(this);
		 //undefined.initialize(this);
		 
         
        psq=config.getPSQL();
		
		fdEnabled=!config.getCFMLEngineImpl().allowRequestTimeout();
		
		if(config.getExecutionLogEnabled())
			this.execLog=config.getExecutionLogFactory().getInstance(this);
		if(config.debug())
			debugger.init(config);
			
        return this;
	 }
	
	/**
	 * @see javax.servlet.jsp.PageContext#release()
	 */
	public void release() {
		
		if(config.debug()) {
    		if(!gatewayContext)config.getDebuggerPool().store(this, debugger);
    		debugger.reset();
    	}
	
		this.serverPassword=null;

		boolean isChild=parent!=null;
		parent=null;
		// Attention have to be before close
		if(client!=null){
        	client.touchAfterRequest(this);
        	client=null;
        }
		if(session!=null){
        	session.touchAfterRequest(this);
        	session=null;
        }
		
		// ORM
        if(ormSession!=null){
        	// flush orm session
        	try {
				ORMEngine engine=ormSession.getEngine();
        		ORMConfiguration config=engine.getConfiguration(this);
        		if(config==null || (config.flushAtRequestEnd() && config.autoManageSession())){
					ormSession.flush(this);
					//ormSession.close(this);
					//print.err("2orm flush:"+Thread.currentThread().getId());
				}
				ormSession.close(this);
			} 
        	catch (Throwable t) {
        		//print.printST(t);
        	}
        	
			
        	// release connection
			DatasourceConnectionPool pool = this.config.getDatasourceConnectionPool();
			DatasourceConnection dc=ormSession.getDatasourceConnection();
			if(dc!=null)pool.releaseDatasourceConnection(dc);
	       
        	ormSession=null;
        }
        
		
		close();
        thread=null;
        base=null;
        //RequestImpl r = request;
        
        // Scopes
        if(hasFamily) {
        	if(!isChild){
        		req.disconnect();
        	}
        	
        	request=null;
        	_url=null;
        	_form=null;
            urlForm=null;
            undefined=null;
            variables=null;
            variablesRoot=null;
            if(threads!=null && threads.size()>0) threads.clear();
            
        }
        else {
            if(variables.isBind()) {
            	variables=null;
            	variablesRoot=null;
            }
            else {
            	variables=variablesRoot;
            	variables.release();
            }
            undefined.release();
            urlForm.release();
        	request.release();
        }
        cgi.release();
        argument.release();
        local=localUnsupportedScope;
        
        cookie.release();
		//if(cluster!=null)cluster.release();
        
        //client=null;
        //session=null;
        
		
        
        
		application=null;// not needed at the moment -> application.releaseAfterRequest();
		applicationContext=null;
		
		// Properties
        requestTimeout=-1;
        outputState=0;
        cfid=null;
        cftoken=null;
        locale=null;
        timeZone=null;
        url=null;
        form=null;
        
        
        // Pools
        errorPagePool.clear();

        // transaction connection
        if(!conns.isEmpty()){
        	java.util.Iterator<Entry<String, DatasourceConnection>> it = conns.entrySet().iterator();
        	DatasourceConnectionPool pool = config.getDatasourceConnectionPool();
        	while(it.hasNext())	{
        		pool.releaseDatasourceConnection((it.next().getValue()));
        	}
        	conns.clear();
        }
        
        
        
        pathList.clear();
        includePathList.clear();
		executionTime=0;
		
		bodyContentStack.release();
		

        
		//activeComponent=null;
		remoteUser=null;
		exception=null;
		ftpPool.clear();
        parentTag=null;
        currentTag=null;
        
        // Req/Rsp
        //if(req!=null)
        	req.clear();
        req=null;
        rsp=null;
        servlet=null;

        // Writer
        writer=null;
        forceWriter=null;
        if(pagesUsed.size()>0)pagesUsed.clear();
        
        activeComponent=null;
        activeUDF=null;
        
        if(config.getExecutionLogEnabled()){
        	execLog.release();
			execLog=null;
        }

    	gatewayContext=false;
    	
    	manager.release();
	}

	
	
	/* *
	 * called when parent thread end
	 * /
	public void unlink() {
		
		//print.o(request.keysAsString());
		// unlink request scope
		HttpServletRequest org = req.getOriginalRequest();
		if(org instanceof HttpServletRequestDummy) {
			((HttpServletRequestDummy)org).setAttributes(new StructImpl());
		}
		
		RequestImpl r = new RequestImpl();
		r.initialize(this);
		StructImpl.copy(request,r,false);
		//print.o(request.keysAsString());
		//print.o(r.keysAsString());
		this.request=r;
	}*/

    /**
     * @see railo.runtime.PageContext#write(java.lang.String)
     */
    public void write(String str) throws IOException {
    	writer.write(str);
	}

    /**
     * @see railo.runtime.PageContext#forceWrite(java.lang.String)
     */
    public void forceWrite(String str) throws IOException {
        forceWriter.write(str);
    }
	
    /**
     * @see railo.runtime.PageContext#writePSQ(java.lang.Object)
     */
    public void writePSQ(Object o) throws IOException, PageException {
    	if(o instanceof Date || Decision.isDate(o, false)) {
			writer.write(Caster.toString(o));
		}
		else {
            writer.write(psq?Caster.toString(o):StringUtil.replace(Caster.toString(o),"'","''",false));
		}
	} 
	
    /**
     * @see railo.runtime.PageContext#flush()
     */
    public void flush() {
		try {
			getOut().flush();
		} catch (IOException e) {}
	}
	
    /**
     * @see railo.runtime.PageContext#close()
     */
    public void close() {
		IOUtil.closeEL(getOut());
	}
	
    /**
     * @see railo.runtime.PageContext#getRelativePageSource(java.lang.String)
     */
    public PageSource getRelativePageSource(String realPath) {
    	if(StringUtil.startsWith(realPath,'/')) return getPageSource(realPath);
    	if(pathList.size()==0) return null;
		return pathList.getLast().getRealPage(realPath);
	}
    
    public PageSource getPageSource(String realPath) {
    	return config.getPageSource(this,applicationContext.getMappings(),realPath,false,useSpecialMappings,true);
	}

    public boolean useSpecialMappings(boolean useTagMappings) {
		boolean b=this.useSpecialMappings;
		this.useSpecialMappings=useTagMappings;
		return b;
	}
    public boolean useSpecialMappings() {
		return useSpecialMappings;
	}
    

    public Resource getPhysical(String realPath, boolean alsoDefaultMapping){
    	return config.getPhysical(applicationContext.getMappings(),realPath, alsoDefaultMapping);
    }
    

	public PageSource toPageSource(Resource res, PageSource defaultValue){
		return config.toPageSource(applicationContext.getMappings(),res, defaultValue);
	}

	/**
	 * @see railo.runtime.PageContext#doInclude(java.lang.String)
	 */
	public void doInclude(String realPath) throws PageException {
		doInclude(getRelativePageSource(realPath));
	}

	/**
	 * @see railo.runtime.PageContext#doInclude(railo.runtime.PageSource)
	 */
	public void doInclude(PageSource source) throws PageException {
    	// debug
		if(!gatewayContext && config.debug()) {
			DebugEntry debugEntry=debugger.getEntry(this,source);
			int currTime=executionTime;
            long exeTime=0;
            long time=System.nanoTime();
            
            Page currentPage = ((PageSourceImpl)source).loadPage(this);
            try {
                addPageSource(source,true);
                debugEntry.updateFileLoadTime((int)(System.nanoTime()-time));
                exeTime=System.nanoTime();
                currentPage.call(this);
			}
			catch(Throwable t){
				PageException pe = Caster.toPageException(t);
				if(Abort.isAbort(pe)) {
                    if(Abort.isAbort(pe,Abort.SCOPE_REQUEST))throw pe;
                }
                else {
                	if(fdEnabled){
                		FDSignal.signal(pe, false);
                	}
                	pe.addContext(currentPage.getPageSource(),-187,-187, null);// TODO was soll das 187
                	throw pe;
                }
			}
			finally {
				int diff= ((int)(System.nanoTime()-exeTime)-(executionTime-currTime));
			    executionTime+=(int)(System.nanoTime()-time);
				debugEntry.updateExeTime(diff);
				removeLastPageSource(true);
			}	
		}
	// no debug
		else {
            Page currentPage = ((PageSourceImpl)source).loadPage(this);
		    try {
				addPageSource(source,true);
                currentPage.call(this);
			}
			catch(Throwable t){
				PageException pe = Caster.toPageException(t);
				if(Abort.isAbort(pe)) {
					if(Abort.isAbort(pe,Abort.SCOPE_REQUEST))throw pe;
                }
                else    {
                	pe.addContext(currentPage.getPageSource(),-187,-187, null);
                	throw pe;
                }
			}
			finally {
				removeLastPageSource(true);
			}	
		}
	}

	/**
     * @see railo.runtime.PageContext#getTemplatePath()
     */
    public Array getTemplatePath() throws ExpressionException {
        int len=includePathList.size();
        SVArray sva = new SVArray();
        PageSource ps;
        for(int i=0;i<len;i++) {
        	ps=includePathList.get(i);
        	if(i==0) {
        		if(!ps.equals(getBasePageSource()))
        			sva.append(ResourceUtil.getResource(this,getBasePageSource()).getAbsolutePath());
        	}
        	sva.append(ResourceUtil.getResource(this, ps).getAbsolutePath());
        }
        //sva.setPosition(sva.size());
        return sva;
    }
  
    public List<PageSource> getPageSourceList() {
        return (List<PageSource>) pathList.clone();
        
    }
    
    
    
    protected PageSource getPageSource(int index) {
        return includePathList.get(index-1);
    }
    public synchronized void copyStateTo(PageContextImpl other) {
    	
    	
    	
    	
    	// private Debugger debugger=new DebuggerImpl();
    	other.requestTimeout=requestTimeout;
    	other.locale=locale;
    	other.timeZone=timeZone;
    	other.fdEnabled=fdEnabled;
    	other.useSpecialMappings=useSpecialMappings;
    	other.serverPassword=serverPassword;
    	
    	
    	hasFamily=true;
    	other.hasFamily=true;
    	other.parent=this;
		other.applicationContext=applicationContext;
		other.thread=Thread.currentThread();
		other.startTime=System.currentTimeMillis();
		other.isCFCRequest = isCFCRequest;
        
        
        
    	// path
    	other.base=base;
    	java.util.Iterator<PageSource> it = includePathList.iterator();
    	while(it.hasNext()) {
    		other.includePathList.add(it.next());
    	}
    	it = pathList.iterator();
    	while(it.hasNext()) {
    		other.pathList.add(it.next());
    	}
    	
    	
    	// scopes
    	//other.req.setAttributes(request);
    	/*HttpServletRequest org = other.req.getOriginalRequest();
    	if(org instanceof HttpServletRequestDummy) {
    		((HttpServletRequestDummy)org).setAttributes(request);
    	}*/
    	other.req=req;
    	other.request=request;
    	other.form=form;
    	other.url=url;
    	other.urlForm=urlForm;
    	other._url=_url;
    	other._form=_form;
    	other.variables=variables;
    	other.undefined=new UndefinedImpl(other,(short)other.undefined.getType());
    	
    	// writers
    	other.bodyContentStack.init(other.req,other.rsp,other.config.isSuppressWhitespace(),other.config.closeConnection(),
    			other.config.isShowVersion(),config.contentLength(),config.allowCompression());
    	other.writer=other.bodyContentStack.getWriter();
    	other.forceWriter=other.writer;
        
    	other.psq=psq;
    	other.gatewayContext=gatewayContext;
        
        // thread
        if(threads!=null){
        	synchronized (threads) {
				
	        	java.util.Iterator it2 = threads.entrySet().iterator();
	        	Map.Entry entry;
	        	while(it2.hasNext()) {
	        		entry=(Entry) it2.next();
	        		other.setThreadScope((String)entry.getKey(), (Threads)entry.getValue());
	        	}
			}
        }
        
        
    }
    
    /*public static void setState(PageContextImpl other,ApplicationContext applicationContext, boolean isCFCRequest) {

    	other.hasFamily=true;
    	
		other.applicationContext=applicationContext;
		other.thread=Thread.currentThread();
		other.startTime=System.currentTimeMillis();
        other.isCFCRequest = isCFCRequest;
        
    	// path
    	other.base=base;
    	java.util.Iterator it = includePathList.iterator();
    	while(it.hasNext()) {
    		other.includePathList.add(it.next());
    	}
    	
    	// scopes
    	other.request=request;
    	other.form=form;
    	other.url=url;
    	other.urlForm=urlForm;
    	other._url=_url;
    	other._form=_form;
    	other.variables=variables;
    	other.undefined=new UndefinedImpl(other,(short)other.undefined.getType());
    	
    	// writers
    	other.bodyContentStack.init(other.rsp,other.config.isSuppressWhitespace(),other.config.closeConnection(),other.config.isShowVersion());
    	other.writer=other.bodyContentStack.getWriter();
    	other.forceWriter=other.writer;
        
        other.psq=psq;
	}*/
    
    public int getCurrentLevel() {
        return includePathList.size()+1;
    }
    
    /**
     * @return the current template SourceFile
     */
    public PageSource getCurrentPageSource() {
    	return pathList.getLast();
    }
    
    /**
     * @return the current template SourceFile
     */
    public PageSource getCurrentTemplatePageSource() {
        return includePathList.getLast();
    }
	
    /**
     * @return base template file
     */
    public PageSource getBasePageSource() {
		return base;
	}
	
    /**
     * @see railo.runtime.PageContext#getRootTemplateDirectory()
     */
    public Resource getRootTemplateDirectory() {
		return config.getResource(servlet.getServletContext().getRealPath("/"));
		//new File(servlet.getServletContext().getRealPath("/"));
	}

    /**
     * @see PageContext#scope(int)
     * @deprecated use instead <code>VariableInterpreter.scope(...)</code>
     */
    public Scope scope(int type) throws PageException {
        switch(type) {
            case Scope.SCOPE_UNDEFINED:     return undefinedScope();
            case Scope.SCOPE_URL:           return urlScope();
            case Scope.SCOPE_FORM:          return formScope();
            case Scope.SCOPE_VARIABLES:     return variablesScope();
            case Scope.SCOPE_REQUEST:       return requestScope();
            case Scope.SCOPE_CGI:           return cgiScope();
            case Scope.SCOPE_APPLICATION:   return applicationScope();
            case Scope.SCOPE_ARGUMENTS:     return argumentsScope();
            case Scope.SCOPE_SESSION:       return sessionScope();
            case Scope.SCOPE_SERVER:        return serverScope();
            case Scope.SCOPE_COOKIE:        return cookieScope();
            case Scope.SCOPE_CLIENT:        return clientScope();
            case Scope.SCOPE_LOCAL:         
            case ScopeSupport.SCOPE_VAR:         	return localScope();
            case Scope.SCOPE_CLUSTER:return clusterScope();
        }
        return variables;
    }
    
    public Scope scope(String strScope,Scope defaultValue) throws PageException {
    	if(strScope==null)return defaultValue;
    	strScope=strScope.toLowerCase().trim(); 
    	if("variables".equals(strScope))	return variablesScope(); 
    	if("url".equals(strScope))			return urlScope();
    	if("form".equals(strScope))			return formScope();
    	if("request".equals(strScope))		return requestScope();
    	if("cgi".equals(strScope))			return cgiScope();
    	if("application".equals(strScope))	return applicationScope();
    	if("arguments".equals(strScope))	return argumentsScope();
    	if("session".equals(strScope))		return sessionScope();
    	if("server".equals(strScope))		return serverScope();
    	if("cookie".equals(strScope))		return cookieScope();
    	if("client".equals(strScope))		return clientScope();
    	if("local".equals(strScope))		return localScope();
    	if("cluster".equals(strScope))		return clusterScope();
    	
	    return defaultValue;
    }
    
    /**
     * @see PageContext#undefinedScope()
     */
    public Undefined undefinedScope() {
        if(!undefined.isInitalized()) undefined.initialize(this);
        return undefined;
    }
    
    /**
     * @return undefined scope, undefined scope is a placeholder for the scopecascading
     */
    public Undefined us() {
    	if(!undefined.isInitalized()) undefined.initialize(this);
    	return undefined;
    }
    
    /**
     * @see PageContext#variablesScope()
     */
    public Variables variablesScope() { return variables; }
	
    /**
     * @see PageContext#urlScope()
     */
    public URL urlScope() { 
    	if(!url.isInitalized())url.initialize(this);
		return url;
    }
	
    /**
     * @see PageContext#formScope()
     */
    public Form formScope() {
    	if(!form.isInitalized())form.initialize(this);
		return form;
    }
	
    /**
     * @see railo.runtime.PageContext#urlFormScope()
     */
    public URLForm urlFormScope() {
    	if(!urlForm.isInitalized())urlForm.initialize(this);
		return urlForm;
    }

    /**
     * @see PageContext#requestScope()
     */
    public Request requestScope() { return request; }
	
    /**
     * @see PageContext#cgiScope()
     */
    public CGI cgiScope() {
		if(!cgi.isInitalized())cgi.initialize(this);
		return cgi;
	}
	
    /**
     * @see PageContext#applicationScope()
     */
    public Application applicationScope() throws PageException {
		if(application==null) {
			if(!applicationContext.hasName())
				throw new ExpressionException("there is no application context defined for this application","you can define a application context with the tag "+railo.runtime.config.Constants.CFAPP_NAME+"/"+railo.runtime.config.Constants.APP_CFC);
			application=scopeContext.getApplicationScope(this,DUMMY_BOOL);
		}
		return application; 
	}

    /**
     * @see PageContext#argumentsScope()
     */
    public Argument argumentsScope() { return argument; }

    
    /**
     * @see PageContext#argumentsScope(boolean)
     */
    public Argument argumentsScope(boolean bind) { 
            //Argument a=argumentsScope(); 
            if(bind)argument.setBind(true); 
            return argument; 
    } 
    
    /**
     * @see railo.runtime.PageContext#localScope()
     */
    public Local localScope() { 
    	//if(local==localUnsupportedScope) 
    	//	throw new PageRuntimeException(new ExpressionException("Unsupported Context for Local Scope"));
    	return local;
    }
    
    /**
     * @see railo.runtime.PageContext#localScope(boolean)
     */
    public Local localScope(boolean bind) { 
    	if(bind)local.setBind(true); 
    	//if(local==localUnsupportedScope) 
    	//	throw new PageRuntimeException(new ExpressionException("Unsupported Context for Local Scope"));
    	return local; 
    }

    
    public Object localGet() throws PageException { 
    	return localGet(false);
    }
    
    public Object localGet(boolean bind) throws PageException { 
    	// inside a local supported block
    	if(undefined.getCheckArguments()){
    		return localScope(bind);
    	}
    	return undefinedScope().get(KeyImpl.LOCAL);
    }

    public Object localTouch() throws PageException { 
    	return localTouch(false);
    }
    
    public Object localTouch(boolean bind) throws PageException { 
    	// inside a local supported block
    	if(undefined.getCheckArguments()){
    		return localScope(bind);
    	}
    	return touch(undefinedScope(), KeyImpl.LOCAL);
    	//return undefinedScope().get(LOCAL);
    }
    
	
    /**
     * @param local sets the current local scope
     * @param argument sets the current argument scope
     */
    public void setFunctionScopes(Local local,Argument argument) {
    	this.argument=argument;
		this.local=local;
		undefined.setFunctionScopes(local,argument);
	}
	
    /**
     * @see PageContext#sessionScope()
     */
    public Session sessionScope() throws PageException {
		return sessionScope(true);
	}
    public Session sessionScope(boolean checkExpires) throws PageException {
		if(session==null)	{
			if(!applicationContext.hasName())
				throw new ExpressionException("there is no session context defined for this application","you can define a session context with the tag "+Constants.CFAPP_NAME+"/"+Constants.APP_CFC);
			if(!applicationContext.isSetSessionManagement())
				throw new ExpressionException("session scope is not enabled","you can enable session scope with tag "+Constants.CFAPP_NAME+"/"+Constants.APP_CFC);
			session=scopeContext.getSessionScope(this,DUMMY_BOOL);
		}
		return session;
	}

    /**
     * @see PageContext#serverScope()
     */
    public Server serverScope() { 
		//if(!server.isInitalized()) server.initialize(this);
		return server;
	}
    
    /**
     * @see railo.runtime.PageContext#clusterScope()
     */
    public Cluster clusterScope() throws PageException {
    	return clusterScope(true);
	}
    
    public Cluster clusterScope(boolean create) throws PageException { 
    	if(cluster==null && create) {
    		cluster=ScopeContext.getClusterScope(config.getConfigServerImpl(),create);
    		//cluster.initialize(this);
    	}
    	//else if(!cluster.isInitalized()) cluster.initialize(this);
		return cluster;
	}

    /**
     * @see PageContext#cookieScope()
     */
    public Cookie cookieScope() { 
        if(!cookie.isInitalized()) cookie.initialize(this);
        return cookie;
    }
	
    /**
     * @see PageContext#clientScope()
     */
    public Client clientScope() throws PageException { 
		if(client==null) {
			if(!applicationContext.hasName())
				throw new ExpressionException("there is no client context defined for this application",
						"you can define a client context with the tag "+Constants.CFAPP_NAME+"/"+Constants.APP_CFC);
			if(!applicationContext.isSetClientManagement())
				throw new ExpressionException("client scope is not enabled",
						"you can enable client scope with tag "+Constants.CFAPP_NAME+"/"+Constants.APP_CFC);
			
			client= scopeContext.getClientScope(this);
		}
		return client;
	}
    
    public Client clientScopeEL() { 
		if(client==null) {
			if(!applicationContext.hasName()) 				return null;
			if(!applicationContext.isSetClientManagement())	return null;
			client= scopeContext.getClientScopeEL(this);
		}
		return client;
	}
	
    /**
     * @see PageContext#set(java.lang.Object, java.lang.String, java.lang.Object)
     */
    public Object set(Object coll, String key, Object value) throws PageException {
	    return variableUtil.set(this,coll,key,value);
	}

	public Object set(Object coll, Collection.Key key, Object value) throws PageException {
		return variableUtil.set(this,coll,key,value);
	}
	
    /**
     * @see PageContext#touch(java.lang.Object, java.lang.String)
     */
    public Object touch(Object coll, String key) throws PageException {
	    Object o=getCollection(coll,key,null);
	    if(o!=null) return o;
	    return set(coll,key,new StructImpl());
	} 

	/**
	 *
	 * @see railo.runtime.PageContext#touch(java.lang.Object, railo.runtime.type.Collection.Key)
	 */
	public Object touch(Object coll, Collection.Key key) throws PageException {
		Object o=getCollection(coll,key,null);
	    if(o!=null) return o;
	    return set(coll,key,new StructImpl());
	}
    
    /*private Object _touch(Scope scope, String key) throws PageException {
	    Object o=scope.get(key,null);
	    if(o!=null) return o;
	    return scope.set(key, new StructImpl());
	}*/	
    

    
    

    /**
     * @see PageContext#getCollection(java.lang.Object, java.lang.String)
     */
    public Object getCollection(Object coll, String key) throws PageException {
    	return variableUtil.getCollection(this,coll,key);
	}

	/**
	 * @see railo.runtime.PageContext#getCollection(java.lang.Object, railo.runtime.type.Collection.Key)
	 */
	public Object getCollection(Object coll, Collection.Key key) throws PageException {
		return variableUtil.getCollection(this,coll,key);
	}
	
    /**
     *
     * @see railo.runtime.PageContext#getCollection(java.lang.Object, java.lang.String, java.lang.Object)
     */
    public Object getCollection(Object coll, String key, Object defaultValue) {
		return variableUtil.getCollection(this,coll,key,defaultValue);
	}

	/**
	 * @see railo.runtime.PageContext#getCollection(java.lang.Object, railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object getCollection(Object coll, Collection.Key key, Object defaultValue) {
		return variableUtil.getCollection(this,coll,key,defaultValue);
	}
	
    /**
     * @see PageContext#get(java.lang.Object, java.lang.String)
     */
    public Object get(Object coll, String key) throws PageException {
		return variableUtil.get(this,coll,key);
	}

	/**
	 *
	 * @see railo.runtime.PageContext#get(java.lang.Object, railo.runtime.type.Collection.Key)
	 */
	public Object get(Object coll, Collection.Key key) throws PageException {
		return variableUtil.get(this,coll,key);
	}
	
    /**
     *
     * @see railo.runtime.PageContext#getReference(java.lang.Object, java.lang.String)
     */
    public Reference getReference(Object coll, String key) throws PageException {
		return new VariableReference(coll,key);
	}

	public Reference getReference(Object coll, Collection.Key key) throws PageException {
		return new VariableReference(coll,key);
	}

    /**
     * @see railo.runtime.PageContext#get(java.lang.Object, java.lang.String, java.lang.Object)
     */
    public Object get(Object coll, String key, Object defaultValue) {
        return variableUtil.get(this,coll,key, defaultValue);
    }

	/**
	 * @see railo.runtime.PageContext#get(java.lang.Object, railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object get(Object coll, Collection.Key key, Object defaultValue) {
		return variableUtil.get(this,coll,key, defaultValue);
	}
	
    /**
     * @see PageContext#setVariable(java.lang.String, java.lang.Object)
     */
    public Object setVariable(String var, Object value) throws PageException {
	    //return new CFMLExprInterpreter().interpretReference(this,new ParserString(var)).set(value);
	    return VariableInterpreter.setVariable(this,var,value);
	}
    
    /**
     * @see PageContext#getVariable(java.lang.String)
     */
    public Object getVariable(String var) throws PageException {
		return VariableInterpreter.getVariable(this,var);
	}
    

    public void param(String type, String name, Object defaultValue,String regex) throws PageException {
    	param(type, name, defaultValue,Double.NaN,Double.NaN,regex);
    }
	public void param(String type, String name, Object defaultValue,double min, double max) throws PageException {
    	param(type, name, defaultValue,min,max,null);
    }

    public void param(String type, String name, Object defaultValue) throws PageException {
    	param(type, name, defaultValue,Double.NaN,Double.NaN,null);
    }
	
    private void param(String type, String name, Object defaultValue, double min,double max, String strPattern) throws PageException {
		
    	
    	// check attributes type
    	if(type==null)type="any";
		else type=type.trim().toLowerCase();

    	// check attributes name
    	if(StringUtil.isEmpty(name))
			throw new ExpressionException("The attribute name is required");
    	
    	Object value=null;
		boolean isNew=false;
		// get value
		try {
			value=getVariable(name);
		} 
		catch (PageException e) {
			if(defaultValue==null)
				throw new ExpressionException("The required parameter ["+name+"] was not provided.");
			value=defaultValue;
			isNew=true;
		}
		// cast and set value
		if(!"any".equals(type)) {
			// range
			if("range".equals(type)) {
				double number = Caster.toDoubleValue(value);
				if(!Decision.isValid(min)) throw new ExpressionException("Missing attribute [min]");
				if(!Decision.isValid(max)) throw new ExpressionException("Missing attribute [max]");
				if(number<min || number>max)
					throw new ExpressionException("The number ["+Caster.toString(number)+"] is out of range [min:"+Caster.toString(min)+";max:"+Caster.toString(max)+"]");
				setVariable(name,Caster.toDouble(number));
			}
			// regex
			else if("regex".equals(type) || "regular_expression".equals(type)) {
				String str=Caster.toString(value);
				
				if(strPattern==null) throw new ExpressionException("Missing attribute [pattern]");
				
				try {
					Pattern pattern = new Perl5Compiler().compile(strPattern, Perl5Compiler.DEFAULT_MASK);
			        PatternMatcherInput input = new PatternMatcherInput(str);
			        if( !new Perl5Matcher().matches(input, pattern))
			        	throw new ExpressionException("The value ["+str+"] doesn't match the provided pattern ["+strPattern+"]");
			        
				} catch (MalformedPatternException e) {
					throw new ExpressionException("The provided pattern ["+strPattern+"] is invalid",e.getMessage());
				}
				setVariable(name,str);
			}
			else {
				if(!Decision.isCastableTo(type,value,true)) throw new CasterException(value,type);	
				setVariable(name,value);
				//REALCAST setVariable(name,Caster.castTo(this,type,value,true));
			}
		}
	    else if(isNew) setVariable(name,value);
	}
    
    
    
    
	
    /**
     * @see PageContext#removeVariable(java.lang.String)
     */
    public Object removeVariable(String var) throws PageException {
		return VariableInterpreter.removeVariable(this,var);
	}

    /**
     * 
     * a variable reference, references to variable, to modifed it, with global effect.
     * @param var variable name to get
     * @return return a variable reference by string syntax ("scopename.key.key" -> "url.name")
     * @throws PageException
     */
    public VariableReference getVariableReference(String var) throws PageException { 
	    return VariableInterpreter.getVariableReference(this,var);
	} 
	
    /**
     * @see railo.runtime.PageContext#getFunction(java.lang.Object, java.lang.String, java.lang.Object[])
     */
    public Object getFunction(Object coll, String key, Object[] args) throws PageException {
        return variableUtil.callFunctionWithoutNamedValues(this,coll,key,args);
	}

	/**
	 * @see railo.runtime.PageContext#getFunction(java.lang.Object, railo.runtime.type.Collection.Key, java.lang.Object[])
	 */
	public Object getFunction(Object coll, Key key, Object[] args) throws PageException {
		return variableUtil.callFunctionWithoutNamedValues(this,coll,key,args);
	}
	
    /**
     * @see railo.runtime.PageContext#getFunctionWithNamedValues(java.lang.Object, java.lang.String, java.lang.Object[])
     */
    public Object getFunctionWithNamedValues(Object coll, String key, Object[] args) throws PageException {
		return variableUtil.callFunctionWithNamedValues(this,coll,key,args);
	}

	/**
	 *
	 * @see railo.runtime.PageContext#getFunctionWithNamedValues(java.lang.Object, railo.runtime.type.Collection.Key, java.lang.Object[])
	 */
	public Object getFunctionWithNamedValues(Object coll, Key key, Object[] args) throws PageException {
		return variableUtil.callFunctionWithNamedValues(this,coll,key,args);
	}

    /**
     * @see railo.runtime.PageContext#getConfig()
     */
    public ConfigWeb getConfig() {
        return config;
    }
    
    /**
     * @see railo.runtime.PageContext#getIterator(java.lang.String)
     */
    public Iterator getIterator(String key) throws PageException {
		Object o=VariableInterpreter.getVariable(this,key);
		if(o instanceof Iterator) return (Iterator) o;
		throw new ExpressionException("["+key+"] is not a iterator object");
	}

    /**
     * @see PageContext#getQuery(java.lang.String)
     */
    public Query getQuery(String key) throws PageException {
		Object o=VariableInterpreter.getVariable(this,key);
		if(o instanceof Query) return (Query) o;
		throw new CasterException(o,Query.class);///("["+key+"] is not a query object, object is from type ");
	}

	/**
	 * @see javax.servlet.jsp.PageContext#setAttribute(java.lang.String, java.lang.Object)
	 */
	public void setAttribute(String name, Object value) {
	    try {
            if(value==null)removeVariable(name);
            else setVariable(name,value);
        } catch (PageException e) {}
	}

	/**
	 * @see javax.servlet.jsp.PageContext#setAttribute(java.lang.String, java.lang.Object, int)
	 */
	public void setAttribute(String name, Object value, int scope) {
		switch(scope){
		case javax.servlet.jsp.PageContext.APPLICATION_SCOPE:
			if(value==null) getServletContext().removeAttribute(name);
			else getServletContext().setAttribute(name, value);
		break;
		case javax.servlet.jsp.PageContext.PAGE_SCOPE:
			setAttribute(name, value);
		break;
		case javax.servlet.jsp.PageContext.REQUEST_SCOPE:
			if(value==null) req.removeAttribute(name);
			else setAttribute(name, value);
		break;
		case javax.servlet.jsp.PageContext.SESSION_SCOPE:
			HttpSession s = req.getSession(true);
			if(value==null)s.removeAttribute(name);
			else s.setAttribute(name, value);
		break;
		}	
	}

	/**
	 * @see javax.servlet.jsp.PageContext#getAttribute(java.lang.String)
	 */
	public Object getAttribute(String name) {
		try {
			return getVariable(name);
		} catch (PageException e) {
			return null;
		}
	}

	/**
	 * @see javax.servlet.jsp.PageContext#getAttribute(java.lang.String, int)
	 */
	public Object getAttribute(String name, int scope) {
		switch(scope){
		case javax.servlet.jsp.PageContext.APPLICATION_SCOPE:
			return getServletContext().getAttribute(name);
		case javax.servlet.jsp.PageContext.PAGE_SCOPE:
			return getAttribute(name);
		case javax.servlet.jsp.PageContext.REQUEST_SCOPE:
			return req.getAttribute(name);
		case javax.servlet.jsp.PageContext.SESSION_SCOPE:
			HttpSession s = req.getSession();
			if(s!=null)return s.getAttribute(name);
		break;
		}	
		return null;
	}

	/**
	 * @see javax.servlet.jsp.PageContext#findAttribute(java.lang.String)
	 */
	public Object findAttribute(String name) {
		// page
		Object value=getAttribute(name);
		if(value!=null) return value;
		// request
		value=req.getAttribute(name);
		if(value!=null) return value;
		// session
		HttpSession s = req.getSession();
		value=s!=null?s.getAttribute(name):null;
		if(value!=null) return value;
		// application
		value=getServletContext().getAttribute(name);
		if(value!=null) return value;
		
		
		return null;
	}

	/**
	 * @see javax.servlet.jsp.PageContext#removeAttribute(java.lang.String)
	 */
	public void removeAttribute(String name) {
		setAttribute(name, null);
	}

	/**
	 * @see javax.servlet.jsp.PageContext#removeAttribute(java.lang.String, int)
	 */
	public void removeAttribute(String name, int scope) {
		setAttribute(name, null,scope);
	}

	/**
	 * @see javax.servlet.jsp.PageContext#getAttributesScope(java.lang.String)
	 */
	public int getAttributesScope(String name) {
		// page
		if(getAttribute(name)!=null) return PageContext.PAGE_SCOPE;
        // request
        if(req.getAttribute(name) != null) return PageContext.REQUEST_SCOPE;
        // session
        HttpSession s = req.getSession();
        if(s!=null && s.getAttribute(name) != null) return PageContext.SESSION_SCOPE;
        // application
        if(getServletContext().getAttribute(name)!=null) return PageContext.APPLICATION_SCOPE;
        
		return 0;
	}

	/**
	 * @see javax.servlet.jsp.PageContext#getAttributeNamesInScope(int)
	 */
	public Enumeration getAttributeNamesInScope(int scope) {
		
        switch(scope){
		case javax.servlet.jsp.PageContext.APPLICATION_SCOPE:
			return getServletContext().getAttributeNames();
		case javax.servlet.jsp.PageContext.PAGE_SCOPE:
			return new KeyIterator(variablesScope().keys());
		case javax.servlet.jsp.PageContext.REQUEST_SCOPE:
			return req.getAttributeNames();
		case javax.servlet.jsp.PageContext.SESSION_SCOPE:
			return req.getSession(true).getAttributeNames();
		}
		return null;
	}

	/**
	 * @see javax.servlet.jsp.PageContext#getOut()
	 */
	public JspWriter getOut() {
		return forceWriter;
	}

	/**
	 * @see javax.servlet.jsp.PageContext#getSession()
	 */
	public HttpSession getSession() {
		return getHttpServletRequest().getSession();
	}

	/**
	 * @see javax.servlet.jsp.PageContext#getPage()
	 */
	public Object getPage() {
		return variablesScope();
	}

	/**
	 * @see javax.servlet.jsp.PageContext#getRequest()
	 */
	public ServletRequest getRequest() {
		return getHttpServletRequest();
	}
	
    /**
     * @see railo.runtime.PageContext#getHttpServletRequest()
     */
    public HttpServletRequest getHttpServletRequest() {
		return req;
	}

	/**
	 * @see javax.servlet.jsp.PageContext#getResponse()
	 */
	public ServletResponse getResponse() {
		return rsp;
	}

    /**
     * @see railo.runtime.PageContext#getHttpServletResponse()
     */
    public HttpServletResponse getHttpServletResponse() {
		return rsp;
	}
    
    public OutputStream getResponseStream() throws IOException {
    	return getRootOut().getResponseStream();
	}

	/**
	 * @see javax.servlet.jsp.PageContext#getException()
	 */
	public Exception getException() {
		// TODO impl
		return exception;
	}

	/**
	 * @see javax.servlet.jsp.PageContext#getServletConfig()
	 */
	public ServletConfig getServletConfig() {
		return config;
	}

	/**
	 * @see javax.servlet.jsp.PageContext#getServletContext()
	 */
	public ServletContext getServletContext() {
		return servlet.getServletContext();
	}

	/*public static void main(String[] args) {
		repl(" susi #error.susi# sorglos","susi", "Susanne");
		repl(" susi #error.Susi# sorglos","susi", "Susanne");
	}*/
	private static String repl(String haystack, String needle, String replacement) {
		//print.o("------------");
		//print.o(haystack);
		//print.o(needle);
		StringBuilder regex=new StringBuilder("#[\\s]*error[\\s]*\\.[\\s]*");
		
		char[] carr = needle.toCharArray();
		for(int i=0;i<carr.length;i++){
			regex.append("[");
			regex.append(Character.toLowerCase(carr[i]));
			regex.append(Character.toUpperCase(carr[i]));
			regex.append("]");
		}
		
		
		regex.append("[\\s]*#");
		//print.o(regex);
		
		
		haystack=haystack.replaceAll(regex.toString(), replacement);
		//print.o(haystack);
		return haystack;
	}
	
	
	/**
	 * @see railo.runtime.PageContext#handlePageException(railo.runtime.exp.PageException)
	 */
	public void handlePageException(PageException pe) {
		if(!Abort.isSilentAbort(pe)) {
			
			String charEnc = rsp.getCharacterEncoding();
	        if(StringUtil.isEmpty(charEnc,true)) {
				rsp.setContentType("text/html");
	        }
	        else {
	        	rsp.setContentType("text/html; charset=" + charEnc);
	        }
			
			int statusCode=getStatusCode(pe);
			
			if(getConfig().getErrorStatusCode())rsp.setStatus(statusCode);
			
			ErrorPage ep=errorPagePool.getErrorPage(pe,ErrorPageImpl.TYPE_EXCEPTION);
			
			ExceptionHandler.printStackTrace(this,pe);
			ExceptionHandler.log(getConfig(),pe);
			// error page exception
			if(ep!=null) {
				try {
					Struct sct=pe.getErrorBlock(this,ep);
					variablesScope().setEL(ERROR,sct);
					variablesScope().setEL(CFERROR,sct);
					
					doInclude(ep.getTemplate());
					return;
				} catch (Throwable t) {
					if(Abort.isSilentAbort(t)) return;
					pe=Caster.toPageException(t);
				}
				
			}
			
			// error page request
			
			ep=errorPagePool.getErrorPage(pe,ErrorPageImpl.TYPE_REQUEST);
			if(ep!=null) {
				PageSource ps = ep.getTemplate();
				if(ps.physcalExists()){
					Resource res = ps.getResource();
					try {
						String content = IOUtil.toString(res, getConfig().getTemplateCharset());
						Struct sct=pe.getErrorBlock(this,ep);
						Key[] keys = sct.keys();
						String v;
						for(int i=0;i<keys.length;i++){
							v=Caster.toString(sct.get(keys[i],null),null);
							if(v!=null)content=repl(content, keys[i].getString(), v);
						}
						
						write(content);
						return;
					} catch (Throwable t) {
						pe=Caster.toPageException(t);
						
					}
					
				}
				else pe=new ApplicationException("The error page template for type request only works if the actual source file also exists . If the exception file is in an Railo archive (.rc/.rcs), you need to use type exception instead.");
			}
			
			
			try {
				if(statusCode!=404)
					forceWrite("<!-- Railo ["+Info.getVersionAsString()+"] Error -->");
				
				String template=getConfig().getErrorTemplate(statusCode);
				if(!StringUtil.isEmpty(template)) {
					try {
						Struct catchBlock=pe.getCatchBlock(this);
						variablesScope().setEL(CFCATCH,catchBlock);
						variablesScope().setEL(CATCH,catchBlock);
						doInclude(getRelativePageSource(template));
					    return;
			        } 
					catch (PageException e) {
						pe=e;
					}
				}
				if(!Abort.isSilentAbort(pe))forceWrite(getConfig().getDefaultDumpWriter().toString(this,pe.toDumpData(this, 9999,DumpUtil.toDumpProperties()),true));
			} 
			catch (Exception e) { 
			}
		}
	}

	private int getStatusCode(PageException pe) {
		int statusCode=500;
		int maxDeepFor404=0;
		if(pe instanceof ModernAppListenerException){
			pe=((ModernAppListenerException)pe).getPageException();
			maxDeepFor404=1;
		}
		else if(pe instanceof PageExceptionBox)
			pe=((PageExceptionBox)pe).getPageException();
		
		if(pe instanceof MissingIncludeException) {
			MissingIncludeException mie=(MissingIncludeException) pe;
			if(mie.getPageDeep()<=maxDeepFor404) statusCode=404;
		}
		
		// TODO Auto-generated method stub
		return statusCode;
	}


	/**
	 * @see javax.servlet.jsp.PageContext#handlePageException(java.lang.Exception)
	 */
	public void handlePageException(Exception e) {
		handlePageException(Caster.toPageException(e));		
	}

	/**
	 * @see javax.servlet.jsp.PageContext#handlePageException(java.lang.Throwable)
	 */
	public void handlePageException(Throwable t) {
        handlePageException(Caster.toPageException(t));
	}

    /**
     * @see PageContext#setHeader(java.lang.String, java.lang.String)
     */
    public void setHeader(String name, String value) {
		rsp.setHeader(name,value);
	}

	/**
	 * @see javax.servlet.jsp.PageContext#pushBody()
	 */
	public BodyContent pushBody() {
        forceWriter=bodyContentStack.push();
        if(enablecfoutputonly>0 && outputState==0) {
            writer=devNull;
        }
        else writer=forceWriter;
        return (BodyContent)forceWriter;
	}

	/**
	 * @see javax.servlet.jsp.PageContext#popBody()
	 */
	public JspWriter popBody() {
        forceWriter=bodyContentStack.pop();
        if(enablecfoutputonly>0 && outputState==0) {
            writer=devNull;
        }
        else writer=forceWriter;
        return forceWriter;
	}

    /**
     * @see railo.runtime.PageContext#outputStart()
     */
    public void outputStart() {
		outputState++;
        if(enablecfoutputonly>0 && outputState==1)writer=forceWriter;
		//if(enablecfoutputonly && outputState>0) unsetDevNull();
	}

    /**
     * @see railo.runtime.PageContext#outputEnd()
     */
    public void outputEnd() {
		outputState--;
		if(enablecfoutputonly>0 && outputState==0)writer=devNull;
	}

    /**
     * @see railo.runtime.PageContext#setCFOutputOnly(boolean)
     */
    public void setCFOutputOnly(boolean boolEnablecfoutputonly) {
        if(boolEnablecfoutputonly)this.enablecfoutputonly++;
        else if(this.enablecfoutputonly>0)this.enablecfoutputonly--;
        setCFOutputOnly(enablecfoutputonly);
        //if(!boolEnablecfoutputonly)setCFOutputOnly(enablecfoutputonly=0);
    }

    /**
     * @see railo.runtime.PageContext#setCFOutputOnly(short)
     */
    public void setCFOutputOnly(short enablecfoutputonly) {
        this.enablecfoutputonly=enablecfoutputonly;
        if(enablecfoutputonly>0) {
            if(outputState==0) writer=devNull;
        }
        else {
            writer=forceWriter;
        }
    }

    /**
     * @see railo.runtime.PageContext#setSilent()
     */
    public boolean setSilent() {
        boolean before=bodyContentStack.getDevNull();
		bodyContentStack.setDevNull(true);
		
        forceWriter = bodyContentStack.getWriter();
        writer=forceWriter;
        return before;
	}
	
    /**
     * @see railo.runtime.PageContext#unsetSilent()
     */
    public boolean unsetSilent() {
        boolean before=bodyContentStack.getDevNull();
        bodyContentStack.setDevNull(false);
        
        forceWriter = bodyContentStack.getWriter();
        if(enablecfoutputonly>0 && outputState==0) {
            writer=devNull;
        }
        else writer=forceWriter;
        return before;
	}
    
    /**
     * @see railo.runtime.PageContext#getDebugger()
     */
    public Debugger getDebugger() {
		return debugger;
	}
    
    public void executeRest(String realPath, boolean throwExcpetion) throws PageException  {
    	try{
    	String pathInfo = req.getPathInfo();
    	
    	// Service mapping
    	if(StringUtil.isEmpty(pathInfo) || pathInfo.equals("/")) {// ToDo
    		// list available services (if enabled in admin)
    		try {
				write("Available sevice mappings are:<ul>");
				railo.runtime.rest.Mapping[] mappings = config.getRestMappings();
				for(int i=0;i<mappings.length;i++){
					write("<li>"+mappings[i].getVirtual()+"</li>");
				}
				write("</ul>");
				return;
				
			} catch (IOException e) {
				throw Caster.toPageException(e);
			}
    	}	
    	
    	// check for format extension
    	int format = UDF.RETURN_FORMAT_JSON;
    	if(StringUtil.endsWithIgnoreCase(pathInfo, ".json")) {
    		pathInfo=pathInfo.substring(0,pathInfo.length()-5);
    	}
    	else if(StringUtil.endsWithIgnoreCase(pathInfo, ".wddx")) {
    		pathInfo=pathInfo.substring(0,pathInfo.length()-5);
    		format = UDF.RETURN_FORMAT_WDDX;
    	}
    	else if(StringUtil.endsWithIgnoreCase(pathInfo, ".serialize")) {
    		pathInfo=pathInfo.substring(0,pathInfo.length()-10);
    		format = UDF.RETURN_FORMAT_SERIALIZE;
    	}
    	else if(StringUtil.endsWithIgnoreCase(pathInfo, ".xml")) {
    		pathInfo=pathInfo.substring(0,pathInfo.length()-4);
    		format = UDF.RETURN_FORMAT_XML;
    	}
    	
    	
    	railo.runtime.rest.Result result = null;//config.getRestSource(pathInfo, null);
    	railo.runtime.rest.Mapping[] restMappings = config.getRestMappings();
    	railo.runtime.rest.Mapping mapping;
    	String callerPath=null;
    	if(restMappings!=null)for(int i=0;i<restMappings.length;i++) {
            mapping = restMappings[i];
            print.e(pathInfo+"=="+mapping.getVirtualWithSlash()+"="+pathInfo.startsWith(mapping.getVirtualWithSlash(),0));
            if(pathInfo.startsWith(mapping.getVirtualWithSlash(),0)) {
            	result = mapping.getResult(this,callerPath=pathInfo.substring(mapping.getVirtual().length()),format,null);
            	break;
            }
        }
    	

    	if(result!=null){
    		railo.runtime.rest.Source source=result.getSource();
    		/*print.e("path:");
    		print.e(result.getPath());
    		print.e("vars:");
    		print.e(result.getVariables());
    		
    		
    		
    		print.e("pagesource:"+source.getPageSource());
    		print.e("physical:"+source.getMapping().getPhysical());
    		print.e("path:"+source.getPath());
    		print.e("caller-path:"+callerPath);*/
    		base=source.getPageSource();
    		req.setAttribute("client", "railo-rest-1-0");
    		req.setAttribute("rest-path", callerPath);
    		req.setAttribute("rest-result", result);
    		
    		
    		doInclude(source.getPageSource());
    	}
    	}
    	catch(Throwable t){
    		t.printStackTrace();
    	}
    }
    
	
    /**
     * @throws PageException 
     * @see railo.runtime.PageContext#execute(java.lang.String)
     */

    public void execute(String realPath, boolean throwExcpetion) throws PageException  {
    	execute(realPath, throwExcpetion, true);
    }
    public void execute(String realPath, boolean throwExcpetion, boolean onlyTopLevel) throws PageException  {
    	//SystemOut.printDate(config.getOutWriter(),"Call:"+realPath+" (id:"+getId()+";running-requests:"+config.getThreadQueue().size()+";)");
	    ApplicationListener listener=config.getApplicationListener();
	    if(realPath.startsWith("/mapping-")){
	    	base=null;
	    	int index = realPath.indexOf('/',9);
	    	if(index>-1){
	    		String type = realPath.substring(9,index);
	    		if(type.equalsIgnoreCase("tag")){
	    			base=getPageSource(
	    					new Mapping[]{config.getTagMapping(),config.getServerTagMapping()},
	    					realPath.substring(index)
	    					);
	    		}
	    		else if(type.equalsIgnoreCase("customtag")){
	    			base=getPageSource(
	    					config.getCustomTagMappings(),
	    					realPath.substring(index)
	    					);
	    		}
	    		/*else if(type.equalsIgnoreCase("gateway")){
	    			base=config.getGatewayEngine().getMapping().getPageSource(realPath.substring(index));
	    			if(!base.exists())base=getPageSource(realPath.substring(index));
	    		}*/
	    	}
	    	if(base==null) base=config.getPageSource(this,null,realPath,onlyTopLevel,false,true);
	    	
	    }
	    else base=config.getPageSource(this,null,realPath,onlyTopLevel,false,true);
	    
	    try {
	    	listener.onRequest(this,base);
	    	log(false);
	    }
	    catch(Throwable t) {
	    	PageException pe = Caster.toPageException(t);
	    	if(!Abort.isSilentAbort(pe)){
	    		log(true);
	    		if(fdEnabled){
	        		FDSignal.signal(pe, false);
	        	}
	    		listener.onError(this,pe);	
	    	}
	    	else log(false);

	    	if(throwExcpetion) throw pe;
	    }
	    finally {
	    	
	    	
            if(enablecfoutputonly>0){
            	setCFOutputOnly((short)0);
            }
            if(!gatewayContext && getConfig().debug()) {
            	try {
					listener.onDebug(this);
				} 
            	catch (PageException pe) {
            		if(!Abort.isSilentAbort(pe))listener.onError(this,pe);
				}
            }
            base=null;
	    }
	}

	private void log(boolean error) {
		ConfigServerImpl cs = config.getConfigServerImpl();
		if(!isGatewayContext() && cs.isMonitoringEnabled()) {
            RequestMonitor[] monitors = cs.getRequestMonitors();
            if(monitors!=null)for(int i=0;i<monitors.length;i++){
            	if(monitors[i].isLogEnabled()){
	            	try {
	            		monitors[i].log(this,error);
	        		} 
	        		catch (Throwable e) {}
	            }
            }
		}
	}

	private PageSource getPageSource(Mapping[] mappings, String realPath) {
		PageSource ps;
		//print.err(mappings.length);
        for(int i=0;i<mappings.length;i++) {
            ps = mappings[i].getPageSource(realPath);
            //print.err(ps.getDisplayPath());
            if(ps.exists()) return ps;
            
        }
		return null;
	}



	/**
	 * @see javax.servlet.jsp.PageContext#include(java.lang.String)
	 */
	public void include(String realPath) throws ServletException,IOException  {
		HTTPUtil.include(this, realPath);
	}
	

	/**
	 * @see javax.servlet.jsp.PageContext#forward(java.lang.String)
	 */
	public void forward(String realPath) throws ServletException, IOException {
		HTTPUtil.forward(this, realPath);
	}

	/**
	 * @see railo.runtime.PageContext#include(railo.runtime.PageSource)
	 */
	public void include(PageSource ps) throws ServletException  {
		try {
			doInclude(ps);
		} catch (PageException pe) {
			throw new PageServletException(pe);
		}
	}

    /**
     * @see railo.runtime.PageContext#clear()
     */
    public void clear() {
		try {
			//print.o(getOut().getClass().getName());
			getOut().clear();
		} catch (IOException e) {}
	}
	
    /**
     * @see railo.runtime.PageContext#getRequestTimeout()
     */
    public long getRequestTimeout() {
		if(requestTimeout==-1)
			requestTimeout=config.getRequestTimeout().getMillis();
		return requestTimeout;
	}
	
    /**
     * @see railo.runtime.PageContext#setRequestTimeout(long)
     */
    public void setRequestTimeout(long requestTimeout) {
		this.requestTimeout = requestTimeout;
	}

    /**
     * @see PageContext#getCFID()
     */
    public String getCFID() {
		if(cfid==null) initIdAndToken();
		return cfid;
	}

    /**
     * @see PageContext#getCFToken()
     */
    public String getCFToken() {
		if(cftoken==null) initIdAndToken();
		return cftoken;
	}

    /**
     * @see PageContext#getURLToken()
     */
    public String getURLToken() {
	    if(getConfig().getSessionType()==Config.SESSION_TYPE_J2EE) {
	    	HttpSession s = getSession();
		    return "CFID="+getCFID()+"&CFTOKEN="+getCFToken()+"&jsessionid="+(s!=null?getSession().getId():"");
		}
		return "CFID="+getCFID()+"&CFTOKEN="+getCFToken();
	}
    
    /**
     * @see railo.runtime.PageContext#getJSessionId()
     */
    public String getJSessionId() {
	    if(getConfig().getSessionType()==Config.SESSION_TYPE_J2EE) {
		    return getSession().getId();
		}
		return null;
	}

    /**
     * initialize the cfid and the cftoken
     */
    private void initIdAndToken() {
        boolean setCookie=true;
        // From URL
        Object oCfid = urlScope().get(KeyImpl.CFID,null);
        Object oCftoken = urlScope().get(KeyImpl.CFTOKEN,null);
        // Cookie
        if((oCfid==null || !Decision.isGUIdSimple(oCfid)) || oCftoken==null) {
            setCookie=false;
            oCfid = cookieScope().get(KeyImpl.CFID,null);
            oCftoken = cookieScope().get(KeyImpl.CFTOKEN,null);
        }
        if(oCfid!=null && !Decision.isGUIdSimple(oCfid) ) {
        	oCfid=null;
        }
        // New One
        if(oCfid==null || oCftoken==null) {
            setCookie=true;
            cfid=ScopeContext.getNewCFId();
            cftoken=ScopeContext.getNewCFToken();
        }
        else {
            cfid=Caster.toString(oCfid,null);
            cftoken=Caster.toString(oCftoken,null);
        }
        
        if(setCookie && applicationContext.isSetClientCookies()) {
            cookieScope().setCookieEL(KeyImpl.CFID,cfid,CookieImpl.NEVER,false,"/",applicationContext.isSetDomainCookies()?(String) cgiScope().get(CGIImpl.SERVER_NAME,null):null);
            cookieScope().setCookieEL(KeyImpl.CFTOKEN,cftoken,CookieImpl.NEVER,false,"/",applicationContext.isSetDomainCookies()?(String) cgiScope().get(CGIImpl.SERVER_NAME,null):null);
        }
    }

    /**
     * @see PageContext#getId()
     */
    public int getId() {
		return id;
	}

    /**
     * @return returns the root JSP Writer
     * 
     */
    public CFMLWriter getRootOut() {
		return bodyContentStack.getBase();
	}

    /**
     * @see PageContext#getLocale()
     */
    public Locale getLocale() {
		if(locale==null) locale=config.getLocale();
		return locale;
	}

    /**
     * @see railo.runtime.PageContext#setPsq(boolean)
     */
    public void setPsq(boolean psq) {
		this.psq=psq;
	}
	
    /**
     * @see railo.runtime.PageContext#getPsq()
     */
    public boolean getPsq() {
		return psq;
	}
	
    /**
     * @see railo.runtime.PageContext#setLocale(java.util.Locale)
     */
    public void setLocale(Locale locale) {
		
		//String old=GetLocale.call(pc);
		this.locale=locale;
        HttpServletResponse rsp = getHttpServletResponse();
        
        String charEnc = rsp.getCharacterEncoding();
        rsp.setLocale(locale);
        if(charEnc.equalsIgnoreCase("UTF-8")) {
        	rsp.setContentType("text/html; charset=UTF-8");
        }
        else if(!charEnc.equalsIgnoreCase(rsp.getCharacterEncoding())) {
                rsp.setContentType("text/html; charset=" + charEnc);
        }
	}
    

    /**
     * @see railo.runtime.PageContext#setLocale(java.lang.String)
     */
    public void setLocale(String strLocale) throws ExpressionException {
		setLocale(Caster.toLocale(strLocale));
	}

    /**
     * @see railo.runtime.PageContext#setErrorPage(railo.runtime.err.ErrorPage)
     */
    public void setErrorPage(ErrorPage ep) {
		errorPagePool.setErrorPage(ep);
	}
	
    /**
     * @see railo.runtime.PageContext#use(java.lang.Class)
     * /
    public Tag use(Class tagClass) throws PageException {
        
        parentTag=currentTag;
		currentTag= tagHandlerPool.use(tagClass);
        if(currentTag==parentTag) throw new ApplicationException("");
        currentTag.setPageContext(this);
        currentTag.setParent(parentTag);
        return currentTag;
	}*/

    /**
     *
     * @see railo.runtime.PageContext#use(java.lang.String)
     */
    public Tag use(String tagClassName) throws PageException {

        parentTag=currentTag;
		currentTag= tagHandlerPool.use(tagClassName);
        if(currentTag==parentTag) throw new ApplicationException("");
        currentTag.setPageContext(this);
        currentTag.setParent(parentTag);
        return currentTag;
	}
    
    /**
     * @see railo.runtime.PageContext#use(java.lang.Class)
     */
    public Tag use(Class clazz) throws PageException {
        return use(clazz.getName());
	}
	
    /**
     * @see railo.runtime.PageContext#reuse(javax.servlet.jsp.tagext.Tag)
     */
    public void reuse(Tag tag) throws PageException {
        currentTag=tag.getParent();
        tagHandlerPool.reuse(tag);
	}

    /**
     * @see railo.runtime.PageContext#getQueryCache()
     */
    public QueryCache getQueryCache() {
        return queryCache;
    }
    
    /**
     * @see railo.runtime.PageContext#initBody(javax.servlet.jsp.tagext.BodyTag, int)
     */
    public void initBody(BodyTag bodyTag, int state) throws JspException {
        if (state != Tag.EVAL_BODY_INCLUDE) {
            bodyTag.setBodyContent(pushBody());
            bodyTag.doInitBody();
        }
    }
    
    /**
     * @see railo.runtime.PageContext#releaseBody(javax.servlet.jsp.tagext.BodyTag, int)
     */
    public void releaseBody(BodyTag bodyTag, int state) {
        if(bodyTag instanceof TryCatchFinally) {
            ((TryCatchFinally)bodyTag).doFinally();
        }
        if (state != Tag.EVAL_BODY_INCLUDE)popBody();
    }
    
    /* *
     * @return returns the cfml compiler 
     * /
    public CFMLCompiler getCompiler() {
        return compiler;
    }*/
    
    /**
     * @see railo.runtime.PageContext#setVariablesScope(railo.runtime.type.scope.Scope)
     */
    public void setVariablesScope(Variables variables) {
    	this.variables=variables;
        undefinedScope().setVariableScope(variables);
        
        if(variables instanceof ComponentScope) {
        	activeComponent=((ComponentScope)variables).getComponent();
            /*if(activeComponent.getAbsName().equals("jm.pixeltex.supersuperApp")){
            	print.dumpStack();
            }*/
        }
        else {
            activeComponent=null;
        }
    }

    

    /**
     * @see railo.runtime.PageContext#getActiveComponent()
     */
    public Component getActiveComponent() {
        return activeComponent;
    }
    
    
    
    
    /**
     * @see railo.runtime.PageContext#getRemoteUser()
     */
    public Credential getRemoteUser() throws PageException {
        if(remoteUser==null) {
        	String name=Login.getApplicationName(applicationContext);
		    Resource roles = config.getConfigDir().getRealResource("roles");
		    
        	if(applicationContext.getLoginStorage()==Scope.SCOPE_SESSION) {
                Object auth = sessionScope().get(name,null);
                if(auth!=null) {
                    remoteUser=CredentialImpl.decode(auth,roles);
                }
            }
            else if(applicationContext.getLoginStorage()==Scope.SCOPE_COOKIE) {
                Object auth = cookieScope().get(name,null);
                if(auth!=null) {
                    remoteUser=CredentialImpl.decode(auth,roles);
                }
            }
        }
        return remoteUser;
    }
    
    /**
     * @see railo.runtime.PageContext#clearRemoteUser()
     */
    public void clearRemoteUser() {
        if(remoteUser!=null)remoteUser=null;
        String name=Login.getApplicationName(applicationContext);
	    
        cookieScope().removeEL(KeyImpl.init(name));
        try {
			sessionScope().removeEL(KeyImpl.init(name));
		} catch (PageException e) {}
        
    }
    
    /**
     * @see railo.runtime.PageContext#setRemoteUser(railo.runtime.security.Credential)
     */
    public void setRemoteUser(Credential remoteUser) {
        this.remoteUser = remoteUser;
    }
    
    /**
     * @see railo.runtime.PageContext#getVariableUtil()
     */
    public VariableUtil getVariableUtil() {
        return variableUtil;
    }

    /**
     * @see railo.runtime.PageContext#throwCatch()
     */
    public void throwCatch() throws PageException {
        if(exception!=null) throw exception;
        throw new ApplicationException("invalid context for tag/script expression rethow");
    }

    /**
     * @see railo.runtime.PageContext#setCatch(java.lang.Throwable)
     */
    public PageException setCatch(Throwable t) {
    	if(t==null) {
    		exception=null;
    		undefinedScope().removeEL(CFCATCH);
    	}
    	else {
    		exception = Caster.toPageException(t);
    		undefinedScope().setEL(CFCATCH,exception.getCatchBlock(this));
    		if(!gatewayContext && config.debug()) debugger.addException(config,exception);
    	}
    	return exception;
    }
    
    public void setCatch(PageException pe) {
    	exception = pe;
    	if(pe==null) {
    		undefinedScope().removeEL(CFCATCH);
    	}
    	else {
    		undefinedScope().setEL(CFCATCH,pe.getCatchBlock(config));
    		if(!gatewayContext && config.debug()) debugger.addException(config,exception);
    	}
    }
    
    public void setCatch(PageException pe,boolean caught, boolean store) {
		if(fdEnabled){
    		FDSignal.signal(pe, caught);
    	}
    	exception = pe;
    	if(store){
	    	if(pe==null) {
	    		undefinedScope().removeEL(CFCATCH);
	    	}
	    	else {
	    		undefinedScope().setEL(CFCATCH,pe.getCatchBlock(config));
	    		if(!gatewayContext && config.debug()) debugger.addException(config,exception);
	    	}
    	}
    }
    
    /**
     * @return return current catch
     */
    public PageException getCatch() {
    	return exception;
    }
    
    /**
     * @see railo.runtime.PageContext#clearCatch()
     */
    public void clearCatch() {
        exception = null;
    	undefinedScope().removeEL(CFCATCH);
    }

    /**
     * @see railo.runtime.PageContext#addPageSource(railo.runtime.PageSource, boolean)
     */
    public void addPageSource(PageSource ps, boolean alsoInclude) {
    	pathList.add(ps);
        if(alsoInclude) 
            includePathList.add(ps);
    }
    

    public void addPageSource(PageSource ps, PageSource psInc) {
    	pathList.add(ps);
        if(psInc!=null) 
            includePathList.add(psInc);
    }

    /**
     * @see railo.runtime.PageContext#removeLastPageSource(boolean)
     */
    public void removeLastPageSource(boolean alsoInclude) {
    	pathList.removeLast();
        if(alsoInclude) 
            includePathList.removeLast();
    }


    public UDF[] getUDFs() {
    	return udfs.toArray(new UDF[udfs.size()]);
    }
    
    public void addUDF(UDF udf) {
    	udfs.add(udf);
    }

    /**
     * @see railo.runtime.PageContext#removeLastPageSource(boolean)
     */
    public void removeUDF() {
    	udfs.pop();
    }

    /**
     * @see railo.runtime.PageContext#getFTPPool()
     */
    public FTPPool getFTPPool() {
        return ftpPool;
    }

    /* *
     * @return Returns the manager.
     * /
    public DataSourceManager getManager() {
        return manager;
    }*/
    
    /**
     * @see railo.runtime.PageContext#getApplicationContext()
     */
    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }
    
    /**
     * @see railo.runtime.PageContext#setApplicationContext(railo.runtime.util.ApplicationContext)
     */
    public void setApplicationContext(ApplicationContext applicationContext) {
    	
        session=null;
        application=null;
        client=null;
        this.applicationContext = applicationContext;
        
        int scriptProtect = applicationContext.getScriptProtect();
        
        // ScriptProtecting
        if(config.mergeFormAndURL()) {
        	form.setScriptProtecting(
        			(scriptProtect&ApplicationContext.SCRIPT_PROTECT_FORM)>0 
        			|| 
        			(scriptProtect&ApplicationContext.SCRIPT_PROTECT_URL)>0);
        }
        else {
            form.setScriptProtecting((scriptProtect&ApplicationContext.SCRIPT_PROTECT_FORM)>0);
            url.setScriptProtecting((scriptProtect&ApplicationContext.SCRIPT_PROTECT_URL)>0);
        }
        cookie.setScriptProtecting((scriptProtect&ApplicationContext.SCRIPT_PROTECT_COOKIE)>0);
        cgi.setScriptProtecting((scriptProtect&ApplicationContext.SCRIPT_PROTECT_CGI)>0);
        undefined.reinitialize(this);
    }
    
    /**
     * @return return  value of method "onApplicationStart" or true
     * @throws PageException 
     */
    public boolean initApplicationContext() throws PageException {
    	boolean initSession=false;
	    AppListenerSupport listener = (AppListenerSupport) config.getApplicationListener();
    	KeyLock<String> lock = config.getContextLock();
    	String name=StringUtil.emptyIfNull(applicationContext.getName());
    	String token=name+":"+getCFID();
    	
    	Lock tokenLock = lock.lock(token,getRequestTimeout());
    	//print.o("outer-lock  :"+token);
    	try {
    		// check session before executing any code
	    	initSession=applicationContext.isSetSessionManagement() && listener.hasOnSessionStart(this) && !scopeContext.hasExistingSessionScope(this);
	    	
	    	// init application
	    	
	    	Lock nameLock = lock.lock(name,getRequestTimeout());
	    	//print.o("inner-lock  :"+token);
	    	try {
	    		RefBoolean isNew=new RefBooleanImpl(false);
			    application=scopeContext.getApplicationScope(this,isNew);// this is needed that the application scope is initilized
		    	if(isNew.toBooleanValue()) {
				    try {
						if(!listener.onApplicationStart(this)) {
							scopeContext.removeApplicationScope(this);
						    return false;
						}
					} catch (PageException pe) {
						scopeContext.removeApplicationScope(this);
						throw pe;
					}
			    }
	    	}
	    	finally{
		    	//print.o("inner-unlock:"+token);
	    		lock.unlock(nameLock);
	    	}
    	
	    	// init session
		    if(initSession) {
		    	scopeContext.getSessionScope(this, DUMMY_BOOL);// this is needed that the session scope is initilized
		    	listener.onSessionStart(this);
			}
    	}
    	finally{
	    	//print.o("outer-unlock:"+token);
    		lock.unlock(tokenLock);
    	}
	    return true;
    }
    

    /**
     * @return the scope factory
     */
    public ScopeFactory getScopeFactory() {
        return scopeFactory;
    }

    
    
    
    /**
     * @see railo.runtime.PageContext#getCurrentTag()
     */
    public Tag getCurrentTag() {
        return currentTag;
    }

    /**
     * @see railo.runtime.PageContext#getStartTime()
     */
    public long getStartTime() {
        return startTime;
    }
    
    /**
     * @see railo.runtime.PageContext#getThread()
     */
    public Thread getThread() {
        return thread;
    }
    


	public void setThread(Thread thread) {
		this.thread=thread;
	}

    /**
     * @see railo.runtime.PageContext#getExecutionTime()
     */
    public int getExecutionTime() {
        return executionTime;
    }

    /**
     * @see railo.runtime.PageContext#setExecutionTime(int)
     */
    public void setExecutionTime(int executionTime) {
        this.executionTime = executionTime;
    }

    /**
     * @see railo.runtime.PageContext#compile(railo.runtime.PageSource)
     */
    public synchronized void compile(PageSource pageSource) throws PageException {
        Resource classRootDir = pageSource.getMapping().getClassRootDirectory();
        
        try {
            config.getCompiler().compile(
                    config,
                    pageSource,
                    config.getTLDs(),
                    config.getFLDs(),
                    classRootDir,
                    pageSource.getJavaName()
                    );
        } catch (Exception e) {
            throw Caster.toPageException(e);
        }        
    }

    /**
     * @see railo.runtime.PageContext#compile(java.lang.String)
     */
    public void compile(String realPath) throws PageException {
        compile(getRelativePageSource(realPath));
    }
    
    public HttpServlet getServlet() {
        return servlet;
    }

    /**
     * @see railo.runtime.PageContext#loadComponent(java.lang.String)
     */
    public railo.runtime.Component loadComponent(String compPath) throws PageException {
    	return ComponentLoader.loadComponent(this,compPath,null,null);
    }

	/**
	 * @return the base
	 */
	public PageSource getBase() {
		return base;
	}

	/**
	 * @param base the base to set
	 */
	public void setBase(PageSource base) {
		this.base = base;
	}

	/**
	 * @return the isCFCRequest
	 */
	public boolean isCFCRequest() {
		return isCFCRequest;
	}
	
	/**
	 * @see railo.runtime.config.Config#getDataSourceManager()
	 */
	public DataSourceManager getDataSourceManager() {
		return manager;
	}

	/**
	 * @see railo.runtime.PageContext#evaluate(java.lang.String)
	 */
	public Object evaluate(String expression) throws PageException {
		return new CFMLExpressionInterpreter().interpret(this,expression);
	}
	
	/**
	 * @see railo.runtime.PageContext#serialize(java.lang.Object)
	 */
	public String serialize(Object expression) throws PageException {
		return Serialize.call(this, expression);
	}

	/**
	 * @return the activeUDF
	 */
	public UDF getActiveUDF() {
		return activeUDF;
	}

	/**
	 * @param activeUDF the activeUDF to set
	 */
	public void setActiveUDF(UDF activeUDF) {
		this.activeUDF = activeUDF;
	}

	/**
	 *
	 * @see railo.runtime.PageContext#getCFMLFactory()
	 */
	public CFMLFactory getCFMLFactory() {
		return config.getFactory();
	}

	/**
	 * @see railo.runtime.PageContext#getParentPageContext()
	 */
	public PageContext getParentPageContext() {
		return parent;
	}


	/**
	 * @see railo.runtime.PageContext#getThreadScopeNames()
	 */
	public String[] getThreadScopeNames() {
		if(threads==null)return new String[0];
		Set ks = threads.keySet();
		return (String[]) ks.toArray(new String[ks.size()]);
	}
	
	/**
	 * @see railo.runtime.PageContext#getThreadScope(java.lang.String)
	 */
	public Threads getThreadScope(String name) {
		return getThreadScope(KeyImpl.init(name));
	}
	
	public Threads getThreadScope(Collection.Key name) {
		if(threads==null)threads=new StructImpl();
		Object obj = threads.get(name,null);
		if(obj instanceof Threads)return (Threads) obj;
		return null;
	}
	
	public Object getThreadScope(Collection.Key name,Object defaultValue) {
		if(threads==null)threads=new StructImpl();
		if(name.equalsIgnoreCase(CFTHREAD)) return threads;
		return threads.get(name,defaultValue);
	}
	
	public Object getThreadScope(String name,Object defaultValue) {
		if(threads==null)threads=new StructImpl();
		if(name.equalsIgnoreCase(CFTHREAD.getLowerString())) return threads;
		return threads.get(name,defaultValue);
	}

	/**
	 * @see railo.runtime.PageContext#setThreadScope(java.lang.String, railo.runtime.type.scope.Threads)
	 */
	public void setThreadScope(String name,Threads ct) {
		hasFamily=true;
		if(threads==null)	threads=new StructImpl();
		threads.setEL(name, ct);
	}
	
	public void setThreadScope(Collection.Key name,Threads ct) {
		hasFamily=true;
		if(threads==null)	threads=new StructImpl();
		threads.setEL(name, ct);
	}

	/**
	 * @see railo.runtime.PageContext#hasFamily()
	 */
	public boolean hasFamily() {
		return hasFamily;
	}
	

	public DatasourceConnection _getConnection(String datasource, String user,String pass) throws PageException {
		return _getConnection(config.getDataSource(datasource),user,pass);
	}
	
	public DatasourceConnection _getConnection(DataSource ds, String user,String pass) throws PageException {
		
		String id=DatasourceConnectionPool.createId(ds,user,pass);
		DatasourceConnection dc=conns.get(id);
		if(dc!=null && DatasourceConnectionPool.isValid(dc,null)){
			return dc;
		}
		dc=config.getDatasourceConnectionPool().getDatasourceConnection(this,ds, user, pass);
		conns.put(id, dc);
		return dc;
	}

	/**
	 * @see railo.runtime.PageContext#getTimeZone()
	 */
	public TimeZone getTimeZone() {
		if(timeZone==null) timeZone=config.getTimeZone();
		return timeZone;
	}
	/**
	 * @see railo.runtime.PageContext#setTimeZone(java.util.TimeZone)
	 */
	public void  setTimeZone(TimeZone timeZone) {
		this.timeZone=timeZone;
	}


	/**
	 * @return the requestId
	 */
	public int getRequestId() {
		return requestId;
	}

	private Set<String> pagesUsed=new HashSet<String>();
	
	



	public boolean isTrusted(Page page) {
		if(page==null)return false;
		short it = config.getInspectTemplate();
		if(it==ConfigImpl.INSPECT_NEVER)return true;
		if(it==ConfigImpl.INSPECT_ALWAYS)return false;
		
		return pagesUsed.contains(""+page.hashCode());
	}
	
	public void setPageUsed(Page page) {
		pagesUsed.add(""+page.hashCode());
	}

	/**
	 * @see railo.runtime.PageContext#exeLogStart(int, java.lang.String)
	 */
	public void exeLogStart(int line,String id){
		if(execLog!=null)execLog.start(line, id);
	}
	
	/**
	 * @see railo.runtime.PageContext#exeLogEnd(int, java.lang.String)
	 */
	public void exeLogEnd(int line,String id){
		if(execLog!=null)execLog.end(line, id);
	}

	
	/**
	 * @param create if set to true, railo creates a session when not exist
	 * @return
	 * @throws PageException
	 */
	public ORMSession getORMSession(boolean create) throws PageException {
		if(ormSession==null || !ormSession.isValid())	{
			if(!create) return null;
			ormSession=config.getORMEngine(this).createSession(this);
		}
		DatasourceManagerImpl manager = (DatasourceManagerImpl) getDataSourceManager();
		manager.add(this,ormSession);
		
		return ormSession;
		
		
	}

	public void resetSession() {
		this.session=null;
	}
	/**
	 * @return the gatewayContext
	 */
	public boolean isGatewayContext() {
		return gatewayContext;
	}



	/**
	 * @param gatewayContext the gatewayContext to set
	 */
	public void setGatewayContext(boolean gatewayContext) {
		this.gatewayContext = gatewayContext;
	}



	public void setServerPassword(String serverPassword) {
		this.serverPassword=serverPassword;
	}
	public String getServerPassword() {
		return serverPassword;
	}

	public short getSessionType() {
		if(isGatewayContext())return Config.SESSION_TYPE_CFML;
		return applicationContext.getSessionType();
	}
	
	// this is just a wrapper method for ACF
	public Scope SymTab_findBuiltinScope(String name) throws PageException {
		return scope(name, null);
	}
}