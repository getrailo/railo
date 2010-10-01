package railo.runtime.gateway;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.opencfml.eventgateway.Gateway;
import org.opencfml.eventgateway.GatewayEngine;
import org.opencfml.eventgateway.GatewayException;

import railo.commons.io.DevNullOutputStream;
import railo.commons.io.log.Log;
import railo.commons.io.res.Resource;
import railo.commons.lang.ClassException;
import railo.loader.util.Util;
import railo.runtime.CFMLFactory;
import railo.runtime.Component;
import railo.runtime.Mapping;
import railo.runtime.MappingImpl;
import railo.runtime.Page;
import railo.runtime.PageContext;
import railo.runtime.PageContextImpl;
import railo.runtime.PageSource;
import railo.runtime.PageSourceImpl;
import railo.runtime.component.ComponentLoader;
import railo.runtime.config.Config;
import railo.runtime.config.ConfigImpl;
import railo.runtime.config.ConfigServerImpl;
import railo.runtime.config.ConfigWeb;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.FunctionNotSupported;
import railo.runtime.exp.PageException;
import railo.runtime.exp.PageRuntimeException;
import railo.runtime.op.Caster;
import railo.runtime.thread.ThreadUtil;
import railo.runtime.type.Struct;

public class GatewayEngineImpl implements GatewayEngine {

	private static final Object OBJ = new Object();

	private Map entries=new HashMap();
	private Resource cfcDirectory;
	private ConfigWeb config;

	private Map cfcs=new HashMap();

	private Log log;
	
	public GatewayEngineImpl(Config config){
		if(config instanceof ConfigWeb){
			this.config=(ConfigWeb) config;
		}
		else {
			Resource root = config.getConfigDir().getRealResource("gatewayRoot");
			root.mkdirs();
			this.config=((ConfigServerImpl)config).getConfigWeb();
		}
		this.log=((ConfigImpl)config).getGatewayLogger();
		
	}
	
	public void addEntries(Config config,Map entries) throws ClassException, PageException,GatewayException {
		Iterator it = entries.entrySet().iterator();
		Map.Entry entry;
		while(it.hasNext()){
			entry=(Entry) it.next();
			addEntry(config,(GatewayEntry)entry.getValue());
		}
	}

	public void addEntry(Config config,GatewayEntry ge) throws ClassException, PageException,GatewayException {
		String id=ge.getId().toLowerCase().trim();
		GatewayEntry existing=(GatewayEntry) entries.get(id);
		Gateway g=null;
		
		// does not exist
		if(existing==null) {
			entries.put(id,load(config,ge));
		}
		// exist but changed
		else if(!existing.equals(ge)){
			g=existing.getGateway();
			if(g.getState()==Gateway.RUNNING) g.doStop();
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
	public Map getEntries() {
		return entries;
	}

	public void remove(GatewayEntry ge) throws GatewayException {
		String id=ge.getId().toLowerCase().trim();
		GatewayEntry existing=(GatewayEntry) entries.remove(id);
		Gateway g=null;
		
		// does not exist
		if(existing!=null) {
			g=existing.getGateway();
			if(g.getState()==Gateway.RUNNING) g.doStop();
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
	public String sendMessage(String gatewayId, Struct data) throws PageException,GatewayException {
		Gateway g = getGateway(gatewayId);
		if(g.getState()!=Gateway.RUNNING) throw new GatewayException("Gateway ["+gatewayId+"] is not running");
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

	/**
	 * stop the gateway
	 * @param gatewayId
	 * @throws PageException
	 */
	public void stop(String gatewayId) throws PageException {
		executeThread(gatewayId,GatewayThread.STOP);
	}
	
	/**
	 * restart the gateway
	 * @param gatewayId
	 * @throws PageException
	 */
	public void restart(String gatewayId) throws PageException {
		executeThread(gatewayId,GatewayThread.RESTART);
	}
	
	private Gateway getGateway(String gatewayId) throws PageException {
		return getGatewayEntry(gatewayId).getGateway();
	}
	
	private GatewayEntry getGatewayEntry(String gatewayId) throws PageException {
		GatewayEntry ge=(GatewayEntry) entries.get(gatewayId);
		if(ge!=null) return ge;
		throw new ExpressionException("there is no Gateway instance with id ["+gatewayId+"]");
	}
	private GatewayEntry getGatewayEntry(Gateway gateway)  {
		String gatewayId=gateway.getId();
		// it must exist, because it only can come from here
		return (GatewayEntry) entries.get(gatewayId);
	}

	public static void checkRestriction() {
		PageContext pc = ThreadLocalPageContext.get();
		boolean enable = false;
		try {
			enable=Caster.toBooleanValue(pc.serverScope().get("enableGateway", Boolean.FALSE), false);
		} 
		catch (PageException e) {}
		//enable=false;
		if(!enable)
			throw new PageRuntimeException(new FunctionNotSupported("SendGatewayMessage"));
	}

	public Resource getCFCDirectory() {
		return cfcDirectory;
	}

	public void setCFCDirectory(Resource cfcDirectory) {
		this.cfcDirectory=cfcDirectory;
	}

	private void executeThread(String gatewayId, int action) throws PageException {
		
		new GatewayThread(this,getGateway(gatewayId),action).start();
	}
	
	
	

	public static int toIntState(String state, int defaultValue) {
		state=state.trim().toLowerCase();
		if("running".equals(state))	return Gateway.RUNNING;
		if("started".equals(state)) return Gateway.RUNNING;
		if("run".equals(state)) 	return Gateway.RUNNING;
		
		if("failed".equals(state)) 	return Gateway.FAILED;
		if("starting".equals(state))return Gateway.STARTING;
		if("stopped".equals(state)) return Gateway.STOPPED;
		if("stopping".equals(state))return Gateway.STOPPING;
		
		return defaultValue;
	}
	
	public static String toStringState(int state, String defaultValue) {
		if(Gateway.RUNNING==state)	return "running";
		if(Gateway.FAILED==state)	return "failed";
		if(Gateway.STOPPED==state)	return "stopped";
		if(Gateway.STOPPING==state)	return "stopping";
		if(Gateway.STARTING==state)	return "starting";
			
		return defaultValue;
	}

	public boolean invokeListener(Gateway gateway, String method, Map data) {
		data=GatewayUtil.toCFML(data);
		
		GatewayEntry entry = getGatewayEntry(gateway);
		String cfcPath = entry.getListenerCfcPath();
		if(!Util.isEmpty(cfcPath,true)){
			try {
				if(!callOneWay(cfcPath,gateway.getId(), method, Caster.toStruct(data,null,false), false))
					log(gateway,LOGLEVEL_ERROR, "function ["+method+"] does not exist in cfc ["+getCFCDirectory()+toRequestURI(cfcPath)+"]");
				else
					return true;
			} 
			catch (PageException e) {
				e.printStackTrace();
				log(gateway,LOGLEVEL_ERROR, e.getMessage());
			}
		}
		else
			log(gateway,LOGLEVEL_ERROR, "there is no listener cfc defined");
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
		DevNullOutputStream os = DevNullOutputStream.DEV_NULL_OUTPUT_STREAM;
		String requestURI=toRequestURI(cfcPath);
		
		PageContext oldPC = ThreadLocalPageContext.get();
		PageContextImpl pc = ThreadUtil.createPageContext(config, os, "localhost", requestURI, "", null, null, null, null);
		pc.setRequestTimeout(999999999999999999L);      
		
		try {
			ThreadLocalPageContext.register(pc);
			return getCFC(pc,requestURI,cfcPath,id,false);
		}
		finally{
			ThreadLocalPageContext.register(oldPC);
		}
	}

	public Object call(String cfcPath,String id,String functionName,Struct arguments, boolean cfcPeristent, Object defaultValue) throws PageException  {
		DevNullOutputStream os = DevNullOutputStream.DEV_NULL_OUTPUT_STREAM;
		String requestURI=toRequestURI(cfcPath);
		
		PageContext oldPC = ThreadLocalPageContext.get();
		PageContextImpl pc = ThreadUtil.createPageContext(config, os, "localhost", requestURI, "", null, null, null, null);
		pc.setRequestTimeout(999999999999999999L);      
		try {
			ThreadLocalPageContext.register(pc);
			Component cfc=getCFC(pc,requestURI,cfcPath,id,cfcPeristent);
			if(cfc.containsKey(functionName)){
				return cfc.callWithNamedValues(pc, functionName, arguments);
			}
		}
		finally{
			CFMLFactory factory = config.getFactory();
			factory.releasePageContext(pc);
			ThreadLocalPageContext.register(oldPC);
		}
		return defaultValue;
	}
	
	private Component getCFC(PageContext pc,String requestURI,String cfcPath, String id,boolean peristent) throws PageException{
		Component cfc;
		if(peristent){
			cfc=(Component) cfcs.get(requestURI+id);
			if(cfc!=null)return cfc;
		}
		ConfigImpl config=(ConfigImpl) pc.getConfig();
		Mapping m=new MappingImpl((ConfigImpl)pc.getConfig(),"/",getCFCDirectory().getAbsolutePath(),null,false,true,false,false,false);
		
		PageSource ps = m.getPageSource(requestURI);
		Page p = ((PageSourceImpl)ps).loadPage(pc,(ConfigWeb)config);
		cfc= ComponentLoader.loadComponentImpl(pc, p, ps, cfcPath, false,true);
		if(peristent) cfcs.put(requestURI+id, cfc);
		return cfc;
	}
	
	
	public void clear(String cfcPath,String id)  {
		cfcs.remove(toRequestURI(cfcPath)+id);
	}

	/**
	 * @see org.opencfml.eventgateway.GatewayEngine#toRequestURI(java.lang.String)
	 */
	public String toRequestURI(String cfcPath) {
		return GatewayUtil.toRequestURI(cfcPath);
	}

	public void log(Gateway gateway, int level, String message) {
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
		log.log(l, "Gateway:"+gateway.getId(), message);
	}
}
	
