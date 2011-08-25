package railo.runtime.orm.hibernate.event;

import org.hibernate.event.PostLoadEvent;
import org.hibernate.event.PostLoadEventListener;

import railo.runtime.Component;

public class PostLoadEventListenerImpl extends EventListener implements PostLoadEventListener {

	private static final long serialVersionUID = -3211504876360671598L;

	public PostLoadEventListenerImpl(Component component) {
	    super(component, POST_LOAD, false);
	}

	public void onPostLoad(PostLoadEvent event) {
    	invoke(POST_LOAD, event.getEntity());
    }

}
