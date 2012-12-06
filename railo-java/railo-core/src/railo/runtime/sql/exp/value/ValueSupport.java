package railo.runtime.sql.exp.value;

import railo.runtime.sql.exp.ExpressionSupport;

public abstract class ValueSupport extends ExpressionSupport implements Value {

	private String value;


	public ValueSupport(String value,String alias) {
		this.value = value;
		setAlias(alias);
	}
	public ValueSupport( String value) {
		this.value = value;
	}
	
	
	@Override
	public String getString() {
		return value;
	}
}
