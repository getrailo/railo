package railo.runtime.orm.hibernate.event;

import org.hibernate.event.PreUpdateEvent;
import org.hibernate.event.PreUpdateEventListener;

import railo.runtime.Component;

public class PreUpdateEventListenerImpl extends EventListener implements PreUpdateEventListener {
	
	private static final long serialVersionUID = -2340188926747682946L;

	public PreUpdateEventListenerImpl(Component component) {
	    super(component, PRE_UPDATE, false);
	}
	
	public boolean onPreUpdate(PreUpdateEvent event) {
		return preUpdate(event);
	}

}
