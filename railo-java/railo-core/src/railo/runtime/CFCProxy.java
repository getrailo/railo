package railo.runtime;//.orm.hibernate.tuplizer.proxy;


import java.io.Serializable;

import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;

import railo.runtime.component.Property;
import railo.runtime.orm.hibernate.tuplizer.proxy.CFCLazyInitializer;
import railo.runtime.type.cfc.ComponentAccess;
import railo.runtime.type.cfc.ComponentAccessProxy;

/**
 * Proxy for "dynamic-map" entity representations.
 * SLOW
 */
public class CFCProxy extends ComponentAccessProxy implements HibernateProxy, Serializable {

	private static final long serialVersionUID = 4115236247834562085L;
	
	private CFCLazyInitializer li;

	public ComponentAccess getComponentAccess() {
		return li.getCFC();
	}
	
	public CFCProxy(CFCLazyInitializer li) {
		this.li = li;
	}

	
	public Object writeReplace() {
		return this;
	}

	public LazyInitializer getHibernateLazyInitializer() {
		return li;
	}

	@Override
	public long sizeOf() {
		return li.getCFC().sizeOf();
	}
	
	@Override
	public java.util.Iterator<String> getIterator() {
    	return keysAsStringIterator();
    }


	@Override
	public Property[] getProperties(boolean onlyPeristent, boolean includeBaseProperties, boolean overrideProperties, boolean inheritedMappedSuperClassOnly) {
		return li.getCFC().getProperties(onlyPeristent, includeBaseProperties, overrideProperties, inheritedMappedSuperClassOnly);
	}
}