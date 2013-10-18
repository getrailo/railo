package railo.runtime.orm.hibernate.tuplizer.proxy;

import railo.runtime.Component;
import railo.runtime.type.cfc.ComponentAccess;
import railo.runtime.type.util.ComponentUtil;

public class CFCProxy extends ComponentAccessProxy {
	
	private ComponentAccess ca;

	public CFCProxy(Component cfc){
		this.ca=ComponentUtil.toComponentAccess(cfc,null); 
	}

	@Override
	public ComponentAccess getComponentAccess() {
		return ca;
	}
	
	public Object put(Object key, Object value) {
		super.put(key,value); // writes to this scope
		return getComponentScope().put(key, value); // writes to variables scope
	}

}
