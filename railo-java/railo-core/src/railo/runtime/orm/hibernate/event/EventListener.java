package railo.runtime.orm.hibernate.event;

import org.hibernate.event.PreUpdateEvent;

import railo.runtime.Component;
import railo.runtime.PageContext;
import railo.runtime.component.Property;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.PageException;
import railo.runtime.exp.PageRuntimeException;
import railo.runtime.op.Caster;
import railo.runtime.orm.hibernate.HibernateUtil;
import railo.runtime.type.Collection;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.UDF;
import railo.runtime.type.util.ComponentUtil;

public abstract class EventListener {

	private static final long serialVersionUID = -4842481789634140033L;
	
	public static final Collection.Key POST_INSERT=KeyImpl.intern("postInsert");
	public static final Collection.Key POST_UPDATE=KeyImpl.intern("postUpdate");
	public static final Collection.Key PRE_DELETE=KeyImpl.intern("preDelete");
	public static final Collection.Key POST_DELETE=KeyImpl.intern("postDelete");
	public static final Collection.Key PRE_LOAD=KeyImpl.intern("preLoad");
	public static final Collection.Key POST_LOAD=KeyImpl.intern("postLoad");
	public static final Collection.Key PRE_UPDATE=KeyImpl.intern("preUpdate");
	public static final Collection.Key PRE_INSERT=KeyImpl.intern("preInsert");
	

	
    protected Component component;

    private boolean allEvents;
	private Key eventType;
    
	public EventListener(Component component, Key eventType, boolean allEvents) {
	       this.component=component; 
	       this.allEvents=allEvents; 
	       this.eventType=eventType;
    }
	
	protected boolean preUpdate(PreUpdateEvent event) {
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

    
    public Component getCFC() {
		return component;
	}
    

    protected void invoke(Collection.Key name, Object obj) {
    	invoke(name, obj,null);
    }
    protected void invoke(Collection.Key name, Object obj, Struct data) {
    	if(eventType!=null && !eventType.equals(name)) return;
    	//print.e(name);
    	Component caller = Caster.toComponent(obj,null);
    	Component c=allEvents?component:caller;
    	if(c==null) return;
    	
    	if(!allEvents &&!caller.getPageSource().equals(component.getPageSource())) return;
		invoke(name, c, data, allEvents?obj:null);
    	
	}
    
    public static void invoke(Key name, Component cfc, Struct data, Object arg) {
    	if(cfc==null) return;
    	
		try {
			PageContext pc = ThreadLocalPageContext.get();
			Object[] args;
			if(data==null) {
				args=arg!=null?new Object[]{arg}:new Object[]{};
			}
			else {
				args=arg!=null?new Object[]{arg,data}:new Object[]{data};
			}
			cfc.call(pc, name, args);
		}
		catch (PageException pe) {
			throw new PageRuntimeException(pe);
		}
	}
    
	public static boolean hasEventType(Component cfc, Collection.Key eventType) {
		return cfc.get(eventType,null) instanceof UDF;
	}
}