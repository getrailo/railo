package railo.runtime.net.flex;

import flex.messaging.messages.Message;
import flex.messaging.services.ServiceAdapter;

public class HelloWorldAdapter extends ServiceAdapter {    
	
	/**
	 * @see flex.messaging.services.ServiceAdapter#invoke(flex.messaging.messages.Message)
	 */
	public Object invoke(Message message){
		return "HelloWorld";
    }
}
