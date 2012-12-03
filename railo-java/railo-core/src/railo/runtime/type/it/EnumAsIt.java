package railo.runtime.type.it;

import java.util.Enumeration;
import java.util.Iterator;

public class EnumAsIt implements Iterator {
	private Enumeration e;

	public EnumAsIt(Enumeration e){
		this.e=e;
	}

	@Override
	public boolean hasNext() {
		return e.hasMoreElements();
	}

	@Override
	public Object next() {
		return e.nextElement();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("this operation is not suppored");
	}
}
