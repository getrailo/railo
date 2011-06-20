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
import railo.runtime.ComponentPro;
import railo.runtime.PageContext;
import railo.runtime.component.Property;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.op.Caster;
import railo.runtime.orm.hibernate.HibernateUtil;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.util.ComponentUtil;

public class EventListenerImpl 
	implements 	PreDeleteEventListener, PreInsertEventListener, PreLoadEventListener, PreUpdateEventListener,
    			PostDeleteEventListener, PostInsertEventListener, PostLoadEventListener, PostUpdateEventListener {

	private static final long serialVersionUID = -4842481789634140033L;
	
	public static final Collection.Key POST_INSERT=KeyImpl.getInstance("postInsert");
	public static final Collection.Key POST_UPDATE=KeyImpl.getInstance("postUpdate");
	public static final Collection.Key PRE_DELETE=KeyImpl.getInstance("preDelete");
	public static final Collection.Key POST_DELETE=KeyImpl.getInstance("postDelete");
	public static final Collection.Key PRE_LOAD=KeyImpl.getInstance("preLoad");
	public static final Collection.Key POST_LOAD=KeyImpl.getInstance("postLoad");
	public static final Collection.Key PRE_UPDATE=KeyImpl.getInstance("preUpdate");
	public static final Collection.Key PRE_INSERT=KeyImpl.getInstance("preInsert");
	

	
    private ComponentPro component;
	private boolean allEvents;
    
	public EventListenerImpl(Component component, boolean allEvents) {
	       this.component=ComponentUtil.toComponentPro(component,null); 
	       this.allEvents=allEvents; 
    }

    

	public void onPostInsert(PostInsertEvent event) {
        invoke(POST_INSERT, event.getEntity());
    }

    public void onPostUpdate(PostUpdateEvent event) {
    	invoke(POST_UPDATE, event.getEntity());
    }

    public boolean onPreDelete(PreDeleteEvent event) {
        invoke(PRE_DELETE, event.getEntity());
		return false;
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
		Struct oldData=new StructImpl();
		Property[] properties = HibernateUtil.getProperties(component,HibernateUtil.FIELDTYPE_COLUMN,null);
		Object[] data = event.getOldState();
		
		if(data!=null && properties!=null && data.length==properties.length) {
			for(int i=0;i<data.length;i++){
				oldData.setEL(KeyImpl.getInstance(properties[i].getName()), data[i]);
			}
		}
		
		invoke(PRE_UPDATE, event.getEntity(),oldData);
		return false;
	}



	public boolean onPreInsert(PreInsertEvent event) {
		invoke(PRE_INSERT, event.getEntity());
		return false;
	}
    public Component getCFC() {
		return component;
	}
    

    private void invoke(Collection.Key name, Object obj) {
    	invoke(name, obj,null);
    }
    private void invoke(Collection.Key name, Object obj, Struct data) {
    	Component c=allEvents?component:Caster.toComponent(obj,null);
    	if(c==null) return;
    	
		try {
			PageContext pc = ThreadLocalPageContext.get();
			Object[] args;
			if(data==null) {
				args=allEvents?new Object[]{obj}:new Object[]{};
			}
			else {
				args=allEvents?new Object[]{obj,data}:new Object[]{data};
			}
			if(!allEvents) {
				if(!ComponentUtil.toComponentPro(component).getPageSource().equals(ComponentUtil.toComponentPro(obj).getPageSource()))
					return;
			}
			c.call(pc, name, args);
		}
		catch (Throwable e) {}
	}
    
}