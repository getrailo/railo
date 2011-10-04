package railo.runtime.orm.hibernate.event;

import org.hibernate.event.PreInsertEvent;
import org.hibernate.event.PreInsertEventListener;

import railo.runtime.Component;

public class PreInsertEventListenerImpl extends EventListener implements PreInsertEventListener {
	
	private static final long serialVersionUID = -808107633829478391L;

	public PreInsertEventListenerImpl(Component component) {
	    super(component, PRE_INSERT, false);
	}
	
	public boolean onPreInsert(PreInsertEvent event) {
		invoke(PRE_INSERT, event.getEntity());
		return false;
	}

}
