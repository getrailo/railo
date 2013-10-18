package railo.runtime.orm.hibernate.tuplizer;
import org.hibernate.EntityNameResolver;

import railo.runtime.Component;
import railo.runtime.exp.PageException;
import railo.runtime.orm.hibernate.HibernateCaster;
import railo.runtime.orm.hibernate.HibernatePageException;


public  class CFCEntityNameResolver implements EntityNameResolver {
	public static final CFCEntityNameResolver INSTANCE = new CFCEntityNameResolver();

	/**
	 * {@inheritDoc}
	 */
	public String resolveEntityName(Object entity) {
		try {
			Component cfc = HibernateCaster.toComponent(entity);
			return HibernateCaster.getEntityName(cfc);
		} catch (PageException pe) {
			//print.printST(e);
			throw new HibernatePageException(pe);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object obj) {
		return getClass().equals( obj.getClass() );
	}

	/**
	 * {@inheritDoc}
	 */
	public int hashCode() {
		return getClass().hashCode();
	}
}