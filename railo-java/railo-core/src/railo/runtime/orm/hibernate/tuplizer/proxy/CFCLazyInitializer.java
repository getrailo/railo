package railo.runtime.orm.hibernate.tuplizer.proxy;

import java.io.Serializable;

import org.hibernate.engine.SessionImplementor;
import org.hibernate.proxy.AbstractLazyInitializer;

import railo.runtime.ComponentImpl;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.orm.hibernate.HibernateRuntimeException;
import railo.runtime.type.util.ComponentUtil;

/**
 * Lazy initializer for "dynamic-map" entity representations.
 *
 * @author Gavin King
 */
public class CFCLazyInitializer extends AbstractLazyInitializer implements Serializable {

	
	CFCLazyInitializer(String entityName, Serializable id, SessionImplementor session) {
		super(entityName, id, session);
		
	}

	public ComponentImpl getCFC() {
		try {
			return ComponentUtil.toComponentImpl(Caster.toComponent(getImplementation()));
		} catch (PageException e) {
			//print.printST(e);
			throw new HibernateRuntimeException(e);
		}
	}

	public Class getPersistentClass() {
		throw new UnsupportedOperationException("dynamic-map entity representation");
	}
	
	
	
}




