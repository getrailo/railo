package railo.runtime.orm.hibernate.event;

import org.hibernate.event.PreDeleteEvent;
import org.hibernate.event.PreDeleteEventListener;

import railo.runtime.Component;

public class PreDeleteEventListenerImpl extends EventListener implements PreDeleteEventListener {

	private static final long serialVersionUID = 1730085093470940646L;

	public PreDeleteEventListenerImpl(Component component) {
	    super(component, PRE_DELETE, false);
	}

    public boolean onPreDelete(PreDeleteEvent event) {
    	invoke(PRE_DELETE, event.getEntity());
		return false;
    }

}
