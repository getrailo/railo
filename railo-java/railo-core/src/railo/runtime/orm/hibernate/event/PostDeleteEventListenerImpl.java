package railo.runtime.orm.hibernate.event;

import org.hibernate.event.PostDeleteEvent;
import org.hibernate.event.PostDeleteEventListener;

import railo.runtime.Component;

public class PostDeleteEventListenerImpl extends EventListener implements PostDeleteEventListener {

	private static final long serialVersionUID = -4882488527866603549L;

	public PostDeleteEventListenerImpl(Component component) {
	    super(component, POST_DELETE, false);
	}

	public void onPostDelete(PostDeleteEvent event) {
    	invoke(POST_DELETE, event.getEntity());
    }

}
