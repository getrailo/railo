package railo.runtime.gateway.proxy;

import railo.runtime.gateway.Gateway;
import railo.runtime.gateway.GatewayEngine;
import railo.runtime.gateway.GatewayEngineImpl;
import railo.runtime.gateway.GatewayEnginePro;
import railo.runtime.gateway.GatewayPro;

// FUTURE remove this class
public class GatewayProFactory {
	
	public static GatewayPro toGatewayPro(Gateway gateway){
		return new GatewayProxy(gateway);
	}
	

	public static Gateway toGateway(GatewayPro gateway){
		return ((GatewayProxy)gateway).getGateway();
	}
	
	public static GatewayPro toGatewayPro(Object gateway){
		if(gateway instanceof GatewayPro) return (GatewayPro) gateway;
		return new GatewayProxy(gateway);
	}

	public static GatewayEngineImpl toGatewayEngineImpl(GatewayEnginePro engine) {
		if(engine instanceof GatewayEngineImpl)return (GatewayEngineImpl) engine;
		return ((GatewayEngineProxy) engine).getEngine();
	}


	public static GatewayEngine toGatewayEngine(GatewayEnginePro engine) {
		return new GatewayEngineProxy((GatewayEngineImpl) engine);
	}
}
