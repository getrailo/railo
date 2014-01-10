package railo.runtime.gateway;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import railo.commons.io.DevNullOutputStream;
import railo.commons.io.log.Log;
import railo.commons.lang.ClassException;
import railo.commons.lang.Md5;
import railo.commons.lang.Pair;
import railo.loader.util.Util;
import railo.runtime.CFMLFactory;
import railo.runtime.Component;
import railo.runtime.ComponentPage;
import railo.runtime.PageContext;
import railo.runtime.PageContextImpl;
import railo.runtime.config.Config;
import railo.runtime.config.ConfigWeb;
import railo.runtime.config.ConfigWebImpl;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.exp.PageRuntimeException;
import railo.runtime.op.Caster;
import railo.runtime.thread.ThreadUtil;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.util.KeyConstants;

public class GatewayEngineImpl implements GatewayEnginePro {

	private static final Object OBJ = new Object();

	private static final Collection.Key AMF_FORWARD = KeyImpl.init("AMF-Forward");

	private Map<String,GatewayEntry> entries=new HashMap<String,GatewayEntry>();
	private ConfigWeb config;
	private Log log;

	
	public GatewayEngineImpl(ConfigWeb config){
		this.config=config;
		this.log=((ConfigWebImpl)config).getGatewayLogger();
		
	}
	
	public void addEntries(Config config,Map<String, GatewayEntry> entries) throws ClassException, PageException,IOException {
		Iterator<Entry<String, GatewayEntry>> it = entries.entrySet().iterator();
		while(it.hasNext()){
			addEntry(config,it.next().getValue());
		}
	}

	public void addEntry(Config config,GatewayEntry ge) throws ClassException, PageException,IOException {
		String id=ge.getId().toLowerCase().trim();
		GatewayEntry existing=entries.get(id);
		GatewayPro g=null;
		
		// does not exist
		if(existing==null) {
			entries.put(id,load(config,ge));
		}
		// exist but changed
		else if(!existing.equals(ge)){
			g=existing.getGateway();
			if(g.getState()==GatewayPro.RUNNING) g.doStop();
			entries.put(id,load(config,ge));
		}
		// not changed
		//else print.out("untouched:"+id);
	}

	private GatewayEntry load(Config config,GatewayEntry ge) throws ClassException,PageException {
		ge.createGateway(config);
		return ge;
	}

	/**
	 * @return the entries
	 */
	public Map<String,GatewayEntry> getEntries() {
		return entries;
	}

	public void remove(GatewayEntry ge) {
		String id=ge.getId().toLowerCase().trim();
		GatewayEntry existing=entries.remove(id);
		GatewayPro g=null;
		
		// does not exist
		if(existing!=null) {
			g=existing.getGateway();
			try{
				if(g.getState()==GatewayPro.RUNNING) g.doStop();
			}
			catch(Throwable t){}
		}
	}

	/**
	 * get the state of gateway
	 * @param gatewayId
	 * @return
	 * @throws PageException
	 */
	public int getState(String gatewayId) throws PageException {
		return getGateway(gatewayId).getState();
	}
	
	/**
	 * get helper object
	 * @param gatewayId
	 * @return
	 * @throws PageException
	 */
	public Object getHelper(String gatewayId) throws PageException {
		return getGateway(gatewayId).getHelper();
	}

	/**
	 * send the message to the gateway
	 * @param gatewayId
	 * @param data
	 * @return
	 * @throws PageException
	 */
	public String sendMessage(String gatewayId, Struct data) throws PageException,IOException {
		GatewayPro g = getGateway(gatewayId);
		if(g.getState()!=GatewayPro.RUNNING) throw new GatewayException("Gateway ["+gatewayId+"] is not running");
		return g.sendMessage(data);
	}
	
	/**
	 * start the gateway
	 * @param gatewayId
	 * @throws PageException
	 */
	public void start(String gatewayId) throws PageException {
		executeThread(gatewayId,GatewayThread.START);
	}
	private void start(GatewayPro gateway) {
		executeThread(gateway,GatewayThread.START);
	}

	/**
	 * stop the gateway
	 * @param gatewayId
	 * @throws PageException
	 */
	public void stop(String gatewayId) throws PageException {
		executeThread(gatewayId,GatewayThread.STOP);
	}
	private void stop(GatewayPro gateway) {
		executeThread(gateway,GatewayThread.STOP);
	}
	
	


	public void reset() {
		Iterator<Entry<String, GatewayEntry>> it = entries.entrySet().iterator();
		Entry<String, GatewayEntry> entry;
		GatewayEntry ge;
		GatewayPro g;
		while(it.hasNext()){
			entry = it.next();
			ge = entry.getValue();
			g=ge.getGateway();
			if(g.getState()==GatewayPro.RUNNING) {
				try {
					g.doStop();
				} catch (IOException e) {
					log(g, LOGLEVEL_ERROR, e.getMessage());
				}
			}
			if(ge.getStartupMode()==GatewayEntry.STARTUP_MODE_AUTOMATIC)
				start(g);
			
		}
	}

	public synchronized void clear() {
		Iterator<Entry<String, GatewayEntry>> it = entries.entrySet().iterator();
		Entry<String, GatewayEntry> entry;
		while(it.hasNext()){
			entry = it.next();
			if(entry.getValue().getGateway().getState()==GatewayPro.RUNNING) 
				stop(entry.getValue().getGateway());
		}
		entries.clear();
	}
	
	/**
	 * restart the gateway
	 * @param gatewayId
	 * @throws PageException
	 */
	public void restart(String gatewayId) throws PageException {
		executeThread(gatewayId,GatewayThread.RESTART);
	}
	
	private GatewayPro getGateway(String gatewayId) throws PageException {
		return getGatewayEntry(gatewayId).getGateway();
	}
	
	private GatewayEntry getGatewayEntry(String gatewayId) throws PageException {
		String id=gatewayId.toLowerCase().trim();
		GatewayEntry ge=entries.get(id);
		if(ge!=null) return ge;
		
		// create list
		Iterator<String> it = entries.keySet().iterator();
		StringBuilder sb=new StringBuilder();
		while(it.hasNext()){
			if(sb.length()>0) sb.append(", ");
			sb.append(it.next());
		}
		
		throw new ExpressionException("there is no gateway instance with id ["+gatewayId+"], available gateway instances are ["+sb+"]");
	}
	private GatewayEntry getGatewayEntry(GatewayPro gateway)  {
		String gatewayId=gateway.getId();
		// it must exist, because it only can come from here
		return entries.get(gatewayId);
	}
	
	private void executeThread(String gatewayId, int action) throws PageException {
		new GatewayThread(this,getGateway(gatewayId),action).start();
	}

	private void executeThread(GatewayPro g, int action) {
		new GatewayThread(this,g,action).start();
	}
	
	
	

	public static int toIntState(String state, int defaultValue) {
		state=state.trim().toLowerCase();
		if("running".equals(state))	return GatewayPro.RUNNING;
		if("started".equals(state)) return GatewayPro.RUNNING;
		if("run".equals(state)) 	return GatewayPro.RUNNING;
		
		if("failed".equals(state)) 	return GatewayPro.FAILED;
		if("starting".equals(state))return GatewayPro.STARTING;
		if("stopped".equals(state)) return GatewayPro.STOPPED;
		if("stopping".equals(state))return GatewayPro.STOPPING;
		
		return defaultValue;
	}
	
	public static String toStringState(int state, String defaultValue) {
		if(GatewayPro.RUNNING==state)	return "running";
		if(GatewayPro.FAILED==state)	return "failed";
		if(GatewayPro.STOPPED==state)	return "stopped";
		if(GatewayPro.STOPPING==state)	return "stopping";
		if(GatewayPro.STARTING==state)	return "starting";
			
		return defaultValue;
	}

	public boolean invokeListener(GatewayPro gateway, String method, Map data) {// FUTUTE add generic type to interface
		return invokeListener(gateway.getId(), method, data);
	}

	public boolean invokeListener(String gatewayId, String method, Map data) {// do not add this method to loade, it can be removed with Railo 5
		data=GatewayUtil.toCFML(data);
		
		GatewayEntry entry;
		try {
			entry = getGatewayEntry(gatewayId);
		}
		catch (PageException pe) {
			throw new PageRuntimeException(pe);
		}
		String cfcPath = entry.getListenerCfcPath();
		if(!Util.isEmpty(cfcPath,true)){
			try {
				if(!callOneWay(cfcPath,gatewayId, method, Caster.toStruct(data,null,false), false))
					log(gatewayId,LOGLEVEL_ERROR, "function ["+method+"] does not exist in cfc ["+toRequestURI(cfcPath)+"]");
				else
					return true;
			} 
			catch (PageException e) {
				e.printStackTrace();
				log(gatewayId,LOGLEVEL_ERROR, e.getMessage());
			}
		}
		else
			log(gatewayId,LOGLEVEL_ERROR, "there is no listener cfc defined");
		return false;
	}
	
	public Object callEL(String cfcPath,String id,String functionName,Struct arguments, boolean cfcPeristent, Object defaultValue)  {
		try {
			return call(cfcPath,id,functionName, arguments,cfcPeristent, defaultValue);
		} catch (PageException e) {
			return defaultValue;
		}
	}
	
	public boolean callOneWay(String cfcPath,String id,String functionName,Struct arguments, boolean cfcPeristent) throws PageException {
		return call(cfcPath,id,functionName, arguments,cfcPeristent, OBJ)!=OBJ;
	}

	public Object getComponent(String cfcPath,String id) throws PageException  {
		String requestURI=toRequestURI(cfcPath);
		
		PageContext oldPC = ThreadLocalPageContext.get();
		PageContextImpl pc = createPageContext(requestURI,id, "init", null, false);
		try {
			ThreadLocalPageContext.register(pc);
			return getCFC(pc,requestURI);
		}
		finally{
			CFMLFactory f = config.getFactory();
			f.releasePageContext(pc);
			ThreadLocalPageContext.register(oldPC);
		}
	}

	public Object call(String cfcPath,String id,String functionName,Struct arguments, boolean cfcPeristent, Object defaultValue) throws PageException  {
		String requestURI=toRequestURI(cfcPath);
		
		PageContext oldPC = ThreadLocalPageContext.get();
		PageContextImpl pc=createPageContext(requestURI,id,functionName,arguments,cfcPeristent);
		
		try {
			ThreadLocalPageContext.register(pc);
			Component cfc=getCFC(pc,requestURI);
			if(cfc.containsKey(functionName)){
				pc.execute(requestURI, true,false);
				// Result
				return pc.variablesScope().get(AMF_FORWARD,null);
			}
		}
		finally{
			CFMLFactory f = config.getFactory();
			f.releasePageContext(pc);
			ThreadLocalPageContext.register(oldPC);
		}
		return defaultValue;
	}

	private Component getCFC(PageContextImpl pc,String requestURI) throws PageException  {
		HttpServletRequest req = pc.getHttpServletRequest();
		try {
			req.setAttribute("client", "railo-gateway-1-0");
			req.setAttribute("call-type", "store-only");
			pc.execute(requestURI, true,false);
			return (Component) req.getAttribute("component");
		}
		finally {
			req.removeAttribute("call-type");
			req.removeAttribute("component");
		}
	}
	
	private PageContextImpl createPageContext(String requestURI,String id,String functionName, Struct arguments, boolean cfcPeristent) throws PageException {
		Struct attrs=new StructImpl();
		String remotePersisId;
		try {
			remotePersisId=Md5.getDigestAsString(requestURI+id);
		} catch (IOException e) {
			throw Caster.toPageException(e);
		}
		PageContextImpl pc = ThreadUtil.createPageContext(
				config, 
				DevNullOutputStream.DEV_NULL_OUTPUT_STREAM, 
				"localhost", 
				requestURI, 
				"method="+functionName+(cfcPeristent?"&"+ComponentPage.REMOTE_PERSISTENT_ID+"="+remotePersisId:""), 
				null, 
				new Pair[]{new Pair<String,Object>("AMF-Forward","true")}, 
				null, 
				attrs);
		
		pc.setRequestTimeout(999999999999999999L); 
		pc.setGatewayContext(true);
		if(arguments!=null)attrs.setEL(KeyConstants._argumentCollection, arguments);
		attrs.setEL("client", "railo-gateway-1-0");
		return pc;
	}

	public String toRequestURI(String cfcPath) {
		return GatewayUtil.toRequestURI(cfcPath);
	}

	@Override
	public void log(GatewayPro gateway, int level, String message) {
		log(gateway.getId(), level, message);
	}
	
	public void log(String gatewayId, int level, String message) {
		int l=level;
		switch(level){
		case LOGLEVEL_INFO:l=Log.LEVEL_INFO;
		break;
		case LOGLEVEL_DEBUG:l=Log.LEVEL_DEBUG;
		break;
		case LOGLEVEL_ERROR:l=Log.LEVEL_ERROR;
		break;
		case LOGLEVEL_FATAL:l=Log.LEVEL_FATAL;
		break;
		case LOGLEVEL_WARN:l=Log.LEVEL_WARN;
		break;
		}
		log.log(l, "Gateway:"+gatewayId, message);
	}
	

	private Map<String, Component> persistentRemoteCFC;
	public Component getPersistentRemoteCFC(String id) {
		if(persistentRemoteCFC==null) persistentRemoteCFC=new HashMap<String,Component>();
		return persistentRemoteCFC.get(id);
	}
	
	public Component setPersistentRemoteCFC(String id, Component cfc) {
		if(persistentRemoteCFC==null) persistentRemoteCFC=new HashMap<String,Component>();
		return persistentRemoteCFC.put(id,cfc);
	}
}
	
