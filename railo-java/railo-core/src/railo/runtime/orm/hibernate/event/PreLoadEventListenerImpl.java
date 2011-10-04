package railo.runtime.orm.hibernate.event;

import org.hibernate.event.PreLoadEvent;
import org.hibernate.event.PreLoadEventListener;

import railo.runtime.Component;

public class PreLoadEventListenerImpl extends EventListener implements PreLoadEventListener {

	private static final long serialVersionUID = 6470830422058063880L;

	public PreLoadEventListenerImpl(Component component) {
	    super(component, PRE_LOAD, false);
	}

    public void onPreLoad(PreLoadEvent event) {
    	invoke(PRE_LOAD, event.getEntity());
    }

}
