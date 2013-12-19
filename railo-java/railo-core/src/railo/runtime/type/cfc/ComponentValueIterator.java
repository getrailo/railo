package railo.runtime.type.cfc;

import java.util.Iterator;

import railo.runtime.Component;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.it.ValueIterator;
import railo.runtime.type.util.ComponentProUtil;

public class ComponentValueIterator extends ValueIterator implements Iterator<Object> {

	private Component cfc;
	private int access;

	public ComponentValueIterator(Component cfc, Key[] keys, int access) { 
		super(cfc,keys);
		this.cfc=cfc;
		this.access=access;
	}

	@Override
	public Object next() {
		Key key = keys[pos++];
		if(key==null) return null;
		return ComponentProUtil.get(cfc,access,key,null);
	}
}
