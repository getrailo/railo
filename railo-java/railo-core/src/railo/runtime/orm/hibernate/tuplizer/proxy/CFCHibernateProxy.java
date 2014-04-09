package railo.runtime.orm.hibernate.tuplizer.proxy;

import java.io.Serializable;

import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;

import railo.runtime.Component;
import railo.runtime.exp.PageException;
import railo.runtime.type.ObjectWrap;




/**
 * Proxy for "dynamic-map" entity representations.
 * SLOW
 */
public class CFCHibernateProxy extends ComponentProxy implements HibernateProxy, Serializable,ObjectWrap {

	private static final long serialVersionUID = 4115236247834562085L;
	
	private CFCLazyInitializer li;
	
	@Override
	public Component getComponent() {
		return li.getCFC();
	}
	
	public CFCHibernateProxy(CFCLazyInitializer li) {
		this.li = li;
	}

	@Override
	public Object writeReplace() {
		return this;
	}

	@Override
	public LazyInitializer getHibernateLazyInitializer() {
		return li;
	}

	@Override
	public Object getEmbededObject(Object defaultValue) {
		return getComponent();
	}

	@Override
	public Object getEmbededObject() throws PageException {
		return getComponent();
	}
}