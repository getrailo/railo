package railo.transformer.bytecode.expression.var;

import railo.transformer.bytecode.cast.CastString;
import railo.transformer.bytecode.expression.ExprString;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.literal.LitString;

public final class UDF extends FunctionMember {
	
	private ExprString name;

	public UDF(Expression name) {
		this.name=CastString.toExprString(name);
	}
	public UDF(String name) {
		this.name=LitString.toExprString(name);
	}
	
	/**
	 * @return the name
	 */
	public ExprString getName() {
		return name;
	}
}
