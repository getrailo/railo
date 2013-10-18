package railo.runtime.orm.hibernate.event;

import org.hibernate.event.PreDeleteEvent;
import org.hibernate.event.PreDeleteEventListener;

import railo.runtime.Component;
import railo.runtime.orm.hibernate.CommonUtil;

public class PreDeleteEventListenerImpl extends EventListener implements PreDeleteEventListener {

	private static final long serialVersionUID = 1730085093470940646L;

	public PreDeleteEventListenerImpl(Component component) {
	    super(component, CommonUtil.PRE_DELETE, false);
	}

    public boolean onPreDelete(PreDeleteEvent event) {
    	invoke(CommonUtil.PRE_DELETE, event.getEntity());
		return false;
    }

}
