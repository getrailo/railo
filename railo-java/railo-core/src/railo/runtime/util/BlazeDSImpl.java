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
package railo.runtime.util;

import railo.runtime.exp.PageException;
import railo.runtime.net.amf.CFMLProxy;
import railo.runtime.op.Caster;
import flex.messaging.FlexContext;
import flex.messaging.MessageException;
import flex.messaging.config.ConfigMap;
import flex.messaging.messages.Message;
import flex.messaging.messages.RemotingMessage;
import flex.messaging.services.ServiceAdapter;

public class BlazeDSImpl implements BlazeDS {
	

	private ConfigMap properties;


	public Object invoke(ServiceAdapter serviceAdapter, Message message){
		
		
        //RemotingDestination remotingDestination = (RemotingDestination)serviceAdapter.getDestination();
        RemotingMessage remotingMessage = (RemotingMessage)message;
        //FactoryInstance factoryInstance = remotingDestination.getFactoryInstance();
        /*
        print.out("className:"+remotingMessage.getSource());
        print.out("methodName:"+remotingMessage.getOperation());
        print.out("params:");
        print.out(remotingMessage.getParameters().toArray());
         */      
        try {
			Object rtn = new CFMLProxy().invokeBody(
					null,properties, 
					FlexContext.getServletContext(),
					FlexContext.getServletConfig(), 
					FlexContext.getHttpRequest(), 
					FlexContext.getHttpResponse(), 
					remotingMessage.getSource(), 
					remotingMessage.getOperation(), 
					remotingMessage.getParameters());
			
	        return rtn;
		} 
        catch (Exception e) {
        	e.printStackTrace();// TODO
        	String msg=e.getMessage();
        	
        	MessageException me = new MessageException(Caster.toClassName(e) + " : " + msg);
        	me.setRootCause(e);
            me.setCode("Server.Processing");
            me.setRootCause(e);
            
            if(e instanceof PageException){
            	PageException pe=(PageException) e;
            	me.setDetails(pe.getDetail());
            	me.setMessage(pe.getMessage());
            	me.setCode(pe.getErrorCode());
            }
            
            throw me;
		}
    }


	/**
	 * @see railo.runtime.util.BlazeDS#init(flex.messaging.config.ConfigMap)
	 */
	public void init(ConfigMap properties) {
		this.properties=properties;
	}
}
