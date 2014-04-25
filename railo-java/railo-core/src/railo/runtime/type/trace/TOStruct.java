package railo.runtime.type.trace;

import java.util.Map;
import java.util.Set;

import railo.runtime.debug.Debugger;
import railo.runtime.op.Duplicator;
import railo.runtime.type.Collection;
import railo.runtime.type.Struct;

public class TOStruct extends TOCollection implements Struct {

	private static final long serialVersionUID = 4868199372417392722L;
	private Struct sct;

	protected TOStruct(Debugger debugger,Struct sct,int type,String category,String text) {
		super(debugger,sct, type, category, text);
		this.sct=sct;
	}
	
	@Override
	public boolean isEmpty() {
		log(null);
		return sct.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		log(null);
		return sct.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		log(null);
		return sct.containsValue(value);
	}

	@Override
	public Object get(Object key) {
		log(null);
		return sct.get(key);
	}

	@Override
	public Object put(Object key, Object value) {
		log(null);
		return sct.put(key, value);
	}

	@Override
	public Object remove(Object key) {
		log(null);
		return sct.remove(key);
	}

	@Override
	public void putAll(Map m) {
		log(null);
		sct.putAll(m);
	}

	@Override
	public Set keySet() {
		log(null);
		return sct.keySet();
	}

	@Override
	public java.util.Collection values() {
		log(null);
		return sct.values();
	}

	@Override
	public Set entrySet() {
		log(null);
		return sct.entrySet();
	}

	@Override
	public Collection duplicate(boolean deepCopy) {
		log(null);
		return new TOStruct(debugger,(Struct)Duplicator.duplicate(sct,deepCopy), type, category, text);
	}

	@Override
	public java.util.Iterator<String> getIterator() {
    	return keysAsStringIterator();
    } 

}
