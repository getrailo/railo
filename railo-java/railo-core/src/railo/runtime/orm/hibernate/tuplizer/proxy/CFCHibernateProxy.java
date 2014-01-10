package railo.runtime.orm.hibernate.tuplizer.proxy;

import java.io.Serializable;

import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;

import railo.runtime.type.cfc.ComponentAccess;

/**
 * Proxy for "dynamic-map" entity representations.
 * SLOW
 */
public class CFCHibernateProxy extends ComponentAccessProxy implements HibernateProxy, Serializable {

	private static final long serialVersionUID = 4115236247834562085L;
	
	private CFCLazyInitializer li;

	public ComponentAccess getComponentAccess() {
		return li.getCFC();
	}
	
	public CFCHibernateProxy(CFCLazyInitializer li) {
		this.li = li;
	}

	
	public Object writeReplace() {
		return this;
	}

	public LazyInitializer getHibernateLazyInitializer() {
		return li;
	}
}