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

import org.hibernate.event.PostUpdateEvent;
import org.hibernate.event.PostUpdateEventListener;

import railo.runtime.Component;
import railo.runtime.orm.hibernate.CommonUtil;

public class PostUpdateEventListenerImpl extends EventListener implements PostUpdateEventListener {

	private static final long serialVersionUID = -6636253331286381298L;

	public PostUpdateEventListenerImpl(Component component) {
	    super(component, CommonUtil.POST_UPDATE, false);
	}
	
	public void onPostUpdate(PostUpdateEvent event) {
    	invoke(CommonUtil.POST_UPDATE, event.getEntity());
    }

}
