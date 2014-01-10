package railo.runtime.sql.exp.value;

import railo.runtime.op.Caster;
import railo.runtime.sql.exp.Literal;


public class ValueNumber extends ValueSupport implements Literal {

	private double value;

	public ValueNumber(double value) {
		super(Caster.toString(value));
		this.value=value;
	}
	public ValueNumber(double value, String strValue) {
		super(strValue);
		this.value=value;
	}
	public ValueNumber(String strValue) {
		super(strValue);
		this.value=Caster.toDoubleValue(strValue,0);
	}

	@Override
	public String toString(boolean noAlias) {
		if(noAlias || getIndex()==0)return getString();
		return getString()+" as "+getAlias();
	}
	
	public Object getValue() {
		return Caster.toDouble(value);
	}
	
	public double getValueAsDouble() {
		return value;
	}
}
