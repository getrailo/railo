package railo.runtime.listener;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;

import railo.commons.io.DevNullOutputStream;
import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.CFMLFactory;
import railo.runtime.Component;
import railo.runtime.ComponentImpl;
import railo.runtime.ComponentPage;
import railo.runtime.PageContext;
import railo.runtime.PageContextImpl;
import railo.runtime.PageSource;
import railo.runtime.component.ComponentLoader;
import railo.runtime.component.Member;
import railo.runtime.config.ConfigImpl;
import railo.runtime.exp.Abort;
import railo.runtime.exp.MissingIncludeException;
import railo.runtime.exp.PageException;
import railo.runtime.interpreter.JSONExpressionInterpreter;
import railo.runtime.net.http.HttpServletRequestDummy;
import railo.runtime.net.http.HttpServletResponseDummy;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.orm.ORMConfiguration;
import railo.runtime.orm.ORMUtil;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.Collection;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.util.ArrayUtil;
import railo.runtime.type.util.StructUtil;
import railo.runtime.util.ApplicationContextImpl;

public class ModernAppListener extends AppListenerSupport {



	private static final Collection.Key NAME = KeyImpl.getInstance("name");
	private static final Collection.Key APPLICATION_TIMEOUT = KeyImpl.getInstance("applicationTimeout");
	private static final Collection.Key CLIENT_MANAGEMENT = KeyImpl.getInstance("clientManagement");
	private static final Collection.Key CLIENT_STORAGE = KeyImpl.getInstance("clientStorage");
	private static final Collection.Key SESSION_STORAGE = KeyImpl.getInstance("sessionStorage");
	private static final Collection.Key LOGIN_STORAGE = KeyImpl.getInstance("loginStorage");
	private static final Collection.Key SESSION_MANAGEMENT = KeyImpl.getInstance("sessionManagement");
	private static final Collection.Key SESSION_TIMEOUT = KeyImpl.getInstance("sessionTimeout");
	private static final Collection.Key CLIENT_TIMEOUT = KeyImpl.getInstance("clientTimeout");
	private static final Collection.Key SET_CLIENT_COOKIES = KeyImpl.getInstance("setClientCookies");
	private static final Collection.Key SET_DOMAIN_COOKIES = KeyImpl.getInstance("setDomainCookies");
	private static final Collection.Key SCRIPT_PROTECT = KeyImpl.getInstance("scriptProtect");
	private static final Collection.Key MAPPINGS = KeyImpl.getInstance("mappings");
	private static final Collection.Key CUSTOM_TAG_PATHS = KeyImpl.getInstance("customtagpaths");
	private static final Collection.Key SECURE_JSON_PREFIX = KeyImpl.getInstance("secureJsonPrefix");
	private static final Collection.Key SECURE_JSON = KeyImpl.getInstance("secureJson");
	private static final Collection.Key LOCAL_MODE = KeyImpl.getInstance("localMode");
	

	private static final Collection.Key ON_REQUEST_START = KeyImpl.getInstance("onRequestStart");
	private static final Collection.Key ON_CFCREQUEST = KeyImpl.getInstance("onCFCRequest");
	private static final Collection.Key ON_REQUEST = KeyImpl.getInstance("onRequest");
	private static final Collection.Key ON_REQUEST_END = KeyImpl.getInstance("onRequestEnd");
	private static final Collection.Key ON_APPLICATION_START = KeyImpl.getInstance("onApplicationStart");
	private static final Collection.Key ON_APPLICATION_END = KeyImpl.getInstance("onApplicationEnd");
	private static final Collection.Key ON_SESSION_START = KeyImpl.getInstance("onSessionStart");
	private static final Collection.Key ON_SESSION_END = KeyImpl.getInstance("onSessionEnd");
	private static final Collection.Key ON_DEBUG = KeyImpl.getInstance("onDebug");
	private static final Collection.Key ON_ERROR = KeyImpl.getInstance("onError");
	private static final Collection.Key ON_MISSING_TEMPLATE = KeyImpl.getInstance("onMissingTemplate");
	private static final Collection.Key DEFAULT_DATA_SOURCE = KeyImpl.getInstance("defaultdatasource");
	private static final Collection.Key DATA_SOURCE = KeyImpl.getInstance("datasource");
	private static final Collection.Key ORM_ENABLED = KeyImpl.getInstance("ormenabled");
	private static final Collection.Key ORM_SETTINGS = KeyImpl.getInstance("ormsettings");

	private static final Collection.Key S3 = KeyImpl.getInstance("s3");
	private static final Collection.Key ACCESS_KEY_ID = KeyImpl.getInstance("accessKeyId");
	private static final Collection.Key AWS_SECRET_KEY = KeyImpl.getInstance("awsSecretKey");
	private static final Collection.Key DEFAULT_LOCATION = KeyImpl.getInstance("defaultLocation");
	private static final Collection.Key HOST = KeyImpl.getInstance("host");
	private static final Collection.Key SERVER = KeyImpl.getInstance("server");
	
	
	//private ComponentImpl app;
	private Map apps=new HashMap();
	protected int mode=MODE_CURRENT2ROOT;
	private String type;
	private Boolean hasOnSessionStart;
	
	//private ApplicationContextImpl appContext;
	//private long cfcCompileTime;


	/**
	 *
	 * @throws PageException 
	 * @see railo.runtime.listener.ApplicationListener#onRequest(railo.runtime.PageContext, railo.runtime.PageSource)
	 */
	public void onRequest(PageContext pc, PageSource requestedPage) throws PageException {
		// on requestStart
		PageSource appPS=//pc.isCFCRequest()?null:
			AppListenerUtil.getApplicationPageSource(pc,requestedPage,"Application.cfc",mode);
		
		_onRequest(pc, requestedPage, appPS);
	}
	
	protected void _onRequest(PageContext pc, PageSource requestedPage,PageSource appPS) throws PageException {
		PageContextImpl pci = (PageContextImpl)pc;
		if(appPS!=null) {
			String callPath=appPS.getComponentName();
			
			
			ComponentImpl app = ComponentLoader.loadComponentImpl(pci,null,appPS, callPath, false,true);
			
			String targetPage=requestedPage.getFullRealpath();
			// init
			initApplicationContext(pci,app);
	    	
			
			apps.put(pc.getApplicationContext().getName(), app);

			if(!pci.initApplicationContext()) return;

			
			// onRequestStart
			if(app.contains(pc,ON_REQUEST_START)) {
				Object rtn=call(app,pci, ON_REQUEST_START, new Object[]{targetPage});
				if(!Caster.toBooleanValue(rtn,true))
					return;
			}
	    	
			// onRequest
			boolean isCFC=ResourceUtil.getExtension(targetPage,"").equalsIgnoreCase(pc.getConfig().getCFCExtension());
			Object method;
			if(isCFC && app.contains(pc,ON_CFCREQUEST) && (method=pc.urlFormScope().get(ComponentPage.METHOD,null))!=null) {
				
				Struct url = StructUtil.duplicate(pc.urlFormScope(),true);
		        
		        url.removeEL(ComponentPage.FIELDNAMES);
		        url.removeEL(ComponentPage.METHOD);
		        Object args=url.get(ComponentPage.ARGUMENT_COLLECTION,null);
		        Object returnFormat=url.removeEL(ComponentPage.RETURN_FORMAT);
		        Object queryFormat=url.removeEL(ComponentPage.QUERY_FORMAT);
		        
		        if(args==null){
		        	args=pc.getHttpServletRequest().getAttribute("argumentCollection");
		        }
		        
		        if(args instanceof String){
		        	args=new JSONExpressionInterpreter().interpret(pc, (String)args);
		        }
		        
		        if(args!=null) {
		        	if(Decision.isCastableToStruct(args)){
			        	Struct sct = Caster.toStruct(args,false);
			        	Key[] keys = url.keys();
			        	for(int i=0;i<keys.length;i++){
			        		sct.setEL(keys[i],url.get(keys[i]));
			        	}
			        	args=sct;
		        	}
			        else if(Decision.isCastableToArray(args)){
			        	args = Caster.toArray(args);
			        }
			        else {
			        	Array arr = new ArrayImpl();
			        	arr.appendEL(args);
			        	args=arr;
			        }
		        }
		        else 
		        	args=url;

		        //print.out("c:"+requestedPage.getComponentName());
		        //print.out("c:"+requestedPage.getComponentName());
				Object rtn = call(app,pci, ON_CFCREQUEST, new Object[]{requestedPage.getComponentName(),method,args});
		        
		        if(rtn!=null){
		        	if(pc.getHttpServletRequest().getHeader("AMF-Forward")!=null) {
		        		pc.variablesScope().setEL("AMF-Forward", rtn);
		        		//ThreadLocalWDDXResult.set(rtn);
		        	}
		        	else {
		        		try {
							pc.forceWrite(ComponentPage.convertResult(pc,app,method.toString(),returnFormat,queryFormat,rtn));
						} catch (Exception e) {
							throw Caster.toPageException(e);
						}
		        	}
		        }
				
				
			}
			else if(!isCFC && app.contains(pc,ON_REQUEST)) {
				call(app,pci, ON_REQUEST, new Object[]{targetPage});
			}
			else {
				// TODO impl die nicht so generisch ist
				try{
					pci.doInclude(requestedPage);
				}
				catch(PageException pe){
					if(pe instanceof MissingIncludeException){
						if(((MissingIncludeException) pe).getPageSource().equals(requestedPage)){
							if(app.contains(pc,ON_MISSING_TEMPLATE)) {
								if(!Caster.toBooleanValue(call(app,pci, ON_MISSING_TEMPLATE, new Object[]{targetPage}),true))
									throw pe;
							}
							else throw pe;
						}
						else throw pe;
					}
					else throw pe;
				}
			}
			
			// onRequestEnd
			if(app.contains(pc,ON_REQUEST_END)) {
				call(app,pci, ON_REQUEST_END, new Object[]{targetPage});
			}
		}
		else {
			apps.put(pc.getApplicationContext().getName(), null);
			pc.doInclude(requestedPage);
		}
	}


	/**
	 *
	 * @see railo.runtime.listener.ApplicationListener#onApplicationStart(railo.runtime.PageContext)
	 */
	public boolean onApplicationStart(PageContext pc) throws PageException {
		ComponentImpl app = (ComponentImpl) apps.get(pc.getApplicationContext().getName());
		if(app!=null && app.contains(pc,ON_APPLICATION_START)) {
			Object rtn = call(app,pc, ON_APPLICATION_START, ArrayUtil.OBJECT_EMPTY);
			
			
			//print.o("has:"+hasOnSessionStart(pc));
			//((PageContextImpl)pc).resetSession();
			return Caster.toBooleanValue(rtn,true);
		}
		return true;
	}

	public void onApplicationEnd(CFMLFactory factory, String applicationName) throws PageException {
		ComponentImpl app = (ComponentImpl) apps.get(applicationName);
		if(app==null || !app.containsKey(ON_APPLICATION_END)) return;
		
		PageContextImpl pc=null;
		try {
			pc = (PageContextImpl) createPageContext(factory,app,applicationName,null,ON_APPLICATION_END);
			call(app,pc, ON_APPLICATION_END, new Object[]{pc.applicationScope()});
		}
		finally {
			if(pc!=null){
				factory.releasePageContext(pc);
			}
		}
	}
	
	/**
	 *
	 * @see railo.runtime.listener.ApplicationListener#onSessionStart(railo.runtime.PageContext)
	 */
	public void onSessionStart(PageContext pc) throws PageException {
		ComponentImpl app = (ComponentImpl) apps.get(pc.getApplicationContext().getName());
		if(hasOnSessionStart(pc,app)) {
			call(app,pc, ON_SESSION_START, ArrayUtil.OBJECT_EMPTY);
		}
	}

	/**
	 *
	 * @see railo.runtime.listener.ApplicationListener#onSessionEnd(railo.runtime.CFMLFactory, java.lang.String, java.lang.String)
	 */
	public void onSessionEnd(CFMLFactory factory, String applicationName, String cfid) throws PageException {
		ComponentImpl app = (ComponentImpl) apps.get(applicationName);
		if(app==null || !app.containsKey(ON_SESSION_END)) return;
		
		PageContextImpl pc=null;
		try {
			pc = createPageContext(factory,app,applicationName,cfid,ON_SESSION_END);
			call(app,pc, ON_SESSION_END, new Object[]{pc.sessionScope(false),pc.applicationScope()});
		}
		finally {
			if(pc!=null){
				factory.releasePageContext(pc);
			}
		}
	}

	private PageContextImpl createPageContext(CFMLFactory factory, ComponentImpl app, String applicationName, String cfid,Collection.Key methodName) throws PageException {
		Resource root = factory.getConfig().getRootDirectory();
		String path = app.getPageSource().getFullRealpath();
		
		// Request
		HttpServletRequestDummy req = new HttpServletRequestDummy(root,"localhost",path,"",null,null,null,null,null);
		if(!StringUtil.isEmpty(cfid))req.setCookies(new Cookie[]{new Cookie("cfid",cfid),new Cookie("cftoken","0")});
		
		// Response	
		OutputStream os=DevNullOutputStream.DEV_NULL_OUTPUT_STREAM;
		try {
			Resource out = factory.getConfig().getConfigDir().getRealResource("output/"+methodName.getString()+".out");
			out.getParentResource().mkdirs();
			os = out.getOutputStream(false);
		} 
		catch (IOException e) {
			e.printStackTrace();
			// TODO was passiert hier
		}
		HttpServletResponseDummy rsp = new HttpServletResponseDummy(os);
		
		// PageContext
		PageContextImpl pc = (PageContextImpl) factory.getRailoPageContext(factory.getServlet(), req, rsp, null, false, -1, false);
		// ApplicationContext
		ApplicationContextImpl ap = new ApplicationContextImpl(factory.getConfig(),false);
		initApplicationContext(pc, app);
		ap.setName(applicationName);
		ap.setSetSessionManagement(true);
		//if(!ap.hasName())ap.setName("Controler")
		// Base
		pc.setBase(app.getPageSource());
		
		return pc;
	}

	/**
	 *
	 * @see railo.runtime.listener.ApplicationListener#onDebug(railo.runtime.PageContext)
	 */
	public void onDebug(PageContext pc) throws PageException {
		ComponentImpl app = (ComponentImpl) apps.get(pc.getApplicationContext().getName());
		if(app!=null && app.contains(pc,ON_DEBUG)) {
			call(app,pc, ON_DEBUG, new Object[]{pc.getDebugger().getDebuggingData()});
			return;
		}
		try {
			pc.getDebugger().writeOut(pc);
		} 
		catch (IOException e) {
			throw Caster.toPageException(e);
		}
	}

	/**
	 *
	 * @see railo.runtime.listener.ApplicationListener#onError(railo.runtime.PageContext, railo.runtime.exp.PageException)
	 */
	public void onError(PageContext pc, PageException pe) {
		ComponentImpl app = (ComponentImpl) apps.get(pc.getApplicationContext().getName());
		if(app!=null && app.containsKey(ON_ERROR) && !(pe instanceof Abort)) {
			try {
				String eventName="";
				if(pe instanceof ModernAppListenerException) eventName= ((ModernAppListenerException)pe).getEventName();
				if(eventName==null)eventName="";
				
				call(app,pc, ON_ERROR, new Object[]{pe.getCatchBlock(pc),eventName});
				return;
			}
			catch(PageException _pe) {
				pe=_pe;
			}
		}
		pc.handlePageException(pe);
	}


	private Object call(ComponentImpl app, PageContext pc, Collection.Key eventName, Object[] args) throws ModernAppListenerException {
		try {
			return app.call(pc, eventName, args);
		} 
		catch (Abort abort) {
			return Boolean.FALSE;
		} 
		catch (PageException pe) {
			throw new ModernAppListenerException(pe,eventName.getString());
		}
	}

	private void initApplicationContext(PageContextImpl pc, ComponentImpl app) throws PageException {
		
		// use existing app context
		ApplicationContextImpl appContext = new ApplicationContextImpl(pc.getConfig(),app,false);

		
		Object o;
		boolean initORM=false;
		pc.addPageSource(app.getPageSource(), true);
		boolean hasError=false;
		try {
			
			// name
			o=get(app,NAME,"");
			if(o!=null) appContext.setName(Caster.toString(o));
			
			// applicationTimeout
			o=get(app,APPLICATION_TIMEOUT,null);
			if(o!=null) appContext.setApplicationTimeout(Caster.toTimespan(o));
				
			// clientManagement
			o=get(app,CLIENT_MANAGEMENT,null);
			if(o!=null) appContext.setSetClientManagement(Caster.toBooleanValue(o));

			// clientStorage
			o=get(app,CLIENT_STORAGE,null);
			if(o!=null) appContext.setClientstorage(Caster.toString(o));
			
			// sessionStorage
			o=get(app,SESSION_STORAGE,null);
			if(o!=null) appContext.setSessionstorage(Caster.toString(o));

			// loginStorage
			o=get(app,LOGIN_STORAGE,null);
			if(o!=null) appContext.setLoginStorage(Caster.toString(o));

			// datasource
			o = get(app,DATA_SOURCE,null);
			if(o!=null) {
				String ds = Caster.toString(o);
				appContext.setORMDataSource(ds);
				appContext.setDefaultDataSource(ds);
			}

			// default datasource
			o=get(app,DEFAULT_DATA_SOURCE,null);
			if(o!=null) appContext.setDefaultDataSource(Caster.toString(o));
			

			// sessionManagement
			o=get(app,SESSION_MANAGEMENT,null);
			if(o!=null) appContext.setSetSessionManagement(Caster.toBooleanValue(o));
			
			// sessionTimeout
			o=get(app,SESSION_TIMEOUT,null);
			if(o!=null) appContext.setSessionTimeout(Caster.toTimespan(o));
			
			// clientTimeout
			o=get(app,CLIENT_TIMEOUT,null);
			if(o!=null) appContext.setClientTimeout(Caster.toTimespan(o));
			
			// setClientCookies
			o=get(app,SET_CLIENT_COOKIES,null);
			if(o!=null) appContext.setSetClientCookies(Caster.toBooleanValue(o));
			
			// setDomainCookies
			o=get(app,SET_DOMAIN_COOKIES,null);
			if(o!=null) appContext.setSetDomainCookies(Caster.toBooleanValue(o));
			
			// scriptProtect
			o=get(app,SCRIPT_PROTECT,null);
			if(o!=null) appContext.setScriptProtect(Caster.toString(o));
			
			// mappings
			o=get(app,MAPPINGS,null);
			if(o!=null) appContext.setMappings(AppListenerUtil.toMappings(pc,o));
			
			// customtagpaths
			o=get(app,CUSTOM_TAG_PATHS,null);
			if(o!=null) appContext.setCustomTagMappings(AppListenerUtil.toCustomTagMappings(pc,o));
			
			// secureJsonPrefix
			o=get(app,SECURE_JSON_PREFIX,null);
			if(o!=null) appContext.setSecureJsonPrefix(Caster.toString(o));
			
			// secureJson
			o=get(app,SECURE_JSON,null);
			if(o!=null) appContext.setSecureJson(Caster.toBooleanValue(o));
			
			// local mode (always/update)
			o=get(app,LOCAL_MODE,null);
			if(o!=null) {
				int localMode = AppListenerUtil.toLocalMode(o,-1);
				if(localMode!=-1)
					appContext.setLocalMode(localMode);
				
			}
			
			
			
			
			// S3
			o=get(app,S3,null);
			if(o!=null && Decision.isStruct(o)){
				Struct sct=Caster.toStruct(o);
				
				String host=Caster.toString(sct.get(HOST,null));
				if(StringUtil.isEmpty(host))host=Caster.toString(sct.get(SERVER,null));
				
				appContext.setS3(
						Caster.toString(sct.get(ACCESS_KEY_ID,null)),
						Caster.toString(sct.get(AWS_SECRET_KEY,null)),
						Caster.toString(sct.get(DEFAULT_LOCATION,null)),
						host
					);
			}
			
			
			
	///////////////////////////////// ORM /////////////////////////////////
			// ormenabled
			o=get(app,ORM_ENABLED,null);
			if(o!=null && Caster.toBooleanValue(o,false)){
				initORM=true;
				appContext.setORMEnabled(Caster.toBooleanValue(o));
				
				// settings
				o=get(app,ORM_SETTINGS,null);
				Struct settings;
				if(!(o instanceof Struct))
					settings=new StructImpl();
				else
					settings=(Struct) o;
				//if(o instanceof Struct){
					//Struct settings=(Struct) o;
					
					// default cfc location (parent of the application.cfc)
					Resource res=null;
					
						res=ResourceUtil.getResource(pc, pc.getCurrentTemplatePageSource()).getParentResource();
					/*try {} catch (ExpressionException e) {
						e.printStackTrace();
					}*/
					ConfigImpl config=(ConfigImpl) pc.getConfig();
					ORMConfiguration ormConfig=ORMConfiguration.load(config,settings,res,config.getORMConfig());
					appContext.setORMConfiguration(ormConfig);
					
					// datasource
					o=settings.get(DATA_SOURCE,null);
					if(o!=null) appContext.setORMDataSource(Caster.toString(o));
				//}
			}
			
			
		}
		catch(Throwable t) {
			hasError=true;
			pc.removeLastPageSource(true);
		}
		
		pc.setApplicationContext(appContext);
		if(initORM) {
			if(hasError)pc.addPageSource(app.getPageSource(), true);
			try{
				ORMUtil.resetEngine(pc);
			}
			finally {
				if(hasError)pc.removeLastPageSource(true);
			}
		}
	}


	private static Object get(ComponentImpl app, Key name,String defaultValue) {
		Member mem = app.getMember(Component.ACCESS_PRIVATE, name, true, false);
		if(mem==null) return defaultValue;
		return mem.getValue();
	}

	/**
	 *
	 * @see railo.runtime.listener.ApplicationListener#setMode(int)
	 */
	public void setMode(int mode) {
		this.mode=mode;
	}

	/**
	 *
	 * @see railo.runtime.listener.ApplicationListener#getMode()
	 */
	public int getMode() {
		return mode;
	}
	

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * @see railo.runtime.listener.AppListenerSupport#hasOnSessionStart(railo.runtime.PageContext)
	 */
	public boolean hasOnSessionStart(PageContext pc) {
		return hasOnSessionStart(pc,(ComponentImpl) apps.get(pc.getApplicationContext().getName()));
	}
	private boolean hasOnSessionStart(PageContext pc,ComponentImpl app) {
		return app!=null && app.contains(pc,ON_SESSION_START);
	}
}
