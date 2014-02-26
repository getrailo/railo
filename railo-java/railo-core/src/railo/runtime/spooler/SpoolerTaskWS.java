package railo.runtime.spooler;

import railo.runtime.config.Config;
import railo.runtime.config.RemoteClient;
import railo.runtime.exp.PageException;
import railo.runtime.net.rpc.client.WSClient;
import railo.runtime.op.Caster;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;

public abstract class SpoolerTaskWS extends SpoolerTaskSupport {
	
	private RemoteClient client;
    
    
	public SpoolerTaskWS(ExecutionPlan[] plans,RemoteClient client) {
		super(plans);
		this.client=client;
	}

	@Override
	public final Object execute(Config config) throws PageException {
		try {
			WSClient rpc = 
				WSClient.getInstance(null,client.getUrl(),client.getServerUsername(),client.getServerPassword(),client.getProxyData());
			
			return rpc.callWithNamedValues(config, KeyImpl.init(getMethodName()), getArguments());
		} 
		catch (Throwable t) {
			throw Caster.toPageException(t);
		}
	}
	
	@Override
	public String subject() {
		return client.getLabel();
	}

	@Override
	public Struct detail() {
		Struct sct=new StructImpl();
		sct.setEL("label", client.getLabel());
		sct.setEL("url", client.getUrl());
		
		return sct;
	}

	protected abstract String getMethodName();
	protected abstract Struct getArguments();
}
