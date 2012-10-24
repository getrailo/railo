package railo.runtime.util;

import flex.messaging.config.ConfigMap;
import flex.messaging.messages.Message;
import flex.messaging.services.ServiceAdapter;
//FUTURE make this interface independent from flex.messaging... so that the loader no longer need the flex jar

public interface BlazeDS {
	public void init(ConfigMap properties);
	
	public Object invoke(ServiceAdapter serviceAdapter, Message message);
	
}
