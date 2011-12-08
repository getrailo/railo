package railo.runtime.orm.hibernate.event;

import java.io.Serializable;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

import railo.runtime.Component;
import railo.runtime.ComponentPro;
import railo.runtime.op.Caster;
import railo.runtime.type.util.ComponentUtil;

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
		
		Component cfc = Caster.toComponent(entity,null);
		if(cfc!=null && EventListener.hasEventType(cfc, EventListener.PRE_INSERT))
			EventListener.invoke(EventListener.PRE_INSERT, cfc, null, null);
		
		if(hasPreInsert) 
			EventListener.invoke(EventListener.PRE_INSERT, listener.getCFC(), null, entity);
		
		return super.onSave(entity, id, state, propertyNames, types);
	}

	/**
	 * @see org.hibernate.EmptyInterceptor#onFlushDirty(java.lang.Object, java.io.Serializable, java.lang.Object[], java.lang.Object[], java.lang.String[], org.hibernate.type.Type[])
	 */
	public boolean onFlushDirty(Object entity, Serializable id,
			Object[] currentState, Object[] previousState,
			String[] propertyNames, Type[] types) {
		
		Component cfc = Caster.toComponent(entity,null);
		if(cfc!=null && EventListener.hasEventType(cfc, EventListener.PRE_UPDATE))
			EventListener.invoke(EventListener.PRE_UPDATE, cfc, null, null);
		
		if(hasPreUpdate) 
			EventListener.invoke(EventListener.PRE_UPDATE, listener.getCFC(), null, entity);
		
		return super.onFlushDirty(entity, id, currentState, previousState, propertyNames, types);
	}

}
