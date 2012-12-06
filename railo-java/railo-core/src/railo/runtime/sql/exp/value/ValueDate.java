package railo.runtime.sql.exp.value;

import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.sql.exp.Literal;
import railo.runtime.type.dt.DateTime;


public class ValueDate extends ValueSupport implements Literal {

	private DateTime value;

	public ValueDate(DateTime value) {
		super(value.toString());
		this.value=value;
	}
	
	public ValueDate(String strValue) throws PageException {
		super(strValue);
		this.value=Caster.toDate(strValue,false,null);
	}

	@Override
	public String toString(boolean noAlias) {
		if(noAlias || getIndex()==0)return getString();
		return getString()+" as "+getAlias();
	}
	
	public Object getValue() {
		return value;
	}
	
	public DateTime getValueAsDateTime() {
		return value;
	}
}
