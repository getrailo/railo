package railo.runtime.orm.hibernate.event;

import java.io.Serializable;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

import railo.runtime.Component;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.op.Operator;
import railo.runtime.orm.ORMUtil;
import railo.runtime.orm.hibernate.HibernateCaster;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;

public class InterceptorImpl extends EmptyInterceptor {

	private static final long serialVersionUID = 7992972603422833660L;

	private final AllEventListener listener;
	private final boolean hasPreInsert;
	private final boolean hasPreUpdate;

	public InterceptorImpl(AllEventListener listener) {
		this.listener=listener;
		if(listener!=null) {
			Component cfc = listener.getCFC();
			hasPreInsert=EventListener.hasEventType(cfc, EventListener.PRE_INSERT);
			hasPreUpdate=EventListener.hasEventType(cfc, EventListener.PRE_UPDATE);
		}
		else {
			hasPreInsert=false;
			hasPreUpdate=false;
		}
	}

	/**
	 * @see org.hibernate.EmptyInterceptor#onSave(java.lang.Object, java.io.Serializable, java.lang.Object[], java.lang.String[], org.hibernate.type.Type[])
	 */
	public boolean onSave(Object entity, Serializable id, Object[] state,
			String[] propertyNames, Type[] types) {
		
		return on(entity, id, state, null, propertyNames, types, EventListener.PRE_INSERT, hasPreInsert);
		//return super.onSave(entity, id, state, propertyNames, types);
	}

	/**
	 * @see org.hibernate.EmptyInterceptor#onFlushDirty(java.lang.Object, java.io.Serializable, java.lang.Object[], java.lang.Object[], java.lang.String[], org.hibernate.type.Type[])
	 */
	public boolean onFlushDirty(Object entity, Serializable id,
			Object[] currentState, Object[] previousState,
			String[] propertyNames, Type[] types) {
		return on(entity, id, currentState, toStruct(propertyNames, previousState), propertyNames, types, EventListener.PRE_UPDATE, hasPreUpdate);
		//return super.onFlushDirty(entity, id, currentState, previousState, propertyNames, types);
	}
	

	private boolean on(Object entity, Serializable id,
			Object[] state, Struct data,
			String[] propertyNames, Type[] types, Collection.Key eventType, boolean hasMethod) {
		
		Component cfc = Caster.toComponent(entity,null);
		if(cfc!=null && EventListener.hasEventType(cfc, eventType)){
			EventListener.invoke(eventType, cfc, data, null);
		}
		if(hasMethod) 
			EventListener.invoke(eventType, listener.getCFC(), data, entity);
		
		
		boolean rtn=false;
		String prop;
		Object before,current;
        for(int i = 0; i < propertyNames.length; i++)	{
            prop = propertyNames[i];
            before = state[i];
            current = ORMUtil.getPropertyValue(cfc, prop,null);
            
            if(before != current && (current == null || !Operator.equalsEL(before, current, false, true))) {
            	try {
					state[i] = HibernateCaster.toSQL(null, types[i], current,null);
				} catch (PageException e) {
					state[i] = current;
				}
				rtn = true;
            }
        }
        return rtn;	
	}
	
    
	
	
	

    private static Struct toStruct(String propertyNames[], Object state[])  {
        Struct sct = new StructImpl();
        if(state!=null && propertyNames!=null){
        	for(int i = 0; i < propertyNames.length; i++) {
        		sct.setEL(KeyImpl.init(propertyNames[i]), state[i]);
        	}
    	}
        return sct;
    }
}
