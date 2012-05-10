package railo.runtime.type.cfc;

import java.util.Iterator;

import railo.runtime.type.Collection.Key;
import railo.runtime.type.it.ValueIterator;

public class ComponentAccessValueIterator extends ValueIterator implements Iterator<Object> {

	private ComponentAccess cfc;
	private int access;

	public ComponentAccessValueIterator(ComponentAccess cfc, Key[] keys, int access) { 
		super(cfc,keys);
		this.cfc=cfc;
		this.access=access;
	}

	@Override
	public Object next() {
		Key key = keys[pos++];
		if(key==null) return null;
		return cfc.get(access,key,null);
	}
}
