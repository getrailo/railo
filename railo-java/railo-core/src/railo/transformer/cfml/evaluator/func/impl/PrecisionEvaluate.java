package railo.transformer.cfml.evaluator.func.impl;

import railo.runtime.exp.TemplateException;
import railo.transformer.bytecode.Position;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.expression.var.Argument;
import railo.transformer.bytecode.expression.var.BIF;
import railo.transformer.bytecode.literal.LitString;
import railo.transformer.bytecode.op.OpDouble;
import railo.transformer.cfml.evaluator.FunctionEvaluator;
import railo.transformer.library.function.FunctionLibFunction;


public class PrecisionEvaluate implements FunctionEvaluator {

	@Override
	public void evaluate(BIF bif, FunctionLibFunction flf) throws TemplateException {

		Argument[] args = bif.getArguments();

		for (Argument arg : args) {

			Expression value = arg.getValue();

			if (value instanceof OpDouble) {

				/* this works:
				String lit = "p1 + p2";
				Position p1 = new Position(0,0,0), p2 = new Position(0,lit.length()-1,lit.length()-1);
				arg.setValue(LitString.toExprString( lit, p1, p2 ), "string"); //*/

				// TODO: HOW DO WE GET THE ORIGINAL CFML STRING HERE?
//				arg.setValue(LitString.toExprString( arg.getRawValue(), value.getStart(), value.getEnd() ), "string");
			}
		}
	}
}
