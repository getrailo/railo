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

import flex.messaging.config.ConfigMap;
import flex.messaging.messages.Message;
import flex.messaging.services.ServiceAdapter;
//FUTURE make this interface independent from flex.messaging... so that the loader no longer need the flex jar

public interface BlazeDS {
	public void init(ConfigMap properties);
	
	public Object invoke(ServiceAdapter serviceAdapter, Message message);
	
}
