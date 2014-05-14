package railo.runtime;

import java.util.Iterator;

import railo.runtime.exp.PageException;
import railo.runtime.type.Collection;
import railo.runtime.type.Objects;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.scope.Scope;
import railo.runtime.type.util.StructSupport;

public class StaticScope extends StructSupport implements Scope,Objects {

	private final StaticScope base;
	private final Struct data;

	public StaticScope(StaticScope base) {
		this.base=base;
		// top scope
		if(base==null) {
			this.data=new StructImpl();
		}
		else {
			this.data=base.data;
		}
		
	}
	
	@Override
	public int size() {
		return data.size();
	}

	@Override
	public Key[] keys() {
		return data.keys();
	}

	@Override
	public Object remove(Key key) throws PageException {
		return data.remove(key);
	}

	@Override
	public Object removeEL(Key key) {
		return data.removeEL(key);
	}

	@Override
	public void clear() {
		data.clear();
	}

	@Override
	public Object get(Key key) throws PageException {
		return data.get(key);
	}

	@Override
	public Object get(Key key, Object defaultValue) {
		return data.get(key, defaultValue);
	}

	@Override
	public Object set(Key key, Object value) throws PageException {
		return data.set(key, value);
	}

	@Override
	public Object setEL(Key key, Object value) {
		return data.setEL(key, value);
	}

	@Override
	public Collection duplicate(boolean deepCopy) {
		return data.duplicate(deepCopy);
	}

	@Override
	public boolean containsKey(Key key) {
		return data.containsKey(key);
	}

	@Override
	public Iterator<Key> keyIterator() {
		return data.keyIterator();
	}

	@Override
	public Iterator<Object> valueIterator() {
		return data.valueIterator();
	}

	@Override
	public Iterator<Entry<Key, Object>> entryIterator() {
		return data.entryIterator();
	}

	@Override
	public boolean isInitalized() {
		return true;
	}

	@Override
	public void initialize(PageContext pc) {
		
	}

	@Override
	public void release() {
		
	}

	@Override
	public void release(PageContext pc) {
		
	}

	@Override
	public int getType() {
		// TODO Auto-generated method stub
		return SCOPE_VARIABLES;
	}

	@Override
	public String getTypeAsString() {
		return "static";
	}


}
