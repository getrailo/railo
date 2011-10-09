package railo.runtime.orm.hibernate.tuplizer.proxy;

import java.io.Serializable;

import org.hibernate.engine.SessionImplementor;
import org.hibernate.proxy.AbstractLazyInitializer;

import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.orm.hibernate.HibernateRuntimeException;
import railo.runtime.type.cfc.ComponentAccess;
import railo.runtime.type.util.ComponentUtil;

/**
 * Lazy initializer for "dynamic-map" entity representations.
 * SLOW
 */
public class CFCLazyInitializer extends AbstractLazyInitializer implements Serializable {

	
	CFCLazyInitializer(String entityName, Serializable id, SessionImplementor session) {
		super(entityName, id, session);
		
	}

	public ComponentAccess getCFC() {
		try {
			return ComponentUtil.toComponentAccess(Caster.toComponent(getImplementation()));
		} catch (PageException e) {
			throw new HibernateRuntimeException(e);
		}
	}

	public Class getPersistentClass() {
		throw new UnsupportedOperationException("dynamic-map entity representation");
	}
	
	
	
}




