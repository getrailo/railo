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
import railo.commons.lang.types.RefBoolean;
import railo.commons.lang.types.RefBooleanImpl;
import railo.runtime.CFMLFactory;
import railo.runtime.Component;
import railo.runtime.ComponentPage;
import railo.runtime.ComponentPro;
import railo.runtime.PageContext;
import railo.runtime.PageContextImpl;
import railo.runtime.PageSource;
import railo.runtime.component.ComponentLoader;
import railo.runtime.component.Member;
import railo.runtime.exp.Abort;
import railo.runtime.exp.MissingIncludeException;
import railo.runtime.exp.PageException;
import railo.runtime.interpreter.JSONExpressionInterpreter;
import railo.runtime.net.http.HttpServletRequestDummy;
import railo.runtime.net.http.HttpServletResponseDummy;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.orm.ORMUtil;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.Collection;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.cfc.ComponentAccess;
import railo.runtime.type.util.ArrayUtil;
import railo.runtime.type.util.StructUtil;

public class ModernAppListener extends AppListenerSupport {



	

	private static final Collection.Key ON_REQUEST_START = KeyImpl.intern("onRequestStart");
	private static final Collection.Key ON_CFCREQUEST = KeyImpl.intern("onCFCRequest");
	private static final Collection.Key ON_REQUEST = KeyImpl.intern("onRequest");
	private static final Collection.Key ON_REQUEST_END = KeyImpl.intern("onRequestEnd");
	private static final Collection.Key ON_APPLICATION_START = KeyImpl.intern("onApplicationStart");
	private static final Collection.Key ON_APPLICATION_END = KeyImpl.intern("onApplicationEnd");
	private static final Collection.Key ON_SESSION_START = KeyImpl.intern("onSessionStart");
	private static final Collection.Key ON_SESSION_END = KeyImpl.intern("onSessionEnd");
	private static final Collection.Key ON_DEBUG = KeyImpl.intern("onDebug");
	private static final Collection.Key ON_ERROR = KeyImpl.intern("onError");
	private static final Collection.Key ON_MISSING_TEMPLATE = KeyImpl.intern("onMissingTemplate");
	
	
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
			
			
			ComponentAccess app = ComponentLoader.loadComponent(pci,null,appPS, callPath, false,true);
			
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
		        
		        url.removeEL(KeyImpl.FIELD_NAMES);
		        url.removeEL(ComponentPage.METHOD);
		        Object args=url.get(KeyImpl.ARGUMENT_COLLECTION,null);
		        Object returnFormat=url.removeEL(KeyImpl.RETURN_FORMAT);
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
				catch (Abort abort) {}
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
		ComponentAccess app = (ComponentAccess) apps.get(pc.getApplicationContext().getName());
		if(app!=null && app.contains(pc,ON_APPLICATION_START)) {
			Object rtn = call(app,pc, ON_APPLICATION_START, ArrayUtil.OBJECT_EMPTY);
			return Caster.toBooleanValue(rtn,true);
		}
		return true;
	}

	public void onApplicationEnd(CFMLFactory factory, String applicationName) throws PageException {
		ComponentAccess app = (ComponentAccess) apps.get(applicationName);
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
		ComponentAccess app = (ComponentAccess) apps.get(pc.getApplicationContext().getName());
		if(hasOnSessionStart(pc,app)) {
			call(app,pc, ON_SESSION_START, ArrayUtil.OBJECT_EMPTY);
		}
	}

	/**
	 *
	 * @see railo.runtime.listener.ApplicationListener#onSessionEnd(railo.runtime.CFMLFactory, java.lang.String, java.lang.String)
	 */
	public void onSessionEnd(CFMLFactory factory, String applicationName, String cfid) throws PageException {
		ComponentAccess app = (ComponentAccess) apps.get(applicationName);
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

	private PageContextImpl createPageContext(CFMLFactory factory, ComponentAccess app, String applicationName, String cfid,Collection.Key methodName) throws PageException {
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
		ClassicApplicationContext ap = new ClassicApplicationContext(factory.getConfig(),applicationName,false);
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
		ComponentAccess app = (ComponentAccess) apps.get(pc.getApplicationContext().getName());
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
		ComponentAccess app = (ComponentAccess) apps.get(pc.getApplicationContext().getName());
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


	private Object call(ComponentPro app, PageContext pc, Collection.Key eventName, Object[] args) throws ModernAppListenerException {
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

	private void initApplicationContext(PageContextImpl pc, ComponentAccess app) throws PageException {
		
		// use existing app context
		RefBoolean throwsErrorWhileInit=new RefBooleanImpl(false);
		ModernApplicationContext appContext = new ModernApplicationContext(pc,app,throwsErrorWhileInit);

		
		pc.setApplicationContext(appContext);
		if(appContext.isORMEnabled()) {
			boolean hasError=throwsErrorWhileInit.toBooleanValue();
			if(hasError)pc.addPageSource(app.getPageSource(), true);
			try{
				ORMUtil.resetEngine(pc,false);
			}
			finally {
				if(hasError)pc.removeLastPageSource(true);
			}
		}
	}


	private static Object get(ComponentAccess app, Key name,String defaultValue) {
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
		return hasOnSessionStart(pc,(ComponentAccess) apps.get(pc.getApplicationContext().getName()));
	}
	private boolean hasOnSessionStart(PageContext pc,ComponentAccess app) {
		return app!=null && app.contains(pc,ON_SESSION_START);
	}
}
