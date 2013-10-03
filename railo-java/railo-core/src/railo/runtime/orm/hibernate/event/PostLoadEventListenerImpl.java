package railo.runtime.orm.hibernate.event;

import org.hibernate.event.PostLoadEvent;
import org.hibernate.event.PostLoadEventListener;

import railo.runtime.Component;
import railo.runtime.orm.hibernate.CommonUtil;

public class PostLoadEventListenerImpl extends EventListener implements PostLoadEventListener {

	private static final long serialVersionUID = -3211504876360671598L;

	public PostLoadEventListenerImpl(Component component) {
	    super(component, CommonUtil.POST_LOAD, false);
	}

	public void onPostLoad(PostLoadEvent event) {
    	invoke(CommonUtil.POST_LOAD, event.getEntity());
    }

}
