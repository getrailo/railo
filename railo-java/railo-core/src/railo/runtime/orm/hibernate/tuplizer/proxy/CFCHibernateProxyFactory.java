/**
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library.  If not, see <http://www.gnu.org/licenses/>.
 * 
 **/
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