package railo.transformer.cfml.evaluator.func.impl;

import railo.runtime.exp.TemplateException;
import railo.transformer.bytecode.expression.ExprString;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.expression.var.Argument;
import railo.transformer.bytecode.expression.var.BIF;
import railo.transformer.bytecode.expression.var.Variable;
import railo.transformer.bytecode.expression.var.VariableString;
import railo.transformer.cfml.evaluator.FunctionEvaluator;
import railo.transformer.library.function.FunctionLibFunction;

public class IsNull implements FunctionEvaluator{

	public void evaluate(BIF bif, FunctionLibFunction flf) throws TemplateException {
		Argument arg = bif.getArguments()[0];
		Expression value = arg.getValue();
		if(value instanceof Variable){
			try{
				ExprString exprStr=VariableString.translateVariableToExprString(value);
				arg.setStringType(String.class.getName());
				arg.setValue(exprStr);
			}
			catch(Throwable t){
				
			}
		}
	}

}