package railo.runtime.gateway;

import java.util.Map;

import railo.commons.lang.StringUtil;
import railo.runtime.exp.PageException;
import railo.runtime.gateway.proxy.GatewayProFactory;
import railo.runtime.op.Caster;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;

public class CFCGateway implements GatewayPro {
	
	//private static final Object OBJ = new Object();
	//private Component _cfc;
	private String id;
	private int state=GatewayPro.STOPPED;
	private String cfcPath;
	//private Config config;
	//private String requestURI;
	//private Resource cfcDirectory;
	private GatewayEngineImpl engine;

	public CFCGateway(String cfcPath) {
		this.cfcPath=cfcPath;
	}

	@Override
	public void init(GatewayEnginePro engine,String id, String cfcPath, Map config) throws GatewayException {
		this.engine=GatewayProFactory.toGatewayEngineImpl(engine);
		this.id=id;
		
		//requestURI=engine.toRequestURI(cfcPath);
		Struct args=new StructImpl(StructImpl.TYPE_LINKED);
		args.setEL("id", id);
		args.setEL("config", Caster.toStruct(config,null,false));
		if(!StringUtil.isEmpty(cfcPath)){
			try {
				args.setEL("listener", this.engine.getComponent(cfcPath,id));
			} catch (PageException e) {
				engine.log(this,GatewayEnginePro.LOGLEVEL_ERROR, e.getMessage());
			}
		}
		
		try {
			callOneWay("init",args);
		} catch (PageException pe) {
			
			engine.log(this,GatewayEnginePro.LOGLEVEL_ERROR, pe.getMessage());
			//throw new PageGatewayException(pe);
		}
		
	}

	@Override
	public void doRestart() throws GatewayException {

		engine.log(this,GatewayEnginePro.LOGLEVEL_INFO,"restart");
		Struct args=new StructImpl();
		try{
			boolean has=callOneWay("restart",args);
			if(!has){
				if(callOneWay("stop",args)){
					//engine.clear(cfcPath,id);
					callOneWay("start",args);
				}
			}
		}
		catch(PageException pe){ 
			throw new PageGatewayException(pe);
		}
		
	}

	@Override
	public void doStart() throws GatewayException {
		engine.log(this,GatewayEnginePro.LOGLEVEL_INFO,"start");
		Struct args=new StructImpl();
		state=STARTING;
		try{
			callOneWay("start",args);
			engine.log(this,GatewayEnginePro.LOGLEVEL_INFO,"running");
			state=RUNNING;
		}
		catch(PageException pe){
			state=FAILED;
			throw new PageGatewayException(pe);
		}
	}

	@Override
	public void doStop() throws GatewayException {

		engine.log(this,GatewayEnginePro.LOGLEVEL_INFO,"stop");
		Struct args=new StructImpl();
		state=STOPPING;
		try{
			callOneWay("stop",args);
			//engine.clear(cfcPath,id);
			state=STOPPED;
		}
		catch(PageException pe){
			state=FAILED;
			//engine.clear(cfcPath,id);
			throw new PageGatewayException(pe);
		}
	}

	@Override
	public Object getHelper() {
		Struct args=new StructImpl(StructImpl.TYPE_LINKED);
		return callEL("getHelper",args,null);
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public int getState() {
		Struct args=new StructImpl();
		Integer state=Integer.valueOf(this.state);
		try {
			return GatewayEngineImpl.toIntState(Caster.toString(call("getState",args,state)),this.state);
		} 
		catch (PageException pe) {
			engine.log(this, GatewayEnginePro.LOGLEVEL_ERROR, pe.getMessage());
		}
		return this.state;
	}



	@Override
	public String sendMessage(Map data) throws GatewayException {
		Struct args=new StructImpl(StructImpl.TYPE_LINKED);
		args.setEL("data", Caster.toStruct(data, null, false));
		try {
			return Caster.toString(call("sendMessage",args,""));
		} catch (PageException pe) {
			throw new PageGatewayException(pe);
		}
	}
	
	private Object callEL(String methodName,Struct arguments, Object defaultValue)  {
		return engine.callEL(cfcPath,id, methodName, arguments, true, defaultValue);
	}

	private boolean callOneWay(String methodName,Struct arguments) throws PageException {
		return engine.callOneWay(cfcPath,id, methodName, arguments, true);
	}
	
	private Object call(String methodName,Struct arguments, Object defaultValue) throws PageException  {
		return engine.call(cfcPath,id, methodName, arguments, true, defaultValue);
	}
}
