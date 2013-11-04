package railo.runtime.gateway.proxy;

import railo.runtime.exp.ApplicationException;
import railo.runtime.gateway.Gateway;
import railo.runtime.gateway.GatewayEngine;
import railo.runtime.gateway.GatewayEngineImpl;
import railo.runtime.gateway.GatewayEnginePro;
import railo.runtime.gateway.GatewayPro;

// FUTURE remove this class
public class GatewayProFactory {
	
	public static Gateway toGateway(GatewayPro gateway){
		return ((GatewayProxy)gateway).getGateway();
	}
	
	public static GatewayPro toGatewayPro(Object obj) throws ApplicationException{
		if(obj instanceof GatewayPro) 
			return (GatewayPro) obj;
		if(obj instanceof Gateway) 
			return new GatewayProxy((Gateway)obj);
		throw new ApplicationException("the class ["+obj.getClass().getName()+"] does not implement the interface ["+Gateway.class.getName()+"], make sure you have not multiple implementation of that interface in your classpath");
		
	}

	public static GatewayEngineImpl toGatewayEngineImpl(GatewayEnginePro engine) {
		if(engine instanceof GatewayEngineImpl)return (GatewayEngineImpl) engine;
		return ((GatewayEngineProxy) engine).getEngine();
	}


	public static GatewayEngine toGatewayEngine(GatewayEnginePro engine) {
		return new GatewayEngineProxy((GatewayEngineImpl) engine);
	}
}
