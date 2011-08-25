package railo.runtime.orm.hibernate.event;

import org.hibernate.event.PostUpdateEvent;
import org.hibernate.event.PostUpdateEventListener;

import railo.runtime.Component;

public class PostUpdateEventListenerImpl extends EventListener implements PostUpdateEventListener {

	private static final long serialVersionUID = -6636253331286381298L;

	public PostUpdateEventListenerImpl(Component component) {
	    super(component, POST_UPDATE, false);
	}
	
	public void onPostUpdate(PostUpdateEvent event) {
    	invoke(POST_UPDATE, event.getEntity());
    }

}
