/**
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library.  If not, see <http://www.gnu.org/licenses/>.
 * 
 **/
package railo.runtime.net.flex;

import javax.servlet.ServletException;

import railo.loader.engine.CFMLEngine;
import railo.loader.engine.CFMLEngineFactory;
import railo.loader.engine.EngineChangeListener;
import railo.runtime.util.BlazeDS;
import flex.messaging.FlexContext;
import flex.messaging.config.ConfigMap;
import flex.messaging.messages.Message;
import flex.messaging.services.ServiceAdapter;

// FUTURE make this class independent from flex.messaging... so that the loader no longer need the flex jar

/**
 * Railo implementation of the ServiceAdapter, forward all BlazeDS Request to the CFMLEngine. 
 */
public class RailoAdapter extends ServiceAdapter implements EngineChangeListener {
	
	public static final short LOWER_CASE=0;
	public static final short UPPER_CASE=1;
	public static final short ORIGINAL_CASE=2;
	
	
	private CFMLEngine engine;
	private ConfigMap properties;
	private BlazeDS util;
    
	public void initialize(String id, ConfigMap properties) {
		super.initialize(id, properties);
        this.properties=properties;
        try{
	        // we call this because otherwse they does not exist (bug in BlazeDS)
	        ConfigMap propertyCases = properties.getPropertyAsMap("property-case", null);
	        if(propertyCases!=null){
	            propertyCases.getPropertyAsBoolean("force-cfc-lowercase", false);
	            propertyCases.getPropertyAsBoolean("force-query-lowercase", false);
	            propertyCases.getPropertyAsBoolean("force-struct-lowercase", false);
	        }
	        ConfigMap access = properties.getPropertyAsMap("access", null);
	        if(access!=null){
	            access.getPropertyAsBoolean("use-mappings", false);
	            access.getPropertyAsString("method-access-level","remote");
	        }
        }
        catch(Throwable t){}
        
    }
	
	
	/**
	 * @see flex.messaging.services.ServiceAdapter#invoke(flex.messaging.messages.Message)
	 */
	public Object invoke(Message message){
		
		try {
			
			if(util==null){
				util = (BlazeDS)getEngine().getBlazeDSUtil();
				util.init(properties);
			}
			
			
			//RemotingDestination remotingDestination = (RemotingDestination)getDestination();
	        //FactoryInstance factoryInstance = remotingDestination.getFactoryInstance();

			
			/*
	        System.out.println("source:"+factoryInstance.getSource());
	        System.out.println("scope:"+factoryInstance.getScope());
	        System.out.println("properties:");
	        System.out.println(factoryInstance.getProperties());

	        System.out.println("dest:"+remotingMessage.getDestination());
	        System.out.println("src:"+remotingMessage.getSource());
	        System.out.println("mid:"+remotingMessage.getMessageId());
	        System.out.println("operation:"+remotingMessage.getOperation());
	        System.out.println("params:");
	        System.out.println(remotingMessage.getParameters().toArray());
	        
	        System.out.println("id:"+remotingDestination.getId());
	        System.out.println("scope:"+remotingDestination.getScope());
	        System.out.println("src:"+remotingDestination.getSource());
	        System.out.println("channel:");
	        System.out.println(remotingDestination.getChannels());
	        */
	        
			
			
			
			return util.invoke(this,message);
		} 
		catch (Exception e) {e.printStackTrace();
			throw new RuntimeException(e);
		}
    }
    
    /**
     * load (if needed) and return the CFMLEngine
     * @return CFML Engine
     */
    private CFMLEngine getEngine() {
    	if(engine==null){
        	try {CFMLEngineFactory.getInstance();
				//engine=CFMLEngineFactory.getInstance(FlexContext.getServletConfig());
				engine=CFMLEngineFactory.getInstance(FlexContext.getServletConfig(),this);
			} 
        	catch (Throwable e) {
				throw new RuntimeException(e);
			}
        }
    	return engine;
	}

	/**
     * @see railo.loader.engine.EngineChangeListener#onUpdate(railo.loader.engine.CFMLEngine)
     */
    public void onUpdate(CFMLEngine newEngine) {
        try {
            engine=CFMLEngineFactory.getInstance(FlexContext.getServletConfig(),this);
        } catch (ServletException e) {
            engine=newEngine;
        }
    }
}
