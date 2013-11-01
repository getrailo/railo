package railo.runtime.listener;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.Cookie;

import railo.commons.io.DevNullOutputStream;
import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.StringUtil;
import railo.commons.lang.mimetype.MimeType;
import railo.commons.lang.types.RefBoolean;
import railo.commons.lang.types.RefBooleanImpl;
import railo.runtime.CFMLFactory;
import railo.runtime.Component;
import railo.runtime.ComponentPage;
import railo.runtime.PageContext;
import railo.runtime.PageContextImpl;
import railo.runtime.PageSource;
import railo.runtime.component.ComponentLoader;
import railo.runtime.component.Member;
import railo.runtime.config.Constants;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.Abort;
import railo.runtime.exp.MissingIncludeException;
import railo.runtime.exp.PageException;
import railo.runtime.exp.PostContentAbort;
import railo.runtime.interpreter.JSONExpressionInterpreter;
import railo.runtime.net.http.HttpServletRequestDummy;
import railo.runtime.net.http.HttpServletResponseDummy;
import railo.runtime.net.http.ReqRspUtil;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.op.Duplicator;
import railo.runtime.orm.ORMUtil;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.Collection;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.cfc.ComponentAccess;
import railo.runtime.type.util.ArrayUtil;
import railo.runtime.type.util.KeyConstants;
import railo.runtime.type.util.UDFUtil;

public class ModernAppListener extends AppListenerSupport {



	

	private static final Collection.Key ON_REQUEST_START = KeyImpl.intern("onRequestStart");
	private static final Collection.Key ON_CFCREQUEST = KeyImpl.intern("onCFCRequest");
	private static final Collection.Key ON_REQUEST = KeyImpl.intern("onRequest");
	private static final Collection.Key ON_REQUEST_END = KeyImpl.intern("onRequestEnd");
	private static final Collection.Key ON_ABORT = KeyImpl.intern("onAbort");
	private static final Collection.Key ON_APPLICATION_START = KeyImpl.intern("onApplicationStart");
	private static final Collection.Key ON_APPLICATION_END = KeyImpl.intern("onApplicationEnd");
	private static final Collection.Key ON_SESSION_START = KeyImpl.intern("onSessionStart");
	private static final Collection.Key ON_SESSION_END = KeyImpl.intern("onSessionEnd");
	private static final Collection.Key ON_DEBUG = KeyImpl.intern("onDebug");
	private static final Collection.Key ON_ERROR = KeyImpl.intern("onError");
	private static final Collection.Key ON_MISSING_TEMPLATE = KeyImpl.intern("onMissingTemplate");
	
	
	//private ComponentImpl app;
	private Map<String,ComponentAccess> apps=new HashMap<String,ComponentAccess>();
	protected int mode=MODE_CURRENT2ROOT;
	
	@Override
	public void onRequest(PageContext pc, PageSource requestedPage, RequestListener rl) throws PageException {
		// on requestStart
		PageSource appPS=//pc.isCFCRequest()?null:
			AppListenerUtil.getApplicationPageSource(pc,requestedPage,Constants.APP_CFC,mode);
		
		_onRequest(pc, requestedPage, appPS,rl);
	}
	
	protected void _onRequest(PageContext pc, PageSource requestedPage,PageSource appPS, RequestListener rl) throws PageException {
		PageContextImpl pci = (PageContextImpl)pc;
		if(appPS!=null) {
			String callPath=appPS.getComponentName();
			
			
			ComponentAccess app = ComponentLoader.loadComponent(pci,null,appPS, callPath, false,false);
			
			// init
			initApplicationContext(pci,app);
	    	
			
			apps.put(pc.getApplicationContext().getName(), app);

			if(!pci.initApplicationContext(this)) return;
			
			if(rl!=null) {
				requestedPage=rl.execute(pc, requestedPage);
				if(requestedPage==null) return;
			}
			
			String targetPage=requestedPage.getFullRealpath();
			RefBoolean goon=new RefBooleanImpl(true);
			
			// onRequestStart
			if(app.contains(pc,ON_REQUEST_START)) {
				try {
					Object rtn=call(app,pci, ON_REQUEST_START, new Object[]{targetPage},false);
					if(!Caster.toBooleanValue(rtn,true))
						return;
				}
				catch(PageException pe){
					pe=handlePageException(pci,app,pe,requestedPage,targetPage,goon);
					if(pe!=null) throw pe;
				}
			}
	    	
			// onRequest
			if(goon.toBooleanValue()) {
			boolean isCFC=ResourceUtil.getExtension(targetPage,"").equalsIgnoreCase(pc.getConfig().getCFCExtension());
			Object method;
			if(isCFC && app.contains(pc,ON_CFCREQUEST) && (method=pc.urlFormScope().get(KeyConstants._method,null))!=null) { 
				
				Struct url = (Struct)Duplicator.duplicate(pc.urlFormScope(),true);

				url.removeEL(KeyConstants._fieldnames);
				url.removeEL(KeyConstants._method);
				
				Object args=url.get(KeyConstants._argumentCollection,null);
				
				// url returnFormat
				Object oReturnFormat=url.removeEL(KeyConstants._returnFormat);
				int urlReturnFormat=-1;
				if(oReturnFormat!=null) urlReturnFormat=UDFUtil.toReturnFormat(Caster.toString(oReturnFormat,null),-1);
				
				// request header accept
				List<MimeType> accept = ReqRspUtil.getAccept(pc);
				int headerReturnFormat = MimeType.toFormat(accept, -1,-1);

		        Object queryFormat=url.removeEL(KeyConstants._queryFormat);
		        
		        if(args==null){
		        	args=pc.getHttpServletRequest().getAttribute("argumentCollection");
		        }
		        
		        if(args instanceof String){
		        	args=new JSONExpressionInterpreter().interpret(pc, (String)args);
		        }
		        
		        if(args!=null) {
		        	if(Decision.isCastableToStruct(args)){
			        	Struct sct = Caster.toStruct(args,false);
			        	//Key[] keys = url.keys();
			        	Iterator<Entry<Key, Object>> it = url.entryIterator();
			        	Entry<Key, Object> e;
			        	while(it.hasNext()){
			        		e = it.next();
			        		sct.setEL(e.getKey(),e.getValue());
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

		        Object rtn = call(app,pci, ON_CFCREQUEST, new Object[]{requestedPage.getComponentName(),method,args},true);
		        
		        if(rtn!=null){
		        	if(pc.getHttpServletRequest().getHeader("AMF-Forward")!=null) {
		        		pc.variablesScope().setEL("AMF-Forward", rtn);
		        		//ThreadLocalWDDXResult.set(rtn);
		        	}
		        	else {
		        		try {
							ComponentPage.writeToResponseStream(pc,app,method.toString(),urlReturnFormat,headerReturnFormat,queryFormat,rtn);
						} catch (Exception e) {
							throw Caster.toPageException(e);
						}
		        	}
		        }
				
				
			}
			//else if(!isCFC && app.contains(pc,ON_REQUEST)) {}
			else {
				// TODO impl die nicht so generisch ist
				try{

					if(!isCFC && app.contains(pc,ON_REQUEST))
						call(app,pci, ON_REQUEST, new Object[]{targetPage},false);
					else
						pci.doInclude(requestedPage);
				}
				catch(PageException pe){
					pe=handlePageException(pci,app,pe,requestedPage,targetPage,goon);
					if(pe!=null) throw pe;
				}
			}
			}
			// onRequestEnd
			if(goon.toBooleanValue() && app.contains(pc,ON_REQUEST_END)) {
				try {
					call(app,pci, ON_REQUEST_END, new Object[]{targetPage},false);
				}
				catch(PageException pe){
					pe=handlePageException(pci,app,pe,requestedPage,targetPage,goon);
					if(pe!=null) throw pe;
				}
			}
		}
		else {
			apps.put(pc.getApplicationContext().getName(), null);
			pc.doInclude(requestedPage);
		}
	}
	

	private PageException handlePageException(PageContextImpl pci, ComponentAccess app, PageException pe, PageSource requestedPage, String targetPage, RefBoolean goon) throws PageException {
		PageException _pe=pe;
		if(pe instanceof ModernAppListenerException) {
			_pe=((ModernAppListenerException) pe).getPageException();
		}
		
		if(!Abort.isSilentAbort(_pe)) {
			if(_pe instanceof MissingIncludeException){
				if(((MissingIncludeException) _pe).getPageSource().equals(requestedPage)){
					
					if(app.contains(pci,ON_MISSING_TEMPLATE)) {
						goon.setValue(false);
						if(!Caster.toBooleanValue(call(app,pci, ON_MISSING_TEMPLATE, new Object[]{targetPage},true),true))
							return pe;
					}
					else return pe;
				}
				else return pe;
			}
			else return pe;
		}
		else {
			goon.setValue(false);
			if(app.contains(pci,ON_ABORT)) {
				call(app,pci, ON_ABORT, new Object[]{targetPage},true);
			}
		}
		return null;
	}

	@Override
	public boolean onApplicationStart(PageContext pc) throws PageException {
		ComponentAccess app = apps.get(pc.getApplicationContext().getName());
		if(app!=null && app.contains(pc,ON_APPLICATION_START)) {
			Object rtn = call(app,pc, ON_APPLICATION_START, ArrayUtil.OBJECT_EMPTY,true);
			return Caster.toBooleanValue(rtn,true);
		}
		return true;
	}

	@Override
	public void onApplicationEnd(CFMLFactory factory, String applicationName) throws PageException {
		ComponentAccess app = apps.get(applicationName);
		if(app==null || !app.containsKey(ON_APPLICATION_END)) return;
		
		PageContextImpl pc=(PageContextImpl) ThreadLocalPageContext.get();
		boolean createPc=pc==null;
		try {
			if(createPc)pc =  createPageContext(factory,app,applicationName,null,ON_APPLICATION_END);
			call(app,pc, ON_APPLICATION_END, new Object[]{pc.applicationScope()},true);
		}
		finally {
			if(createPc && pc!=null){
				factory.releasePageContext(pc);
			}
		}
	}
	
	@Override
	public void onSessionStart(PageContext pc) throws PageException {
		ComponentAccess app = apps.get(pc.getApplicationContext().getName());
		if(hasOnSessionStart(pc,app)) {
			call(app,pc, ON_SESSION_START, ArrayUtil.OBJECT_EMPTY,true);
		}
	}

	@Override
	public void onSessionEnd(CFMLFactory factory, String applicationName, String cfid) throws PageException {
		ComponentAccess app = apps.get(applicationName);
		if(app==null || !app.containsKey(ON_SESSION_END)) return;
		
		PageContextImpl pc=null;
		try {
			pc = createPageContext(factory,app,applicationName,cfid,ON_SESSION_END);
			call(app,pc, ON_SESSION_END, new Object[]{pc.sessionScope(false),pc.applicationScope()},true);
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
		ClassicApplicationContext ap = new ClassicApplicationContext(factory.getConfig(),applicationName,false,app==null?null:ResourceUtil.getResource(pc,app.getPageSource(),null));
		initApplicationContext(pc, app);
		ap.setName(applicationName);
		ap.setSetSessionManagement(true);
		//if(!ap.hasName())ap.setName("Controler")
		// Base
		pc.setBase(app.getPageSource());
		
		return pc;
	}

	@Override
	public void onDebug(PageContext pc) throws PageException {
		if(((PageContextImpl)pc).isGatewayContext() || !pc.getConfig().debug()) return;
		ComponentAccess app = apps.get(pc.getApplicationContext().getName());
		if(app!=null && app.contains(pc,ON_DEBUG)) {
			call(app,pc, ON_DEBUG, new Object[]{pc.getDebugger().getDebuggingData(pc)},true);
			return;
		}
		try {
			pc.getDebugger().writeOut(pc);
		} 
		catch (IOException e) {
			throw Caster.toPageException(e);
		}
	}

	@Override
	public void onError(PageContext pc, PageException pe) {
		ComponentAccess app =  apps.get(pc.getApplicationContext().getName());
		if(app!=null && app.containsKey(ON_ERROR) && !Abort.isSilentAbort(pe)) {
			try {
				String eventName="";
				if(pe instanceof ModernAppListenerException) eventName= ((ModernAppListenerException)pe).getEventName();
				if(eventName==null)eventName="";
				
				call(app,pc, ON_ERROR, new Object[]{pe.getCatchBlock(pc),eventName},true);
				return;
			}
			catch(PageException _pe) {
				pe=_pe;
			}
		}
		pc.handlePageException(pe);
	}


	private Object call(Component app, PageContext pc, Collection.Key eventName, Object[] args, boolean catchAbort) throws PageException {
		try {
			return app.call(pc, eventName, args);
		} 
		catch (PageException pe) {
			if(Abort.isSilentAbort(pe)) {
				if(catchAbort)
					return ( pe instanceof PostContentAbort) ? Boolean.TRUE : Boolean.FALSE;

				throw pe;
			}
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

	@Override
	public void setMode(int mode) {
		this.mode=mode;
	}

	@Override
	public int getMode() {
		return mode;
	}
	

	@Override
	public String getType() {
		return "modern";
	}
	
	@Override
	public boolean hasOnSessionStart(PageContext pc) {
		return hasOnSessionStart(pc, apps.get(pc.getApplicationContext().getName()));
	}
	private boolean hasOnSessionStart(PageContext pc,ComponentAccess app) {
		return app!=null && app.contains(pc,ON_SESSION_START);
	}
}
