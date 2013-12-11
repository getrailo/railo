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
import java.util.Stack;
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

import railo.commons.io.BodyContentStack;
import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceClassLoader;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.PhysicalClassLoader;
import railo.commons.lang.SizeOf;
import railo.commons.lang.StringUtil;
import railo.commons.lang.SystemOut;
import railo.commons.lang.mimetype.MimeType;
import railo.commons.lang.types.RefBoolean;
import railo.commons.lang.types.RefBooleanImpl;
import railo.commons.lock.KeyLock;
import railo.commons.lock.Lock;
import railo.commons.net.HTTPUtil;
import railo.intergral.fusiondebug.server.FDSignal;
import railo.runtime.component.ComponentLoader;
import railo.runtime.config.Config;
import railo.runtime.config.ConfigImpl;
import railo.runtime.config.ConfigWeb;
import railo.runtime.config.ConfigWebImpl;
import railo.runtime.config.Constants;
import railo.runtime.config.NullSupportHelper;
import railo.runtime.db.DataSource;
import railo.runtime.db.DataSourceManager;
import railo.runtime.db.DatasourceConnection;
import railo.runtime.db.DatasourceConnectionPool;
import railo.runtime.db.DatasourceManagerImpl;
import railo.runtime.debug.ActiveLock;
import railo.runtime.debug.ActiveQuery;
import railo.runtime.debug.DebugCFMLWriter;
import railo.runtime.debug.DebugEntryTemplate;
import railo.runtime.debug.Debugger;
import railo.runtime.debug.DebuggerImpl;
import railo.runtime.debug.DebuggerPro;
import railo.runtime.dump.DumpUtil;
import railo.runtime.dump.DumpWriter;
import railo.runtime.engine.ExecutionLog;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.err.ErrorPage;
import railo.runtime.err.ErrorPageImpl;
import railo.runtime.err.ErrorPagePool;
import railo.runtime.exp.Abort;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.CasterException;
import railo.runtime.exp.DatabaseException;
import railo.runtime.exp.ExceptionHandler;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.MissingIncludeException;
import railo.runtime.exp.PageException;
import railo.runtime.exp.PageExceptionBox;
import railo.runtime.exp.PageServletException;
import railo.runtime.functions.dynamicEvaluation.Serialize;
import railo.runtime.interpreter.CFMLExpressionInterpreter;
import railo.runtime.interpreter.VariableInterpreter;
import railo.runtime.listener.ApplicationContext;
import railo.runtime.listener.ApplicationContextPro;
import railo.runtime.listener.ApplicationListener;
import railo.runtime.listener.ClassicApplicationContext;
import railo.runtime.listener.JavaSettingsImpl;
import railo.runtime.listener.ModernAppListenerException;
import railo.runtime.monitor.RequestMonitor;
import railo.runtime.net.ftp.FTPPool;
import railo.runtime.net.ftp.FTPPoolImpl;
import railo.runtime.net.http.HTTPServletRequestWrap;
import railo.runtime.net.http.ReqRspUtil;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.orm.ORMConfiguration;
import railo.runtime.orm.ORMEngine;
import railo.runtime.orm.ORMSession;
import railo.runtime.query.QueryCache;
import railo.runtime.rest.RestRequestListener;
import railo.runtime.rest.RestUtil;
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
import railo.runtime.type.UDFPlus;
import railo.runtime.type.it.ItAsEnum;
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
import railo.runtime.type.util.CollectionUtil;
import railo.runtime.type.util.KeyConstants;
import railo.runtime.util.PageContextUtil;
import railo.runtime.util.VariableUtil;
import railo.runtime.util.VariableUtilImpl;
import railo.runtime.writer.CFMLWriter;
import railo.runtime.writer.DevNullBodyContent;

/**
 * page context for every page object. 
 * the PageContext is a jsp page context expanded by CFML functionality.
 * for example you have the method getSession to get jsp combatible session object (HTTPSession)
 *  and with sessionScope() you get CFML combatible session object (Struct,Scope).
 */
public final class PageContextImpl extends PageContext implements Sizeable {
	
	private static final RefBoolean DUMMY_BOOL = new RefBooleanImpl(false);
	
	private static int counter=0;
	
	/** 
	 * Field <code>pathList</code>
	 */
    private LinkedList<UDF> udfs=new LinkedList<UDF>();
    private LinkedList<PageSource> pathList=new LinkedList<PageSource>();
    private LinkedList<PageSource> includePathList=new LinkedList<PageSource>();
    private Set<PageSource> includeOnce=new HashSet<PageSource>();
	
	/**
	 * Field <code>executionTime</code>
	 */
	protected long executionTime=0;
	
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
    private Undefined undefined;
	
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

    private DebuggerPro debugger=new DebuggerImpl();
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
	private final QueryCache queryCache;

	private Component activeComponent;
	private UDF activeUDF;
	private Collection.Key activeUDFCalledName;
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

	private PageException pe;

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
	 * @param scopeContext
	 * @param config Configuration of the CFML Container
	 * @param queryCache Query Cache Object
	 * @param id identity of the pageContext
	 * @param servlet
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
		
		defaultApplicationContext=new ClassicApplicationContext(config,"",true,null);
		
	}

	@Override
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
        if(config.debugLogOutput()) {
        	CFMLWriter w = config.getCFMLWriter(req,rsp);
        	w.setAllowCompression(false);
        	DebugCFMLWriter dcw = new DebugCFMLWriter(w);
        	bodyContentStack.init(dcw);
        	debugger.setOutputLog(dcw);
        }
        else {
        	bodyContentStack.init(config.getCFMLWriter(req,rsp));
        }
        
		
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
		
		fdEnabled=!config.allowRequestTimeout();
		
		if(config.getExecutionLogEnabled())
			this.execLog=config.getExecutionLogFactory().getInstance(this);
		if(debugger!=null)
			debugger.init(config);
		
		undefined.initialize(this);
		return this;
	 }
	
	@Override
	public void release() {
        if(config.getExecutionLogEnabled()){
        	execLog.release();
			execLog=null;
        }
		
		if(config.debug()) {
    		if(!gatewayContext && !isChild)
			    config.getDebuggerPool().store(this, debugger);
    		debugger.reset();
    	}
		else ((DebuggerImpl)debugger).resetTraces(); // traces can alo be used when debugging is off
		
		this.serverPassword=null;

//		boolean isChild=parent!=null;       // isChild is defined in the class outside this method
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
            	variables.release(this);
            }
            undefined.release(this);
            urlForm.release(this);
        	request.release();
        }
        cgi.release();
        argument.release(this);
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
        

    	gatewayContext=false;
    	
    	manager.release();
    	includeOnce.clear();
    	pe=null;

	}

    @Override
    public void write(String str) throws IOException {
    	writer.write(str);
	}

    @Override
    public void forceWrite(String str) throws IOException {
        forceWriter.write(str);
    }
	
    @Override
    public void writePSQ(Object o) throws IOException, PageException {
    	if(o instanceof Date || Decision.isDate(o, false)) {
			writer.write(Caster.toString(o));
		}
		else {
            writer.write(psq?Caster.toString(o):StringUtil.replace(Caster.toString(o),"'","''",false));
		}
	} 
	
    @Override
    public void flush() {
		try {
			getOut().flush();
		} catch (IOException e) {}
	}
	
    @Override
    public void close() {
		IOUtil.closeEL(getOut());
	}
	
    public PageSource getRelativePageSource(String realPath) {
    	SystemOut.print(config.getOutWriter(),"method getRelativePageSource is deprecated");
    	if(StringUtil.startsWith(realPath,'/')) return PageSourceImpl.best(getPageSources(realPath));
    	if(pathList.size()==0) return null;
		return pathList.getLast().getRealPage(realPath);
	}
    
   public PageSource getRelativePageSourceExisting(String realPath) {
    	if(StringUtil.startsWith(realPath,'/')) return getPageSourceExisting(realPath);
    	if(pathList.size()==0) return null;
		PageSource ps = pathList.getLast().getRealPage(realPath);
		if(PageSourceImpl.pageExist(ps)) return ps;
		return null;
	}
    
     /**
     * 
     * @param realPath
     * @param previous relative not to the caller, relative to the callers caller
     * @return
     */
    public PageSource getRelativePageSourceExisting(String realPath, boolean previous ) {
    	if(StringUtil.startsWith(realPath,'/')) return getPageSourceExisting(realPath);
    	if(pathList.size()==0) return null;
    	
    	PageSource ps=null,tmp=null;
    	if(previous) {
    		boolean valid=false;
    		ps=pathList.getLast();
    		for(int i=pathList.size()-2;i>=0;i--){
    			tmp=pathList.get(i);
    			if(tmp!=ps) {
    				ps=tmp;
    				valid=true;
    				break;
    			}
    		}
    		if(!valid) return null;
    	}
    	else ps=pathList.getLast();
    	
    	ps = ps.getRealPage(realPath);
		if(PageSourceImpl.pageExist(ps)) return ps;
		return null;
	}
    
    public PageSource[] getRelativePageSources(String realPath) {
    	if(StringUtil.startsWith(realPath,'/')) return getPageSources(realPath);
    	if(pathList.size()==0) return null;
		return new PageSource[]{ pathList.getLast().getRealPage(realPath)};
	}
    
    public PageSource getPageSource(String realPath) {
    	SystemOut.print(config.getOutWriter(),"method getPageSource is deprecated");
    	return PageSourceImpl.best(config.getPageSources(this,applicationContext.getMappings(),realPath,false,useSpecialMappings,true));
	}
    
    public PageSource[] getPageSources(String realPath) {
    	return config.getPageSources(this,applicationContext.getMappings(),realPath,false,useSpecialMappings,true);
	}
    
    public PageSource getPageSourceExisting(String realPath) {
    	return config.getPageSourceExisting(this,applicationContext.getMappings(),realPath,false,useSpecialMappings,true,false);
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

	@Override
	public void doInclude(String realPath) throws PageException {
		doInclude(getRelativePageSources(realPath),false);
	}
	
	@Override
	public void doInclude(String realPath, boolean runOnce) throws PageException {
		doInclude(getRelativePageSources(realPath),runOnce);
	}

	@Override
	public void doInclude(PageSource source) throws PageException {
		doInclude(new PageSource[]{source},false);
	}

	@Override
	public void doInclude(PageSource[] sources, boolean runOnce) throws PageException {
    	// debug
		if(!gatewayContext && config.debug()) {
			long currTime=executionTime;
            long exeTime=0;
            long time=System.nanoTime();
            
            Page currentPage = PageSourceImpl.loadPage(this, sources);
			if(runOnce && includeOnce.contains(currentPage.getPageSource())) return;
            DebugEntryTemplate debugEntry=debugger.getEntry(this,currentPage.getPageSource());
            try {
                addPageSource(currentPage.getPageSource(),true);
                debugEntry.updateFileLoadTime((System.nanoTime()-time));
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
				includeOnce.add(currentPage.getPageSource());
				long diff= ((System.nanoTime()-exeTime)-(executionTime-currTime));
			    executionTime+=(System.nanoTime()-time);
				debugEntry.updateExeTime(diff);
				removeLastPageSource(true);
			}	
		}
	// no debug
		else {
			Page currentPage = PageSourceImpl.loadPage(this, sources);
			if(runOnce && includeOnce.contains(currentPage.getPageSource())) return;
	    	try {
				addPageSource(currentPage.getPageSource(),true);
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
				includeOnce.add(currentPage.getPageSource());
				removeLastPageSource(true);
			}	
		}
	}

	@Override
    public Array getTemplatePath() throws PageException {
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
    	other.bodyContentStack.init(config.getCFMLWriter(other.req,other.rsp));
    	//other.bodyContentStack.init(other.req,other.rsp,other.config.isSuppressWhitespace(),other.config.closeConnection(), other.config.isShowVersion(),config.contentLength(),config.allowCompression());
    	other.writer=other.bodyContentStack.getWriter();
    	other.forceWriter=other.writer;
        
    	other.psq=psq;
    	other.gatewayContext=gatewayContext;
        
        // thread
        if(threads!=null){
        	synchronized (threads) {
				
	        	java.util.Iterator<Entry<Key, Object>> it2 = threads.entryIterator();
	        	Entry<Key, Object> entry;
	        	while(it2.hasNext()) {
	        		entry = it2.next();
	        		other.setThreadScope(entry.getKey(), (Threads)entry.getValue());
	        	}
			}
        }
        
        
        // initialize stuff
        other.undefined.initialize(other);
    	
        
    }
    
    public int getCurrentLevel() {
        return includePathList.size()+1;
    }
    
    /**
     * @return the current template SourceFile
     */
    public PageSource getCurrentPageSource() {
    	return pathList.getLast();
    }
    public PageSource getCurrentPageSource(PageSource defaultvalue) {
    	try{
    		return pathList.getLast();
    	}
    	catch(Throwable t){
    		return defaultvalue;
    	}
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
	
    @Override
    public Resource getRootTemplateDirectory() {
		return config.getResource(ReqRspUtil.getRootPath(servlet.getServletContext()));
	}

    @Override
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
    
    @Override
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
    
    @Override
    public Variables variablesScope() { return variables; }
	
    @Override
    public URL urlScope() { 
    	if(!url.isInitalized())url.initialize(this);
		return url;
    }
	
    @Override
    public Form formScope() {
    	if(!form.isInitalized())form.initialize(this);
		return form;
    }
	
    @Override
    public URLForm urlFormScope() {
    	if(!urlForm.isInitalized())urlForm.initialize(this);
		return urlForm;
    }

    @Override
    public Request requestScope() { return request; }
	
    @Override
    public CGI cgiScope() {
		if(!cgi.isInitalized())cgi.initialize(this);
		return cgi;
	}
	
    @Override
    public Application applicationScope() throws PageException {
		if(application==null) {
			if(!applicationContext.hasName())
				throw new ExpressionException("there is no application context defined for this application","you can define a application context with the tag "+railo.runtime.config.Constants.CFAPP_NAME+"/"+railo.runtime.config.Constants.APP_CFC);
			application=scopeContext.getApplicationScope(this,DUMMY_BOOL);
		}
		return application; 
	}

    @Override
    public Argument argumentsScope() { return argument; }

    
    @Override
    public Argument argumentsScope(boolean bind) { 
            //Argument a=argumentsScope(); 
            if(bind)argument.setBind(true); 
            return argument; 
    } 
    
    @Override
    public Local localScope() { 
    	//if(local==localUnsupportedScope) 
    	//	throw new PageRuntimeException(new ExpressionException("Unsupported Context for Local Scope"));
    	return local;
    }
    
    @Override
    public Local localScope(boolean bind) { 
    	if(bind)local.setBind(true); 
    	//if(local==localUnsupportedScope) 
    	//	throw new PageRuntimeException(new ExpressionException("Unsupported Context for Local Scope"));
    	return local; 
    }


    public Object localGet() throws PageException { 
    	return localGet(false);
    }
    
    public Object localGet(boolean bind, Object defaultValue) { 
    	if(undefined.getCheckArguments()){
    		return localScope(bind);
    	}
    	return undefinedScope().get(KeyConstants._local,defaultValue);
    }
    
    public Object localGet(boolean bind) throws PageException { 
    	// inside a local supported block
    	if(undefined.getCheckArguments()){
    		return localScope(bind);
    	}
    	return undefinedScope().get(KeyConstants._local);
    }

    public Object localTouch() throws PageException { 
    	return localTouch(false);
    }
    
    public Object localTouch(boolean bind) throws PageException { 
    	// inside a local supported block
    	if(undefined.getCheckArguments()){
    		return localScope(bind);
    	}
    	return touch(undefinedScope(), KeyConstants._local);
    	//return undefinedScope().get(LOCAL);
    }
    
    public Object thisGet() throws PageException { 
    	return thisTouch();
    }

    public Object thisTouch() throws PageException {
    	// inside a component
    	if(undefined.variablesScope() instanceof ComponentScope){
    		return ((ComponentScope)undefined.variablesScope()).getComponent();
    	}
    	return undefinedScope().get(KeyConstants._THIS);
    }
    
    public Object thisGet(Object defaultValue) { 
    	return thisTouch(defaultValue);
    }

    public Object thisTouch(Object defaultValue) {
    	// inside a component
    	if(undefined.variablesScope() instanceof ComponentScope){
    		return ((ComponentScope)undefined.variablesScope()).getComponent();
    	}
    	return undefinedScope().get(KeyConstants._THIS,defaultValue);
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
	
    @Override
    public Session sessionScope() throws PageException {
		return sessionScope(true);
	}
    public Session sessionScope(boolean checkExpires) throws PageException {
		if(session==null)	{
			checkSessionContext();
			session=scopeContext.getSessionScope(this,DUMMY_BOOL);
		}
		return session;
	}


	public void invalidateUserScopes(boolean migrateSessionData,boolean migrateClientData) throws PageException {
		checkSessionContext();
		scopeContext.invalidateUserScope(this, migrateSessionData, migrateClientData);
	}
    
    private void checkSessionContext() throws ExpressionException {
    	if(!applicationContext.hasName())
			throw new ExpressionException("there is no session context defined for this application","you can define a session context with the tag "+Constants.CFAPP_NAME+"/"+Constants.APP_CFC);
		if(!applicationContext.isSetSessionManagement())
			throw new ExpressionException("session scope is not enabled","you can enable session scope with tag "+Constants.CFAPP_NAME+"/"+Constants.APP_CFC);
	}

    @Override
    public Server serverScope() { 
		//if(!server.isInitalized()) server.initialize(this);
		return server;
	}
	
    public void reset() {
    	server=ScopeContext.getServerScope(this);
	}
    
	@Override
    public Cluster clusterScope() throws PageException {
    	return clusterScope(true);
	}
    
    public Cluster clusterScope(boolean create) throws PageException { 
    	if(cluster==null && create) {
    		cluster=ScopeContext.getClusterScope(config,create);
    		//cluster.initialize(this);
    	}
    	//else if(!cluster.isInitalized()) cluster.initialize(this);
		return cluster;
	}

    @Override
    public Cookie cookieScope() { 
        if(!cookie.isInitalized()) cookie.initialize(this);
        return cookie;
    }
	
    @Override
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
			if(applicationContext==null || !applicationContext.hasName()) 				return null;
			if(!applicationContext.isSetClientManagement())	return null;
			client= scopeContext.getClientScopeEL(this);
		}
		return client;
	}
	
    @Override
    public Object set(Object coll, String key, Object value) throws PageException {
	    return variableUtil.set(this,coll,key,value);
	}

	public Object set(Object coll, Collection.Key key, Object value) throws PageException {
		return variableUtil.set(this,coll,key,value);
	}
	
    @Override
    public Object touch(Object coll, String key) throws PageException {
	    Object o=getCollection(coll,key,null);
	    if(o!=null) return o;
	    return set(coll,key,new StructImpl());
	} 

	@Override
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
    

    
    

    @Override
    public Object getCollection(Object coll, String key) throws PageException {
    	return variableUtil.getCollection(this,coll,key);
	}

	@Override
	public Object getCollection(Object coll, Collection.Key key) throws PageException {
		return variableUtil.getCollection(this,coll,key);
	}
	
    @Override
    public Object getCollection(Object coll, String key, Object defaultValue) {
		return variableUtil.getCollection(this,coll,key,defaultValue);
	}

	@Override
	public Object getCollection(Object coll, Collection.Key key, Object defaultValue) {
		return variableUtil.getCollection(this,coll,key,defaultValue);
	}
	
    @Override
    public Object get(Object coll, String key) throws PageException {
		return variableUtil.get(this,coll,key);
	}

	@Override
	public Object get(Object coll, Collection.Key key) throws PageException {
		return variableUtil.get(this,coll,key);
	}
	
    @Override
    public Reference getReference(Object coll, String key) throws PageException {
		return new VariableReference(coll,key);
	}

	public Reference getReference(Object coll, Collection.Key key) throws PageException {
		return new VariableReference(coll,key);
	}

    @Override
    public Object get(Object coll, String key, Object defaultValue) {
        return variableUtil.get(this,coll,key, defaultValue);
    }

	@Override
	public Object get(Object coll, Collection.Key key, Object defaultValue) {
		return variableUtil.get(this,coll,key, defaultValue);
	}
	
    @Override
    public Object setVariable(String var, Object value) throws PageException {
	    //return new CFMLExprInterpreter().interpretReference(this,new ParserString(var)).set(value);
	    return VariableInterpreter.setVariable(this,var,value);
	}
    
    @Override
    public Object getVariable(String var) throws PageException {
		return VariableInterpreter.getVariable(this,var);
	}
    

    public void param(String type, String name, Object defaultValue,String regex) throws PageException {
    	param(type, name, defaultValue,Double.NaN,Double.NaN,regex,-1);
    }
	public void param(String type, String name, Object defaultValue,double min, double max) throws PageException {
    	param(type, name, defaultValue,min,max,null,-1);
    }

    public void param(String type, String name, Object defaultValue,int maxLength) throws PageException {
    	param(type, name, defaultValue,Double.NaN,Double.NaN,null,maxLength);
    }

    public void param(String type, String name, Object defaultValue) throws PageException {
    	param(type, name, defaultValue,Double.NaN,Double.NaN,null,-1);
    }
	
    private void param(String type, String name, Object defaultValue, double min,double max, String strPattern, int maxLength) throws PageException {

    	// check attributes type
    	if(type==null)type="any";
		else type=type.trim().toLowerCase();

    	// check attributes name
    	if(StringUtil.isEmpty(name))
			throw new ExpressionException("The attribute name is required");
    	
    	Object value=null;
		boolean isNew=false;
		
		// get value
		value=VariableInterpreter.getVariableEL(this,name,NullSupportHelper.NULL());
		if(NullSupportHelper.NULL()==value) {
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
			else if ( type.equals( "int" ) || type.equals( "integer" ) ) {

				if ( !Decision.isInteger( value ) )
					throw new ExpressionException( "The value [" + value + "] is not a valid integer" );

                setVariable( name, value );
			}
			else {
				if(!Decision.isCastableTo(type,value,true,true,maxLength)) {
					if(maxLength>-1 && ("email".equalsIgnoreCase(type) || "url".equalsIgnoreCase(type) || "string".equalsIgnoreCase(type))) {
						StringBuilder msg=new StringBuilder(CasterException.createMessage(value, type));
						msg.append(" with a maximum length of "+maxLength+" characters");
						throw new CasterException(msg.toString());	
					}
					throw new CasterException(value,type);	
				}
				
				setVariable(name,value);
				//REALCAST setVariable(name,Caster.castTo(this,type,value,true));
			}
		}
	    else if(isNew) setVariable(name,value);
	}


    @Override
    public Object removeVariable(String var) throws PageException {
		return VariableInterpreter.removeVariable(this,var);
	}

    /**
     * a variable reference, references to variable, to modifed it, with global effect.
     * @param var variable name to get
     * @return return a variable reference by string syntax ("scopename.key.key" -> "url.name")
     * @throws PageException
     */
    public VariableReference getVariableReference(String var) throws PageException { 
	    return VariableInterpreter.getVariableReference(this,var);
	} 
	
    @Override
    public Object getFunction(Object coll, String key, Object[] args) throws PageException {
        return variableUtil.callFunctionWithoutNamedValues(this,coll,key,args);
	}

	@Override
	public Object getFunction(Object coll, Key key, Object[] args) throws PageException {
		return variableUtil.callFunctionWithoutNamedValues(this,coll,key,args);
	}
	
    @Override
    public Object getFunctionWithNamedValues(Object coll, String key, Object[] args) throws PageException {
		return variableUtil.callFunctionWithNamedValues(this,coll,key,args);
	}

	@Override
	public Object getFunctionWithNamedValues(Object coll, Key key, Object[] args) throws PageException {
		return variableUtil.callFunctionWithNamedValues(this,coll,key,args);
	}

    @Override
    public ConfigWeb getConfig() {
        return config;
    }
    
    @Override
    public Iterator getIterator(String key) throws PageException {
		Object o=VariableInterpreter.getVariable(this,key);
		if(o instanceof Iterator) return (Iterator) o;
		throw new ExpressionException("["+key+"] is not a iterator object");
	}

    @Override
    public Query getQuery(String key) throws PageException {
		Object value=VariableInterpreter.getVariable(this,key);
		if(Decision.isQuery(value)) return Caster.toQuery(value);
    	throw new CasterException(value,Query.class);///("["+key+"] is not a query object, object is from type ");
	}
    
    @Override
    public Query getQuery(Object value) throws PageException {
    	if(Decision.isQuery(value)) return Caster.toQuery(value);
    	value=VariableInterpreter.getVariable(this,Caster.toString(value));
    	if(Decision.isQuery(value)) return Caster.toQuery(value);
    	throw new CasterException(value,Query.class);
	}

	@Override
	public void setAttribute(String name, Object value) {
	    try {
            if(value==null)removeVariable(name);
            else setVariable(name,value);
        } catch (PageException e) {}
	}

	@Override
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

	@Override
	public Object getAttribute(String name) {
		try {
			return getVariable(name);
		} catch (PageException e) {
			return null;
		}
	}

	@Override
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

	@Override
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

	@Override
	public void removeAttribute(String name) {
		setAttribute(name, null);
	}

	@Override
	public void removeAttribute(String name, int scope) {
		setAttribute(name, null,scope);
	}

	@Override
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

	@Override
	public Enumeration getAttributeNamesInScope(int scope) {
		
        switch(scope){
		case javax.servlet.jsp.PageContext.APPLICATION_SCOPE:
			return getServletContext().getAttributeNames();
		case javax.servlet.jsp.PageContext.PAGE_SCOPE:
			return ItAsEnum.toStringEnumeration(variablesScope().keyIterator());
		case javax.servlet.jsp.PageContext.REQUEST_SCOPE:
			return req.getAttributeNames();
		case javax.servlet.jsp.PageContext.SESSION_SCOPE:
			return req.getSession(true).getAttributeNames();
		}
		return null;
	}

	@Override
	public JspWriter getOut() {
		return forceWriter;
	}

	@Override
	public HttpSession getSession() {
		return getHttpServletRequest().getSession();
	}

	@Override
	public Object getPage() {
		return variablesScope();
	}

	@Override
	public ServletRequest getRequest() {
		return getHttpServletRequest();
	}
	
    @Override
    public HttpServletRequest getHttpServletRequest() {
		return req;
	}

	@Override
	public ServletResponse getResponse() {
		return rsp;
	}

    @Override
    public HttpServletResponse getHttpServletResponse() {
		return rsp;
	}
    
    public OutputStream getResponseStream() throws IOException {
    	return getRootOut().getResponseStream();
	}

	@Override
	public Exception getException() {
		// TODO impl
		return exception;
	}

	@Override
	public ServletConfig getServletConfig() {
		return config;
	}

	@Override
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
	
	
	@Override
	public void handlePageException(PageException pe) {
		if(!Abort.isSilentAbort(pe)) {
			
			String charEnc = ReqRspUtil.getCharacterEncoding(this,rsp);
	        if(StringUtil.isEmpty(charEnc,true)) {
				rsp.setContentType("text/html");
	        }
	        else {
	        	rsp.setContentType("text/html; charset=" + charEnc);
	        }
	        rsp.setHeader("exception-message", pe.getMessage());
	        //rsp.setHeader("exception-detail", pe.getDetail());
	        
			int statusCode=getStatusCode(pe);
			
			if(getConfig().getErrorStatusCode())rsp.setStatus(statusCode);
			
			ErrorPage ep=errorPagePool.getErrorPage(pe,ErrorPageImpl.TYPE_EXCEPTION);
			
			ExceptionHandler.printStackTrace(this,pe);
			ExceptionHandler.log(getConfig(),pe);

			// error page exception
			if(ep!=null) {
				try {
					Struct sct=pe.getErrorBlock(this,ep);
					variablesScope().setEL(KeyConstants._error,sct);
					variablesScope().setEL(KeyConstants._cferror,sct);
					
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
						java.util.Iterator<Entry<Key, Object>> it = sct.entryIterator();
						Entry<Key, Object> e;
						String v;
						while(it.hasNext()){
							e = it.next();
							v=Caster.toString(e.getValue(),null);
							if(v!=null)content=repl(content, e.getKey().getString(), v);
						}
						
						write(content);
						return;
					} catch (Throwable t) {
						pe=Caster.toPageException(t);
					}
				}
				else pe=new ApplicationException("The error page template for type request only works if the actual source file also exists. If the exception file is in an Railo archive (.rc/.rcs), you need to use type exception instead.");
			}
			
			try {

				String template=getConfig().getErrorTemplate(statusCode);
				if(!StringUtil.isEmpty(template)) {
					try {
						Struct catchBlock=pe.getCatchBlock(getConfig());
						variablesScope().setEL(KeyConstants._cfcatch,catchBlock);
						variablesScope().setEL(KeyConstants._catch,catchBlock);
						doInclude(template);
					    return;
			        } 
					catch (PageException e) {
						pe=e;
					}
				}
				if(!Abort.isSilentAbort(pe))forceWrite(getConfig().getDefaultDumpWriter(DumpWriter.DEFAULT_RICH).toString(this,pe.toDumpData(this, 9999,DumpUtil.toDumpProperties()),true));
			} 
			catch (Exception e) {}
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


	@Override
	public void handlePageException(Exception e) {
		handlePageException(Caster.toPageException(e));		
	}

	@Override
	public void handlePageException(Throwable t) {
        handlePageException(Caster.toPageException(t));
	}

    @Override
    public void setHeader(String name, String value) {
		rsp.setHeader(name,value);
	}

	@Override
	public BodyContent pushBody() {
        forceWriter=bodyContentStack.push();
        if(enablecfoutputonly>0 && outputState==0) {
            writer=devNull;
        }
        else writer=forceWriter;
        return (BodyContent)forceWriter;
	}

	@Override
	public JspWriter popBody() {
        forceWriter=bodyContentStack.pop();
        if(enablecfoutputonly>0 && outputState==0) {
            writer=devNull;
        }
        else writer=forceWriter;
        return forceWriter;
	}

    @Override
    public void outputStart() {
		outputState++;
        if(enablecfoutputonly>0 && outputState==1)writer=forceWriter;
		//if(enablecfoutputonly && outputState>0) unsetDevNull();
	}

    @Override
    public void outputEnd() {
		outputState--;
		if(enablecfoutputonly>0 && outputState==0)writer=devNull;
	}

    @Override
    public void setCFOutputOnly(boolean boolEnablecfoutputonly) {
        if(boolEnablecfoutputonly)this.enablecfoutputonly++;
        else if(this.enablecfoutputonly>0)this.enablecfoutputonly--;
        setCFOutputOnly(enablecfoutputonly);
        //if(!boolEnablecfoutputonly)setCFOutputOnly(enablecfoutputonly=0);
    }

    @Override
    public void setCFOutputOnly(short enablecfoutputonly) {
        this.enablecfoutputonly=enablecfoutputonly;
        if(enablecfoutputonly>0) {
            if(outputState==0) writer=devNull;
        }
        else {
            writer=forceWriter;
        }
    }

    @Override
    public boolean setSilent() {
        boolean before=bodyContentStack.getDevNull();
		bodyContentStack.setDevNull(true);
		
        forceWriter = bodyContentStack.getWriter();
        writer=forceWriter;
        return before;
	}
	
    @Override
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
    

    @Override
    public Debugger getDebugger() {
		return debugger;
	}
    
    @Override
    public void executeRest(String realPath, boolean throwExcpetion) throws PageException  {
    	ApplicationListener listener=null;//config.get ApplicationListener();
	    try{
    	String pathInfo = req.getPathInfo();
    	
    	// charset
    	try{
    		String charset=HTTPUtil.splitMimeTypeAndCharset(req.getContentType(),new String[]{"",""})[1];
    	if(StringUtil.isEmpty(charset))charset=ThreadLocalPageContext.getConfig().getWebCharset();
	    	java.net.URL reqURL = new java.net.URL(req.getRequestURL().toString());
	    	String path=ReqRspUtil.decode(reqURL.getPath(),charset,true);
	    	String srvPath=req.getServletPath();
	    	if(path.startsWith(srvPath)) {
	    		pathInfo=path.substring(srvPath.length());
	    	}
    	}
    	catch (Exception e){}
    	
    	
    	// Service mapping
    	if(StringUtil.isEmpty(pathInfo) || pathInfo.equals("/")) {// ToDo
    		// list available services (if enabled in admin)
    		if(config.getRestList()) {
	    		try {
					HttpServletRequest _req = getHttpServletRequest();
	    			write("Available sevice mappings are:<ul>");
	    			railo.runtime.rest.Mapping[] mappings = config.getRestMappings();
	    			railo.runtime.rest.Mapping _mapping;
					String path;
					for(int i=0;i<mappings.length;i++){
						_mapping=mappings[i];
						Resource p = _mapping.getPhysical();
						path=_req.getContextPath()+ReqRspUtil.getScriptName(_req)+_mapping.getVirtual();
						write("<li "+(p==null || !p.isDirectory()?" style=\"color:red\"":"")+">"+path+"</li>");
						
						
					}
					write("</ul>");
					
				} catch (IOException e) {
					throw Caster.toPageException(e);
				}
    		}
    		else 
    			RestUtil.setStatus(this, 404, null);
			return;
    	}	
    	
    	// check for matrix
    	int index;
    	String entry;
    	Struct matrix=new StructImpl();
    	while((index=pathInfo.lastIndexOf(';'))!=-1){
    		entry=pathInfo.substring(index+1);
    		pathInfo=pathInfo.substring(0,index);
    		if(StringUtil.isEmpty(entry,true)) continue;
    		
    		index=entry.indexOf('=');
    		if(index!=-1)matrix.setEL(entry.substring(0,index).trim(), entry.substring(index+1).trim());
    		else matrix.setEL(entry.trim(), "");
    	}
    	
    	// get accept
    	List<MimeType> accept = ReqRspUtil.getAccept(this);
    	MimeType contentType = ReqRspUtil.getContentType(this);
    	
    	// check for format extension
    	//int format = getApplicationContext().getRestSettings().getReturnFormat();
    	int format;
    	boolean hasFormatExtension=false;
    	if(StringUtil.endsWithIgnoreCase(pathInfo, ".json")) {
    		pathInfo=pathInfo.substring(0,pathInfo.length()-5);
    		format = UDF.RETURN_FORMAT_JSON;
    		accept.clear();
    		accept.add(MimeType.APPLICATION_JSON);
    		hasFormatExtension=true;
    	}
    	else if(StringUtil.endsWithIgnoreCase(pathInfo, ".wddx")) {
    		pathInfo=pathInfo.substring(0,pathInfo.length()-5);
    		format = UDF.RETURN_FORMAT_WDDX;
    		accept.clear();
    		accept.add(MimeType.APPLICATION_WDDX);
    		hasFormatExtension=true;
    	}
    	else if(StringUtil.endsWithIgnoreCase(pathInfo, ".cfml")) {
    		pathInfo=pathInfo.substring(0,pathInfo.length()-5);
    		format = UDF.RETURN_FORMAT_SERIALIZE;
    		accept.clear();
    		accept.add(MimeType.APPLICATION_CFML);
    		hasFormatExtension=true;
    	}
    	else if(StringUtil.endsWithIgnoreCase(pathInfo, ".serialize")) {
    		pathInfo=pathInfo.substring(0,pathInfo.length()-10);
    		format = UDF.RETURN_FORMAT_SERIALIZE;
    		accept.clear();
    		accept.add(MimeType.APPLICATION_CFML);
    		hasFormatExtension=true;
    	}
    	else if(StringUtil.endsWithIgnoreCase(pathInfo, ".xml")) {
    		pathInfo=pathInfo.substring(0,pathInfo.length()-4);
    		format = UDF.RETURN_FORMAT_XML;
    		accept.clear();
    		accept.add(MimeType.APPLICATION_XML);
    		hasFormatExtension=true;
    	}
    	else if(StringUtil.endsWithIgnoreCase(pathInfo, ".java")) {
    		pathInfo=pathInfo.substring(0,pathInfo.length()-5);
    		format = UDFPlus.RETURN_FORMAT_JAVA;
    		accept.clear();
    		accept.add(MimeType.APPLICATION_JAVA);
    		hasFormatExtension=true;
    	}
    	else {
    		format = getApplicationContext().getRestSettings().getReturnFormat();
    		//MimeType mt=MimeType.toMimetype(format);
    		//if(mt!=null)accept.add(mt);
    	}
    	
    	if(accept.size()==0) accept.add(MimeType.ALL);
    	
    	// loop all mappings
    	//railo.runtime.rest.Result result = null;//config.getRestSource(pathInfo, null);
    	RestRequestListener rl=null;
    	railo.runtime.rest.Mapping[] restMappings = config.getRestMappings();
    	railo.runtime.rest.Mapping m,mapping=null,defaultMapping=null;
    	//String callerPath=null;
    	if(restMappings!=null)for(int i=0;i<restMappings.length;i++) {
            m = restMappings[i];
            if(m.isDefault())defaultMapping=m;
            if(pathInfo.startsWith(m.getVirtualWithSlash(),0) && m.getPhysical()!=null) {
            	mapping=m;
            	//result = m.getResult(this,callerPath=pathInfo.substring(m.getVirtual().length()),format,matrix,null);
            	rl=new RestRequestListener(m,pathInfo.substring(m.getVirtual().length()),matrix,format,hasFormatExtension,accept,contentType,null);
            	break;
            }
        }
    	
    	// default mapping
    	if(mapping==null && defaultMapping!=null && defaultMapping.getPhysical()!=null) {
    		mapping=defaultMapping;
            //result = mapping.getResult(this,callerPath=pathInfo,format,matrix,null);
        	rl=new RestRequestListener(mapping,pathInfo,matrix,format,hasFormatExtension,accept,contentType,null);
    	}
    	
    	
    	//base = PageSourceImpl.best(config.getPageSources(this,null,realPath,true,false,true));
    	
    	
    	if(mapping==null || mapping.getPhysical()==null){
    		RestUtil.setStatus(this,404,"no rest service for ["+pathInfo+"] found");
    	}
    	else {
    		base=config.toPageSource(null, mapping.getPhysical(), null);
    		listener=((MappingImpl)base.getMapping()).getApplicationListener();
    	    listener.onRequest(this, base,rl);
    	}
    	
    	
    	
    	}
	    catch(Throwable t) {
	    	PageException pe = Caster.toPageException(t);
	    	if(!Abort.isSilentAbort(pe)){
	    		log(true);
	    		if(fdEnabled){
	        		FDSignal.signal(pe, false);
	        	}
	    		if(listener==null) {
	    			if(base==null)listener=config.getApplicationListener();
	    			else listener=((MappingImpl)base.getMapping()).getApplicationListener();
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
            base=null;
	    }
    }

	@Override
    public void execute(String realPath, boolean throwExcpetion) throws PageException  {
    	execute(realPath, throwExcpetion, true);
    }
    public void execute(String realPath, boolean throwExcpetion, boolean onlyTopLevel) throws PageException  {
    	//SystemOut.printDate(config.getOutWriter(),"Call:"+realPath+" (id:"+getId()+";running-requests:"+config.getThreadQueue().size()+";)");
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
	    	if(base==null) base=PageSourceImpl.best(config.getPageSources(this,null,realPath,onlyTopLevel,false,true));
	    }
	    else base=PageSourceImpl.best(config.getPageSources(this,null,realPath,onlyTopLevel,false,true));
	    ApplicationListener listener=gatewayContext?config.getApplicationListener():((MappingImpl)base.getMapping()).getApplicationListener();
	    
	    
	    try {
	    	listener.onRequest(this,base,null);
	    	log(false);
	    }
	    catch(Throwable t) {
	    	PageException pe = Caster.toPageException(t);
	    	if(!Abort.isSilentAbort(pe)){
	    		this.pe=pe;
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
		if(!isGatewayContext() && config.isMonitoringEnabled()) {
            RequestMonitor[] monitors = config.getRequestMonitors();
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



	@Override
	public void include(String realPath) throws ServletException,IOException  {
		HTTPUtil.include(this, realPath);
	}
	

	@Override
	public void forward(String realPath) throws ServletException, IOException {
		HTTPUtil.forward(this, realPath);
	}

	public void include(PageSource ps) throws ServletException  {
		try {
			doInclude(ps);
		} catch (PageException pe) {
			throw new PageServletException(pe);
		}
	}

    @Override
    public void clear() {
		try {
			//print.o(getOut().getClass().getName());
			getOut().clear();
		} catch (IOException e) {}
	}
	
    @Override
    public long getRequestTimeout() {
		if(requestTimeout==-1)
			requestTimeout=config.getRequestTimeout().getMillis();
		return requestTimeout;
	}
	
    @Override
    public void setRequestTimeout(long requestTimeout) {
		this.requestTimeout = requestTimeout;
	}

    @Override
    public String getCFID() {
		if(cfid==null) initIdAndToken();
		return cfid;
	}

    @Override
    public String getCFToken() {
		if(cftoken==null) initIdAndToken();
		return cftoken;
	}

    @Override
    public String getURLToken() {
	    if(getConfig().getSessionType()==Config.SESSION_TYPE_J2EE) {
	    	HttpSession s = getSession();
		    return "CFID="+getCFID()+"&CFTOKEN="+getCFToken()+"&jsessionid="+(s!=null?getSession().getId():"");
		}
		return "CFID="+getCFID()+"&CFTOKEN="+getCFToken();
	}
    
    @Override
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
        Object oCfid = urlScope().get(KeyConstants._cfid,null);
        Object oCftoken = urlScope().get(KeyConstants._cftoken,null);
        // Cookie
        if((oCfid==null || !Decision.isGUIdSimple(oCfid)) || oCftoken==null) {
            setCookie=false;
            oCfid = cookieScope().get(KeyConstants._cfid,null);
            oCftoken = cookieScope().get(KeyConstants._cftoken,null);
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
        
        if(setCookie && applicationContext.isSetClientCookies())
	        setClientCookies();
    }
    

    public void resetIdAndToken() {
        cfid=ScopeContext.getNewCFId();
        cftoken=ScopeContext.getNewCFToken();

        if(applicationContext.isSetClientCookies())
	        setClientCookies();
    }


	private void setClientCookies() {

		String domain = PageContextUtil.getCookieDomain( this );
		cookieScope().setCookieEL( KeyConstants._cfid, cfid, CookieImpl.NEVER,false, "/", domain, true, true, false );
		cookieScope().setCookieEL( KeyConstants._cftoken, cftoken, CookieImpl.NEVER,false, "/", domain, true, true, false );
	}
    

    @Override
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
    public JspWriter getRootWriter() {
		return bodyContentStack.getBase();
	}

    @Override
    public void setPsq(boolean psq) {
		this.psq=psq;
	}
	
    @Override
    public boolean getPsq() {
		return psq;
	}

    @Override
    public Locale getLocale() {
    	Locale l = ((ApplicationContextPro)getApplicationContext()).getLocale();
    	if(l!=null) return l;
    	if(locale!=null) return locale;
    	return config.getLocale();
	}
	
    @Override
    public void setLocale(Locale locale) {
		
		((ApplicationContextPro)getApplicationContext()).setLocale(locale);
    	this.locale=locale;
        HttpServletResponse rsp = getHttpServletResponse();
        
        String charEnc = ReqRspUtil.getCharacterEncoding(this,rsp);
        rsp.setLocale(locale);
        if(charEnc.equalsIgnoreCase("UTF-8")) {
        	rsp.setContentType("text/html; charset=UTF-8");
        }
        else if(!charEnc.equalsIgnoreCase(ReqRspUtil.getCharacterEncoding(this,rsp))) {
                rsp.setContentType("text/html; charset=" + charEnc);
        }
	}
    

    @Override
    public void setLocale(String strLocale) throws ExpressionException {
		setLocale(Caster.toLocale(strLocale));
	}

    @Override
    public void setErrorPage(ErrorPage ep) {
		errorPagePool.setErrorPage(ep);
	}
	
    @Override
    public Tag use(String tagClassName) throws PageException {

        parentTag=currentTag;
		currentTag= tagHandlerPool.use(tagClassName);
        if(currentTag==parentTag) throw new ApplicationException("");
        currentTag.setPageContext(this);
        currentTag.setParent(parentTag);
        return currentTag;
	}
    
    @Override
    public Tag use(Class clazz) throws PageException {
        return use(clazz.getName());
	}
	
    @Override
    public void reuse(Tag tag) throws PageException {
        currentTag=tag.getParent();
        tagHandlerPool.reuse(tag);
	}

    @Override
    public QueryCache getQueryCache() {
        return queryCache;
    }
    
    @Override
    public void initBody(BodyTag bodyTag, int state) throws JspException {
        if (state != Tag.EVAL_BODY_INCLUDE) {
            bodyTag.setBodyContent(pushBody());
            bodyTag.doInitBody();
        }
    }
    
    @Override
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
    
    @Override
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

    @Override
    public Component getActiveComponent() {
        return activeComponent;
    }
    
    @Override
    public Credential getRemoteUser() throws PageException {
        if(remoteUser==null) {
        	Key name = KeyImpl.init(Login.getApplicationName(applicationContext));
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
    
    @Override
    public void clearRemoteUser() {
        if(remoteUser!=null)remoteUser=null;
        String name=Login.getApplicationName(applicationContext);
	    
        cookieScope().removeEL(KeyImpl.init(name));
        try {
			sessionScope().removeEL(KeyImpl.init(name));
		} catch (PageException e) {}
        
    }
    
    @Override
    public void setRemoteUser(Credential remoteUser) {
        this.remoteUser = remoteUser;
    }
    
    @Override
    public VariableUtil getVariableUtil() {
        return variableUtil;
    }

    @Override
    public void throwCatch() throws PageException {
        if(exception!=null) throw exception;
        throw new ApplicationException("invalid context for tag/script expression rethow");
    }

    @Override
    public PageException setCatch(Throwable t) {
    	if(t==null) {
    		exception=null;
    		undefinedScope().removeEL(KeyConstants._cfcatch);
    	}
    	else {
    		exception = Caster.toPageException(t);
    		undefinedScope().setEL(KeyConstants._cfcatch,exception.getCatchBlock(config));
    		if(!gatewayContext && config.debug() && config.hasDebugOptions(ConfigImpl.DEBUG_EXCEPTION)) debugger.addException(config,exception);
    	}
    	return exception;
    }
    
    public void setCatch(PageException pe) {
    	exception = pe;
    	if(pe==null) {
    		undefinedScope().removeEL(KeyConstants._cfcatch);
    	}
    	else {
    		undefinedScope().setEL(KeyConstants._cfcatch,pe.getCatchBlock(config));
    		if(!gatewayContext && config.debug() && config.hasDebugOptions(ConfigImpl.DEBUG_EXCEPTION)) debugger.addException(config,exception);
    	}
    }
    
    public void setCatch(PageException pe,boolean caught, boolean store) {
		if(fdEnabled){
    		FDSignal.signal(pe, caught);
    	}
    	exception = pe;
    	if(store){
	    	if(pe==null) {
	    		undefinedScope().removeEL(KeyConstants._cfcatch);
	    	}
	    	else {
	    		undefinedScope().setEL(KeyConstants._cfcatch,pe.getCatchBlock(config));
	    		if(!gatewayContext && config.debug() && config.hasDebugOptions(ConfigImpl.DEBUG_EXCEPTION)) debugger.addException(config,exception);
	    	}
    	}
    }
    
    /**
     * @return return current catch
     */
    public PageException getCatch() {
    	return exception;
    }
    
    @Override
    public void clearCatch() {
        exception = null;
    	undefinedScope().removeEL(KeyConstants._cfcatch);
    }

    @Override
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

    @Override
    public void removeLastPageSource(boolean alsoInclude) {
    	if(!pathList.isEmpty())pathList.removeLast();
        if(alsoInclude && !includePathList.isEmpty()) 
            includePathList.removeLast();
    }


    public UDF[] getUDFs() {
    	return udfs.toArray(new UDF[udfs.size()]);
    }
    
    public void addUDF(UDF udf) {
    	udfs.add(udf);
    }

    public void removeUDF() {
    	if(!udfs.isEmpty())udfs.pop();
    }

    @Override
    public FTPPool getFTPPool() {
        return ftpPool;
    }

    /* *
     * @return Returns the manager.
     * /
    public DataSourceManager getManager() {
        return manager;
    }*/
    
    @Override
    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
    	
        session=null;
        application=null;
        client=null;
        this.applicationContext = applicationContext;
        
        int scriptProtect = applicationContext.getScriptProtect();
        
        // ScriptProtecting
        if(config.mergeFormAndURL()) {
        	form.setScriptProtecting(applicationContext,
        			(scriptProtect&ApplicationContext.SCRIPT_PROTECT_FORM)>0 
        			|| 
        			(scriptProtect&ApplicationContext.SCRIPT_PROTECT_URL)>0);
        }
        else {
            form.setScriptProtecting(applicationContext,(scriptProtect&ApplicationContext.SCRIPT_PROTECT_FORM)>0);
            url.setScriptProtecting(applicationContext,(scriptProtect&ApplicationContext.SCRIPT_PROTECT_URL)>0);
        }
        cookie.setScriptProtecting(applicationContext,(scriptProtect&ApplicationContext.SCRIPT_PROTECT_COOKIE)>0);
        cgi.setScriptProtecting(applicationContext,(scriptProtect&ApplicationContext.SCRIPT_PROTECT_CGI)>0);
        undefined.reinitialize(this);
    }
    
    /**
     * @return return  value of method "onApplicationStart" or true
     * @throws PageException 
     */
    public boolean initApplicationContext(ApplicationListener listener) throws PageException {
    	boolean initSession=false;
    	//AppListenerSupport listener = (AppListenerSupport) config.get ApplicationListener();
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

    
    
    
    @Override
    public Tag getCurrentTag() {
        return currentTag;
    }

    @Override
    public long getStartTime() {
        return startTime;
    }
    
    @Override
    public Thread getThread() {
        return thread;
    }
    


	public void setThread(Thread thread) {
		this.thread=thread;
	}

	// FUTURE add as long
    @Override
    public int getExecutionTime() {
        return (int)executionTime;
    }

    @Override
    public void setExecutionTime(int executionTime) {
        this.executionTime = executionTime;
    }

    @Override
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

    @Override
    public void compile(String realPath) throws PageException {
    	SystemOut.printDate("method PageContext.compile(String) should no longer be used!");
    	compile(PageSourceImpl.best(getRelativePageSources(realPath)));
    }
    
    public HttpServlet getServlet() {
        return servlet;
    }

    @Override
    public railo.runtime.Component loadComponent(String compPath) throws PageException {
    	return ComponentLoader.loadComponent(this,null,compPath,null,null);
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
	
	@Override
	public DataSourceManager getDataSourceManager() {
		return manager;
	}

	@Override
	public Object evaluate(String expression) throws PageException {
		return new CFMLExpressionInterpreter().interpret(this,expression);
	}
	
	@Override
	public String serialize(Object expression) throws PageException {
		return Serialize.call(this, expression);
	}

	/**
	 * @return the activeUDF
	 */
	public UDF getActiveUDF() {
		return activeUDF;
	}
	public Collection.Key getActiveUDFCalledName() {
		return activeUDFCalledName;
	}
	public void setActiveUDFCalledName(Collection.Key activeUDFCalledName) {
		this.activeUDFCalledName=activeUDFCalledName;
	}

	/**
	 * @param activeUDF the activeUDF to set
	 */
	public void setActiveUDF(UDF activeUDF) {
		this.activeUDF = activeUDF;
	}

	@Override
	public CFMLFactory getCFMLFactory() {
		return config.getFactory();
	}

	@Override
	public PageContext getParentPageContext() {
		return parent;
	}


	@Override
	public String[] getThreadScopeNames() {
		if(threads==null)return new String[0];
		return CollectionUtil.keysAsString(threads);
		//Set ks = threads.keySet();
		//return (String[]) ks.toArray(new String[ks.size()]);
	}
	
	@Override
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
		if(name.equalsIgnoreCase(KeyConstants._cfthread)) return threads;
		return threads.get(name,defaultValue);
	}
	
	public Object getThreadScope(String name,Object defaultValue) {
		if(threads==null)threads=new StructImpl();
		if(name.equalsIgnoreCase(KeyConstants._cfthread.getLowerString())) return threads;
		return threads.get(KeyImpl.init(name),defaultValue);
	}

	@Override
	public void setThreadScope(String name,Threads ct) {
		hasFamily=true;
		if(threads==null)	threads=new StructImpl();
		threads.setEL(KeyImpl.init(name), ct);
	}
	
	public void setThreadScope(Collection.Key name,Threads ct) {
		hasFamily=true;
		if(threads==null)	threads=new StructImpl();
		threads.setEL(name, ct);
	}

	@Override
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

	@Override
	public TimeZone getTimeZone() {
		TimeZone tz = ((ApplicationContextPro)getApplicationContext()).getTimeZone();
		if(tz!=null) return tz;
		if(timeZone!=null) return timeZone;
		return config.getTimeZone();
	}
	
	@Override
	public void setTimeZone(TimeZone timeZone) {
		((ApplicationContextPro)getApplicationContext()).setTimeZone(timeZone);
		this.timeZone=timeZone;
	}


	/**
	 * @return the requestId
	 */
	public int getRequestId() {
		return requestId;
	}

	private Set<String> pagesUsed=new HashSet<String>();

	private Stack<ActiveQuery> activeQueries=new Stack<ActiveQuery>();
	private Stack<ActiveLock> activeLocks=new Stack<ActiveLock>();
	
	



	public boolean isTrusted(Page page) {
		if(page==null)return false;
		
		short it = ((MappingImpl)page.getPageSource().getMapping()).getInspectTemplate();
		if(it==ConfigImpl.INSPECT_NEVER)return true;
		if(it==ConfigImpl.INSPECT_ALWAYS)return false;
		
		return pagesUsed.contains(""+page.hashCode());
	}
	
	public void setPageUsed(Page page) {
		pagesUsed.add(""+page.hashCode());
	}

	@Override
	public void exeLogStart(int position,String id){
		if(execLog!=null)execLog.start(position, id);
	}
	
	@Override
	public void exeLogEnd(int position,String id){
		if(execLog!=null)execLog.end(position, id);
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

	public ClassLoader getClassLoader() throws IOException {
		return getResourceClassLoader();
	}
	
	public ClassLoader getClassLoader(Resource[] reses) throws IOException{
		return getResourceClassLoader().getCustomResourceClassLoader(reses);
	}
	
	private ResourceClassLoader getResourceClassLoader() throws IOException {
		JavaSettingsImpl js = (JavaSettingsImpl) applicationContext.getJavaSettings();
		if(js!=null) {
			return config.getResourceClassLoader().getCustomResourceClassLoader(js.getResourcesTranslated());
		}
		return config.getResourceClassLoader();
	}

	public ClassLoader getRPCClassLoader(boolean reload) throws IOException {
		JavaSettingsImpl js = (JavaSettingsImpl) applicationContext.getJavaSettings();
		if(js!=null) {
			return ((PhysicalClassLoader)config.getRPCClassLoader(reload)).getCustomClassLoader(js.getResourcesTranslated(),reload);
		}
		return config.getRPCClassLoader(reload);
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
	
// FUTURE add to PageContext
	public DataSource getDataSource(String datasource) throws PageException {
		DataSource ds = ((ApplicationContextPro)getApplicationContext()).getDataSource(datasource,null);
		if(ds!=null) return ds;
		ds=getConfig().getDataSource(datasource,null);
		if(ds!=null) return ds;
		
		throw DatabaseException.notFoundException(this, datasource);
	}
		
// FUTURE add to PageContext
	public DataSource getDataSource(String datasource, DataSource defaultValue) {
		DataSource ds = ((ApplicationContextPro)getApplicationContext()).getDataSource(datasource,null);
		if(ds==null) ds=getConfig().getDataSource(datasource,defaultValue);
		return ds;
	}

	public void setActiveQuery(ActiveQuery activeQuery) {
		this.activeQueries.add(activeQuery);
	}
	
	public ActiveQuery[] getActiveQueries() {
		return activeQueries.toArray(new ActiveQuery[activeQueries.size()]);
	}

	public ActiveQuery releaseActiveQuery() {
		return activeQueries.pop();
	}

	public void setActiveLock(ActiveLock activeLock) {
		this.activeLocks.add(activeLock);
	}
	
	public ActiveLock[] getActiveLocks() {
		return activeLocks.toArray(new ActiveLock[activeLocks.size()]);
	}

	public ActiveLock releaseActiveLock() {
		return activeLocks.pop();
	}

	public PageException getPageException() {
		return pe;
	}
}