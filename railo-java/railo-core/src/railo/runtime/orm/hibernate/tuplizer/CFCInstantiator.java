package railo.runtime.orm.hibernate.tuplizer;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.hibernate.mapping.PersistentClass;
import org.hibernate.tuple.Instantiator;

import railo.runtime.Component;
import railo.runtime.PageContextImpl;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.orm.hibernate.HibernateCaster;
import railo.runtime.orm.hibernate.HibernateORMEngine;
import railo.runtime.orm.hibernate.HibernateORMSession;
import railo.runtime.orm.hibernate.HibernateRuntimeException;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;

public class CFCInstantiator implements Instantiator {
	
	private static final Collection.Key INIT = KeyImpl.getInstance("init");
	private String entityName;
	private Set<String> isInstanceEntityNames = new HashSet<String>();
	
	public CFCInstantiator() {
		this.entityName = null;
	}

	/**
	 * Constructor of the class
	 * @param mappingInfo
	 */
	public CFCInstantiator(PersistentClass mappingInfo) {
		this.entityName = mappingInfo.getEntityName();
		isInstanceEntityNames.add( entityName );
		if ( mappingInfo.hasSubclasses() ) {
			Iterator<PersistentClass> itr = mappingInfo.getSubclassClosureIterator();
			while ( itr.hasNext() ) {
				final PersistentClass subclassInfo = itr.next();
				isInstanceEntityNames.add( subclassInfo.getEntityName() );
			}
		}
	}

	/**
	 * @see org.hibernate.tuple.Instantiator#instantiate(java.io.Serializable)
	 */
	public final Object instantiate(Serializable id) {
		return instantiate();
	}

	/**
	 * @see org.hibernate.tuple.Instantiator#instantiate()
	 */
	public final Object instantiate() {
		try {
			PageContextImpl pc = (PageContextImpl) ThreadLocalPageContext.get();
			HibernateORMSession session=(HibernateORMSession) pc.getORMSession(true);
			HibernateORMEngine engine=(HibernateORMEngine) session.getEngine();
			return engine.create(pc, session, entityName, true);
		} 
		catch (PageException e) {
			throw new HibernateRuntimeException(e);
		}
	}

	/**
	 * @see org.hibernate.tuple.Instantiator#isInstance(java.lang.Object)
	 */
	public final boolean isInstance(Object object) {
		Component cfc = Caster.toComponent(object,null);
		if(cfc==null) return false;
		return isInstanceEntityNames.contains( HibernateCaster.getEntityName(cfc));
	}
}