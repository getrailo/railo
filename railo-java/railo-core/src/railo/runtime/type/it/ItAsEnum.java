package railo.runtime.type.it;

import java.util.Enumeration;
import java.util.Iterator;

public class ItAsEnum implements Enumeration {
	
	private Iterator it;

	private ItAsEnum(Iterator it){
		this.it=it;
	}

	@Override
	public boolean hasMoreElements() {
		return it.hasNext();
	}

	@Override
	public Object nextElement() {
		return it.next();
	}
	
	public static Enumeration toEnumeration(Iterator it){
		if(it instanceof Enumeration) return (Enumeration) it;
		return new ItAsEnum(it);
	}

}
