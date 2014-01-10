package railo.runtime.type.util;

import java.util.Map;

import railo.runtime.type.Collection;
import railo.runtime.type.Struct;

public class StructMapEntry implements Map.Entry<String,Object> {
	
	private Collection.Key key;
	private Object value;
	private Struct sct;

	public StructMapEntry(Struct sct,Collection.Key key,Object value) {
		this.sct=sct;
		this.key=key;
		this.value=value;
	}
	
	@Override
	public String getKey() {
		return key.getString();
	}

	public Object getValue() {
		return value;
	}

	public Object setValue(Object value) {
		Object old = value;
		sct.setEL(key, value);
		this.value=value;
		return old;
	}
	
}
