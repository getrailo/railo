package railo.runtime;//.orm.hibernate.tuplizer.proxy;


import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;
import railo.runtime.orm.hibernate.tuplizer.proxy.CFCLazyInitializer;
import railo.runtime.component.Property;
import railo.runtime.type.cfc.ComponentAccess;
import railo.runtime.type.cfc.ComponentAccessProxy;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;

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
	public Iterator<?> getIterator()
	{
		return li.getCFC().getIterator();
	}

	@Override
	public HashMap<String, Property> getAllPersistentProperties()
	{
		return li.getCFC().getAllPersistentProperties();
	}


}