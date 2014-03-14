package railo.transformer.bytecode.op;

import railo.transformer.Position;
import railo.transformer.bytecode.expression.var.Assign;
import railo.transformer.expression.Expression;
import railo.transformer.expression.var.Variable;

public final class OpVariable extends Assign {

	public OpVariable(Variable variable, Expression value, Position end) {
		super(variable, value,end);
	}

	public OpVariable(Variable variable, double value, Position end) {
		super(variable, variable.getFactory().createLitDouble(value, variable.getEnd(),end),end);
	}
}
