package railo.runtime.orm.hibernate.event;
import org.hibernate.event.PostDeleteEvent;
import org.hibernate.event.PostDeleteEventListener;
import org.hibernate.event.PostInsertEvent;
import org.hibernate.event.PostInsertEventListener;
import org.hibernate.event.PostLoadEvent;
import org.hibernate.event.PostLoadEventListener;
import org.hibernate.event.PostUpdateEvent;
import org.hibernate.event.PostUpdateEventListener;
import org.hibernate.event.PreDeleteEvent;
import org.hibernate.event.PreDeleteEventListener;
import org.hibernate.event.PreInsertEvent;
import org.hibernate.event.PreInsertEventListener;
import org.hibernate.event.PreLoadEvent;
import org.hibernate.event.PreLoadEventListener;
import org.hibernate.event.PreUpdateEvent;
import org.hibernate.event.PreUpdateEventListener;

import railo.runtime.Component;
import railo.runtime.PageContext;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.op.Caster;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;

public class EventListenerImpl 
	implements 	PreDeleteEventListener, PreInsertEventListener, PreLoadEventListener, PreUpdateEventListener,
    			PostDeleteEventListener, PostInsertEventListener, PostLoadEventListener, PostUpdateEventListener {

	public static final Collection.Key POST_INSERT=KeyImpl.getInstance("postInsert");
	public static final Collection.Key POST_UPDATE=KeyImpl.getInstance("postUpdate");
	public static final Collection.Key PRE_DELETE=KeyImpl.getInstance("preDelete");
	public static final Collection.Key POST_DELETE=KeyImpl.getInstance("postDelete");
	public static final Collection.Key PRE_LOAD=KeyImpl.getInstance("preLoad");
	public static final Collection.Key POST_LOAD=KeyImpl.getInstance("postLoad");
	public static final Collection.Key PRE_UPDATE=KeyImpl.getInstance("preUpdate");
	public static final Collection.Key PRE_INSERT=KeyImpl.getInstance("preInsert");
	

	
    private Component component;
	private boolean allEvents;
    
	public EventListenerImpl(Component component, boolean allEvents) {
	       this.component=component; 
	       this.allEvents=allEvents; 
    }

    

	public void onPostInsert(PostInsertEvent event) {
        invoke(POST_INSERT, event.getEntity());
    }

    public void onPostUpdate(PostUpdateEvent event) {
    	invoke(POST_UPDATE, event.getEntity());
    }

    public boolean onPreDelete(PreDeleteEvent event) {
        return invoke(PRE_DELETE, event.getEntity());
    }

    public void onPostDelete(PostDeleteEvent event) {
    	invoke(POST_DELETE, event.getEntity());
    }

    public void onPreLoad(PreLoadEvent event) {
    	invoke(PRE_LOAD, event.getEntity());
    }

    public void onPostLoad(PostLoadEvent event) {
    	invoke(POST_LOAD, event.getEntity());
    }

	public boolean onPreUpdate(PreUpdateEvent event) {
		// MUST olddata -> preUpdate(Struct oldData)
		return invoke(PRE_UPDATE, event.getEntity());
	}



	public boolean onPreInsert(PreInsertEvent event) {
		return invoke(PRE_INSERT, event.getEntity());
	}
    public Component getCFC() {
		return component;
	}
    
    
    private boolean invoke(Collection.Key name, Object obj) {
    	Component c=allEvents?component:Caster.toComponent(obj,null);
    	if(c==null) return false;
    	
		try {
			PageContext pc = ThreadLocalPageContext.get();
			Object[] args=allEvents?new Object[]{obj}:new Object[]{};
			
			if(!allEvents) {
				if(!component.getPageSource().equals(Caster.toComponent(obj).getPageSource()))
					return true;
			}
			return Caster.toBooleanValue(c.call(pc, name, args),false);
		}
		catch (Throwable e) {}
		
    	return false;
	}
    
}