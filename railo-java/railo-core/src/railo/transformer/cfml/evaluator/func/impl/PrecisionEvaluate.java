package railo.transformer.cfml.evaluator.func.impl;

import railo.runtime.exp.TemplateException;
import railo.transformer.bytecode.expression.var.Argument;
import railo.transformer.bytecode.expression.var.BIF;
import railo.transformer.bytecode.op.OpBigDecimal;
import railo.transformer.bytecode.op.OpDouble;
import railo.transformer.cfml.evaluator.FunctionEvaluator;
import railo.transformer.expression.Expression;
import railo.transformer.library.function.FunctionLibFunction;


public class PrecisionEvaluate implements FunctionEvaluator {

	@Override
	public void evaluate(BIF bif, FunctionLibFunction flf) throws TemplateException {

		Argument[] args = bif.getArguments();

		for (Argument arg : args) {
			Expression value = arg.getValue();
			if (value instanceof OpDouble) {
				arg.setValue(value.getFactory().toExprString(toOpBigDecimal(((OpDouble)value))), "any");
			}
		}
	}

	private OpBigDecimal toOpBigDecimal(OpDouble op) {
		Expression left = op.getLeft();
		Expression right = op.getRight();
		if(left instanceof OpDouble) left=toOpBigDecimal((OpDouble) left);
		if(right instanceof OpDouble) right=toOpBigDecimal((OpDouble) right);
		return new OpBigDecimal(left, right, op.getOperation());
	}
}
