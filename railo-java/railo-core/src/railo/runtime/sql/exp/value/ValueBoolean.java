package railo.runtime.sql.exp.value;

import railo.runtime.sql.exp.Literal;


public class ValueBoolean extends ValueSupport implements Literal {

	public static final ValueBoolean TRUE = new ValueBoolean(true);
	public static final ValueBoolean FALSE = new ValueBoolean(false);
	
	private boolean value;

	private ValueBoolean(boolean value) {
		super(value?"TRUE":"FALSE");
		this.value=value;
	}

	@Override
	public String toString(boolean noAlias) {
		if(noAlias || getIndex()==0)return getString();
		return getString()+" as "+getAlias();
	}

	public Object getValue() {
		return value?Boolean.TRUE:Boolean.FALSE;
	}
}
