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
package railo.runtime.orm.hibernate.event;

import org.hibernate.event.PreUpdateEvent;
import org.hibernate.event.PreUpdateEventListener;

import railo.runtime.Component;
import railo.runtime.orm.hibernate.CommonUtil;

public class PreUpdateEventListenerImpl extends EventListener implements PreUpdateEventListener {
	
	private static final long serialVersionUID = -2340188926747682946L;

	public PreUpdateEventListenerImpl(Component component) {
	    super(component, CommonUtil.PRE_UPDATE, false);
	}
	
	public boolean onPreUpdate(PreUpdateEvent event) {
		return preUpdate(event);
	}

}
