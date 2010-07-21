package railo.transformer.cfml.evaluator;

import railo.runtime.exp.TemplateException;
import railo.transformer.bytecode.expression.var.BIF;
import railo.transformer.library.function.FunctionLibFunction;

public interface FunctionEvaluator {
	
	public void evaluate(BIF bif, FunctionLibFunction flf) throws TemplateException;
	
	
}