package railo.transformer.bytecode.op;

import railo.transformer.bytecode.Position;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.expression.var.Assign;
import railo.transformer.bytecode.expression.var.Variable;
import railo.transformer.bytecode.literal.LitDouble;

public final class OpVariable extends Assign {

	public OpVariable(Variable variable, Expression value, Position end) {
		super(variable, value,end);
	}

	public OpVariable(Variable variable, double value, Position end) {
		super(variable, LitDouble.toExprDouble(value, variable.getEnd(),end),end);
	}
}
