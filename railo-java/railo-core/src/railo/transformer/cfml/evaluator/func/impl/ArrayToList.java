package railo.transformer.cfml.evaluator.func.impl;

import railo.runtime.exp.TemplateException;
import railo.transformer.bytecode.cast.Cast;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.expression.var.Argument;
import railo.transformer.bytecode.expression.var.BIF;
import railo.transformer.bytecode.expression.var.Variable;
import railo.transformer.cfml.evaluator.FunctionEvaluator;
import railo.transformer.library.function.FunctionLibFunction;

public class ArrayToList implements FunctionEvaluator{

	public void evaluate(BIF bif, FunctionLibFunction flf) throws TemplateException {
		Argument[] args = bif.getArguments();
		
		Argument arg = args[0];
		Expression value = arg.getValue();
		if(value instanceof Cast) {
			value=((Cast)value).getExpr();
		}
		if(value instanceof Variable) {
			((Variable)value).setAsCollection(Boolean.TRUE);
		}
	}

}
