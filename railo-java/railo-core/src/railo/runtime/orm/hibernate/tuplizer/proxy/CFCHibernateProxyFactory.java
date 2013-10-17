package railo.runtime.orm.hibernate.tuplizer.proxy;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.ProxyFactory;
import org.hibernate.type.AbstractComponentType;


public class CFCHibernateProxyFactory implements ProxyFactory {
	private String entityName;
	private String nodeName;

	public void postInstantiate(
		final String entityName, 
		final Class persistentClass,
		final Set interfaces, 
		final Method getIdentifierMethod,
		final Method setIdentifierMethod,
		AbstractComponentType componentIdType) throws HibernateException {
		int index=entityName.indexOf('.');
		this.nodeName = entityName;
		this.entityName = entityName.substring(index+1);
	}

	public void postInstantiate(PersistentClass pc) {
		this.nodeName =pc.getNodeName();
		this.entityName =pc.getEntityName();
	}

	public HibernateProxy getProxy(final Serializable id,  final SessionImplementor session) {
		try {
			return new CFCHibernateProxy(new CFCLazyInitializer(entityName, id, session));
		}
		catch(Throwable t){
			return new CFCHibernateProxy(new CFCLazyInitializer(nodeName, id, session));
		}
	}
}