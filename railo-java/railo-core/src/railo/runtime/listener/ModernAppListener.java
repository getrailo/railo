package railo.runtime.listener;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;

import railo.commons.io.DevNullOutputStream;
import railo.commons.io.res.Resource;
import railo.commons.lang.StringUtil;
import railo.runtime.CFMLFactory;
import railo.runtime.Component;
import railo.runtime.ComponentImpl;
import railo.runtime.PageContext;
import railo.runtime.PageContextImpl;
import railo.runtime.PageSource;
import railo.runtime.component.ComponentLoader;
import railo.runtime.component.Member;
import railo.runtime.exp.Abort;
import railo.runtime.exp.MissingIncludeException;
import railo.runtime.exp.PageException;
import railo.runtime.net.http.HttpServletRequestDummy;
import railo.runtime.net.http.HttpServletResponseDummy;
import railo.runtime.op.Caster;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.util.ArrayUtil;
import railo.runtime.util.ApplicationContextImpl;

public class ModernAppListener implements ApplicationListener {


	private static final Collection.Key NAME = KeyImpl.getInstance("name");
	private static final Collection.Key APPLICATION_TIMEOUT = KeyImpl.getInstance("applicationTimeout");
	private static final Collection.Key CLIENT_MANAGEMENT = KeyImpl.getInstance("clientManagement");
	private static final Collection.Key CLIENT_STORAGE = KeyImpl.getInstance("clientStorage");
	private static final Collection.Key LOGIN_STORAGE = KeyImpl.getInstance("loginStorage");
	private static final Collection.Key SESSION_MANAGEMENT = KeyImpl.getInstance("sessionManagement");
	private static final Collection.Key SESSION_TIMEOUT = KeyImpl.getInstance("sessionTimeout");
	private static final Collection.Key SET_CLIENT_COOKIES = KeyImpl.getInstance("setClientCookies");
	private static final Collection.Key SET_DOMAIN_COOKIES = KeyImpl.getInstance("setDomainCookies");
	private static final Collection.Key SCRIPT_PROTECT = KeyImpl.getInstance("scriptProtect");
	private static final Collection.Key MAPPINGS = KeyImpl.getInstance("mappings");
	private static final Collection.Key CUSTOM_TAG_PATHS = KeyImpl.getInstance("customtagpaths");
	private static final Collection.Key SECURE_JSON_PREFIX = KeyImpl.getInstance("secureJsonPrefix");
	private static final Collection.Key SECURE_JSON = KeyImpl.getInstance("secureJson");
	

	private static final Collection.Key ON_REQUEST_START = KeyImpl.getInstance("onRequestStart");
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
	
	
	
	//private ComponentImpl app;
	private Map apps=new HashMap();
	protected int mode=MODE_CURRENT2ROOT;
	private String type;


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
			ComponentImpl app = ComponentLoader.loadComponentImpl(pci,null,appPS, callPath, false);
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
			if(app.contains(pc,ON_REQUEST)) {
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
			//if(StringUtil.isEmpty(rtn)) return true;
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
		if(app!=null && app.contains(pc,ON_SESSION_START)) {
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
		
		PageContext pc=null;
		try {
			pc = createPageContext(factory,app,applicationName,cfid,ON_SESSION_END);
			call(app,pc, ON_SESSION_END, new Object[]{pc.sessionScope(),pc.applicationScope()});
		}
		finally {
			if(pc!=null){
				factory.releasePageContext(pc);
			}
		}
	}

	private PageContext createPageContext(CFMLFactory factory, ComponentImpl app, String applicationName, String cfid,Collection.Key methodName) {
		Resource root = factory.getConfig().getRootDirectory();
		String path = app.getPage().getPageSource().getFullRealpath();
		
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
		
		// Base
		pc.setBase(app.getPage().getPageSource());
		
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

	private void initApplicationContext(PageContextImpl pc, ComponentImpl app) {
		ApplicationContextImpl appContext=new ApplicationContextImpl(pc.getConfig(),false);
		Object o;
		
		pc.addPageSource(app.getPage().getPageSource(), true);
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

			// loginStorage
			o=get(app,LOGIN_STORAGE,null);
			if(o!=null) appContext.setLoginStorage(Caster.toString(o));

			// datasource
			o=get(app,DEFAULT_DATA_SOURCE,null);
			if(o==null) o=get(app,DATA_SOURCE,null);
			if(o!=null) appContext.setDefaultDataSource(Caster.toString(o));
			
			// sessionManagement
			o=get(app,SESSION_MANAGEMENT,null);
			if(o!=null) appContext.setSetSessionManagement(Caster.toBooleanValue(o));
			
			// sessionTimeout
			o=get(app,SESSION_TIMEOUT,null);
			if(o!=null) appContext.setSessionTimeout(Caster.toTimespan(o));
			
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
		}
		catch(Throwable t) {
			pc.removeLastPageSource(true);
		}
		
		pc.setApplicationContext(appContext);
		
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
}
