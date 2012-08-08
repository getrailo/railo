package railo.transformer.bytecode.op;

import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.expression.var.Assign;
import railo.transformer.bytecode.expression.var.Variable;
import railo.transformer.bytecode.literal.LitDouble;

public final class OpVariable extends Assign {

	public OpVariable(Variable variable, Expression value) {
		super(variable, value);
	}

	public OpVariable(Variable variable, double value) {
		super(variable, LitDouble.toExprDouble(value, variable.getStart(),variable.getEnd()));
	}
}
