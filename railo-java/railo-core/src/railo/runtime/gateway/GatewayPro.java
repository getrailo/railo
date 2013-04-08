package railo.runtime.gateway;

import java.io.IOException;
import java.util.Map;

public interface GatewayPro {
// FUTURE remove this interface and replace with Gateway    
	public static final int STARTING = 1;
    public static final int RUNNING = 2;
    public static final int STOPPING = 3;
    public static final int STOPPED = 4;
    public static final int FAILED = 5;
    
    /**
     * method to initialize the gateway
     * @param engine the gateway engine
     * @param id the id of the gateway 
     * @param cfcPath the path to the listener component
     * @param config the configuration as map
     */
    public void init(GatewayEnginePro engine,String id, String cfcPath,Map config) throws IOException;

    /**
     * returns the id of the gateway
     * @return the id of the gateway
     */
    public String getId();
    
    /**
     * sends a message based on given data
     * @param data
     * @return answer from gateway
     */
    public String sendMessage(Map data) throws IOException;
    
    /**
     * return helper object
     * @return helper object
     */
    public Object getHelper();
    
    /**
     * starts the gateway
     * @throws GatewayException
     */
    public void doStart() throws IOException;
    
    /**
     * stop the gateway
     * @throws GatewayException
     */
    public void doStop() throws IOException;
    
    /**
     * restart the gateway
     * @throws GatewayException
     */
    public void doRestart() throws IOException;
    
    /**
     * returns a string that is used by the event gateway administrator to display status
     * @return status (STARTING, RSTOPPING, STOPPED, FAILED)
     */
    public int getState();
}
