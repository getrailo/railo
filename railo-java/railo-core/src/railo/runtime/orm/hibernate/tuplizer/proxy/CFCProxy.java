package railo.runtime.orm.hibernate.tuplizer.proxy;

import railo.runtime.Component;

public class CFCProxy extends ComponentProxy {
	
	private Component cfc;

	public CFCProxy(Component cfc){
		this.cfc=cfc; 
	}
	
	@Override
	public Component getComponent() {
		return cfc;
	}
	
	@Override
	public Object put(Object key, Object value) {
		super.put(key,value); // writes to this scope
		return getComponentScope().put(key, value); // writes to variables scope
	}

}
