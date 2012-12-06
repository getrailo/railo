package railo.runtime.sql.exp.value;

import railo.runtime.sql.exp.Literal;


public class ValueNull extends ValueSupport implements Literal {

	public static final ValueNull NULL = new ValueNull();
	
	//private boolean value;

	private ValueNull() {
		super("NULL");
	}

	@Override
	public String toString(boolean noAlias) {
		if(noAlias || getIndex()==0)return getString();
		return getString()+" as "+getAlias();
	}

	public Object getValue() {
		return null;
	}
}
