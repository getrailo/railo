package railo.transformer.bytecode.expression.var;

import railo.transformer.expression.ExprString;
import railo.transformer.expression.var.DataMember;

public final class DataMemberImpl implements DataMember {
	private ExprString name;

	public DataMemberImpl(ExprString name) {
		this.name=name;
	}

	@Override
	public ExprString getName() {
		return name;
	}
}