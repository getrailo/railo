package railo.runtime.gateway.proxy;

import java.util.Map;

import railo.runtime.gateway.Gateway;
import railo.runtime.gateway.GatewayEngine;
import railo.runtime.gateway.GatewayEngineImpl;

public class GatewayEngineProxy implements GatewayEngine {
	
	private GatewayEngineImpl engine;

	public GatewayEngineProxy(GatewayEngineImpl engine){
		this.engine=engine;
	}

	@Override
	public boolean invokeListener(Gateway gateway, String method, Map data) {
		return engine.invokeListener(gateway.getId(), method, data);
	}

	@Override
	public void log(Gateway gateway, int level, String message) {
		engine.log(gateway.getId(), level, message);
	}

	public GatewayEngineImpl getEngine() {
		return engine;
	}

}
