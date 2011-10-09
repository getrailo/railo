package railo.runtime.orm.hibernate.event;

import org.hibernate.event.PostInsertEvent;
import org.hibernate.event.PostInsertEventListener;

import railo.runtime.Component;

public class PostInsertEventListenerImpl extends EventListener implements PostInsertEventListener {

	private static final long serialVersionUID = -1300488254940330390L;

	public PostInsertEventListenerImpl(Component component) {
	    super(component, POST_INSERT, false);
	}

	public void onPostInsert(PostInsertEvent event) {
		invoke(POST_INSERT, event.getEntity());
    }

}
