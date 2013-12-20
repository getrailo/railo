package railo.runtime.orm.hibernate.tuplizer.proxy;

import railo.runtime.Component;
import railo.runtime.ComponentPro;

public class CFCProxy extends ComponentProProxy {
	
	private ComponentPro cfc;

	public CFCProxy(Component cfc){
		this.cfc=(ComponentPro)cfc; 
	}
	
	@Override
	public Component getComponent() {
		return cfc;
	}
	
	@Override
	public ComponentPro getComponentPro() {
		return cfc;
	}
	
	public Object put(Object key, Object value) {
		super.put(key,value); // writes to this scope
		return getComponentScope().put(key, value); // writes to variables scope
	}

}
