package railo.runtime.gateway.proxy;

import railo.runtime.exp.ApplicationException;
import railo.runtime.gateway.Gateway;

// FUTURE remove this class
public class GatewayFactory {
	
	public static Gateway toGateway(Object obj) throws ApplicationException{
		if(obj instanceof Gateway) 
			return (Gateway) obj;
		throw new ApplicationException("the class ["+obj.getClass().getName()+"] does not implement the interface ["+Gateway.class.getName()+"], make sure you have not multiple implementation of that interface in your classpath");
		
	}
}
