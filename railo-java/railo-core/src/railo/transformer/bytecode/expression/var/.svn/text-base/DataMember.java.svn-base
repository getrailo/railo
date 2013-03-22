package railo.transformer.bytecode.expression.var;

import railo.transformer.bytecode.cast.CastString;
import railo.transformer.bytecode.expression.ExprString;
import railo.transformer.bytecode.expression.Expression;

public final class DataMember implements Member {
	private ExprString name;

	public DataMember(Expression name) {
		this.name=CastString.toExprString(name);
	}

	/**
	 * @return the name
	 */
	public ExprString getName() {
		return name;
	}
}