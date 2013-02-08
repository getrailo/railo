package railo.transformer.cfml.evaluator.func.impl;

import railo.commons.lang.StringList;
import railo.runtime.exp.TemplateException;
import railo.runtime.interpreter.VariableInterpreter;
import railo.runtime.type.Collection;
import railo.runtime.type.scope.Scope;
import railo.runtime.type.util.ArrayUtil;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.expression.type.CollectionKey;
import railo.transformer.bytecode.expression.type.CollectionKeyArray;
import railo.transformer.bytecode.expression.var.Argument;
import railo.transformer.bytecode.expression.var.BIF;
import railo.transformer.bytecode.literal.LitDouble;
import railo.transformer.bytecode.literal.LitString;
import railo.transformer.cfml.evaluator.FunctionEvaluator;
import railo.transformer.library.function.FunctionLibFunction;

public class IsDefined implements FunctionEvaluator{

	public void evaluate(BIF bif, FunctionLibFunction flf) throws TemplateException {
		Argument arg = bif.getArguments()[0];
		Expression value = arg.getValue();
		if(value instanceof LitString) {
			String str=((LitString)value).getString();
			StringList sl = VariableInterpreter.parse(str,false);
			if(sl!=null){
				// scope
				str=sl.next();
				int scope = VariableInterpreter.scopeString2Int(str);
				if(scope==Scope.SCOPE_UNDEFINED)sl.reset();
				
				// keys
				String[] arr=sl.toArray();
				ArrayUtil.trim(arr);
				
				// update first arg
				arg.setValue(LitDouble.toExprDouble(scope),"number");
				
				// add second argument
				
				if(arr.length==1){
					Expression expr = new CollectionKey(arr[0]);//LitString.toExprString(str);
					arg=new Argument(expr,Collection.Key.class.getName());
					bif.addArgument(arg);	
				}
				else {
					CollectionKeyArray expr=new CollectionKeyArray(arr);
					//LiteralStringArray expr = new LiteralStringArray(arr);
					arg=new Argument(expr,Collection.Key[].class.getName());
					bif.addArgument(arg);
				}
				
			}
			
		}
		//print.out("bif:"+arg.getValue().getClass().getName());
	}

}
