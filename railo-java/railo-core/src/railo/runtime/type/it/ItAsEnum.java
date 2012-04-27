package railo.runtime.type.it;

import java.util.Enumeration;
import java.util.Iterator;

import railo.runtime.type.Collection;

public class ItAsEnum implements Enumeration<String> {
	
	private Iterator<Collection.Key> it;

	private ItAsEnum(Iterator<Collection.Key> it){
		this.it=it;
	}

	@Override
	public boolean hasMoreElements() {
		return it.hasNext();
	}

	@Override
	public String nextElement() {
		return it.next().getString();
	}
	
	public static Enumeration<String> toStringEnumeration(Iterator<Collection.Key> it){
		return new ItAsEnum(it);
	}

}
