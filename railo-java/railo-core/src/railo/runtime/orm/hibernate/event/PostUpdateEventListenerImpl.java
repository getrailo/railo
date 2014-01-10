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
