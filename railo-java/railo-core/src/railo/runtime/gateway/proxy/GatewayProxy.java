package railo.runtime.gateway.proxy;

import java.io.IOException;
import java.util.Map;

import railo.runtime.gateway.Gateway;
import railo.runtime.gateway.GatewayEnginePro;
import railo.runtime.gateway.GatewayPro;

public class GatewayProxy implements GatewayPro {
	
	private final Gateway gateway;

	public GatewayProxy(Gateway gateway){
		this.gateway=gateway;
	}
	
	@Override
	public void init(GatewayEnginePro engine, String id, String cfcPath, Map config) throws IOException {
		gateway.init(GatewayProFactory.toGatewayEngine(engine), id, cfcPath, config);
	}


	@Override
	public String getId() {
		return gateway.getId();
	}

	@Override
	public String sendMessage(Map data) throws IOException {
		return gateway.sendMessage(data);
	}

	@Override
	public Object getHelper() {
		return gateway.getHelper();
	}

	@Override
	public void doStart() throws IOException {
		gateway.doStart();
	}

	@Override
	public void doStop() throws IOException {
		gateway.doStop();
	}

	@Override
	public void doRestart() throws IOException {
		gateway.doRestart();
	}

	@Override
	public int getState() {
		return gateway.getState();
	}

	public Gateway getGateway() {
		return gateway;
	}
}
