package railo.runtime.sql.exp.value;

import railo.commons.lang.StringUtil;
import railo.runtime.sql.exp.Literal;

public class ValueString extends ValueSupport implements Literal {

	public ValueString(String value,String alias) {
		super(value,alias);
	}

	public ValueString(String value) {
		super(value);
	}
	
	@Override
	public String toString(boolean noAlias) {
		if(noAlias || getIndex()==0)return "'"+StringUtil.replace(getString(), "'", "''", false)+"'";
		return toString(true)+" as "+getAlias();
	}

	@Override
	public Object getValue() {
		return getString();
	}
}
