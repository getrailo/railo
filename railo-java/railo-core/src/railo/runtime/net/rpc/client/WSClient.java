package railo.runtime.net.rpc.client;

import org.apache.axis.client.Call;
import org.apache.axis.message.SOAPHeaderElement;
import org.apache.log4j.Logger;

import railo.runtime.PageContext;
import railo.runtime.config.Config;
import railo.runtime.config.ConfigImpl;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.PageException;
import railo.runtime.listener.ApplicationContext;
import railo.runtime.net.proxy.ProxyData;
import railo.runtime.type.Collection;
import railo.runtime.type.Iteratorable;
import railo.runtime.type.Objects;
import railo.runtime.type.Struct;

public abstract class WSClient implements Objects, Iteratorable {
	
	public static WSClient getInstance(PageContext pc,String wsdlUrl, String username, String password, ProxyData proxyData) throws PageException {
		pc=ThreadLocalPageContext.get(pc);
		if(pc!=null) {
			Logger l = ((ConfigImpl)pc.getConfig()).getLogger("application", true);
			ApplicationContext ac = pc.getApplicationContext();
			if(ac!=null) {
				if(ApplicationContext.WS_TYPE_JAX_WS==ac.getWSType()) {
					l.info("using JAX WS Client");
					return new JaxWSClient(wsdlUrl, username, password, proxyData);
				}
				if(ApplicationContext.WS_TYPE_CXF==ac.getWSType()) {
					l.info("using CXF Client");
					return new CXFClient(wsdlUrl, username, password, proxyData);
				}
			}
			l.info("using Axis 1 RPC Client");
		}
		return new Axis1Client(wsdlUrl,username,password,proxyData);
	}
	
	
	
	public abstract void addHeader(SOAPHeaderElement header) throws PageException;
	public abstract Call getLastCall()throws PageException;
	public abstract Object callWithNamedValues(Config config, Collection.Key methodName, Struct arguments) throws PageException;
	public abstract Object callWithNamedValues(PageContext pc, Collection.Key methodName, Struct arguments) throws PageException;
	    
}
