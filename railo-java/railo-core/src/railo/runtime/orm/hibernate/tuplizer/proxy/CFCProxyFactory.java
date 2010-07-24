package railo.runtime.orm.hibernate.tuplizer.proxy;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.ProxyFactory;
import org.hibernate.type.AbstractComponentType;

import railo.runtime.CFCProxy;

/**
 * @author Gavin King
 */
public class CFCProxyFactory implements ProxyFactory {
	private String entityName;

	public void postInstantiate(
		final String entityName, 
		final Class persistentClass,
		final Set interfaces, 
		final Method getIdentifierMethod,
		final Method setIdentifierMethod,
		AbstractComponentType componentIdType) 
	throws HibernateException {
		
		this.entityName = entityName;

	}

	public HibernateProxy getProxy(
		final Serializable id, 
		final SessionImplementor session)
	throws HibernateException {
		return new CFCProxy( new CFCLazyInitializer(entityName, id, session) );
	}


}