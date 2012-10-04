package railo.transformer.cfml.evaluator.func.impl;

import railo.runtime.exp.TemplateException;
import railo.runtime.type.Collection;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.expression.type.CollectionKey;
import railo.transformer.bytecode.expression.var.Argument;
import railo.transformer.bytecode.expression.var.BIF;
import railo.transformer.bytecode.literal.LitString;
import railo.transformer.cfml.evaluator.FunctionEvaluator;
import railo.transformer.library.function.FunctionLibFunction;

public class StructKeyExists implements FunctionEvaluator{

	public void evaluate(BIF bif, FunctionLibFunction flf) throws TemplateException {
		Argument arg = bif.getArguments()[1];
		Expression value = arg.getValue();
		if(value instanceof LitString) {
			String str=((LitString)value).getString();
			
			// update first arg
			arg.setValue(new CollectionKey(str),Collection.Key.class.getName());
		}
		//print.out("bif:"+arg.getValue().getClass().getName());
	}

}
