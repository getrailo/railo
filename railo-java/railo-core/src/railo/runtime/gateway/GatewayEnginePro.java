package railo.runtime.gateway;

import java.util.Map;

public interface GatewayEnginePro {
	// FUTURE remove this interface and replace with GatewayEngine

	public static final int LOGLEVEL_INFO=0;
	public static final int LOGLEVEL_DEBUG=1;
	public static final int LOGLEVEL_WARN=2;
	public static final int LOGLEVEL_ERROR=3;
	public static final int LOGLEVEL_FATAL=4;
	
	
	/**
	 * invoke given method on cfc listener 
	 * @param gateway 
	 * @param method method to invoke
	 * @param data arguments
	 * @return returns if invocation was successfull
	 */
	public boolean invokeListener(GatewayPro gateway,String method,Map data);
	
	/**
	 * logs message with defined logger for gateways
	 * @param gateway
	 * @param level
	 * @param message
	 */
	public void log(GatewayPro gateway,int level,String message);
    
}
