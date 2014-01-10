package railo.transformer.cfml.evaluator.func.impl;

import railo.runtime.exp.TemplateException;
import railo.runtime.type.util.ArrayUtil;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.expression.var.Argument;
import railo.transformer.bytecode.expression.var.BIF;
import railo.transformer.bytecode.literal.LitDouble;
import railo.transformer.bytecode.literal.LitString;
import railo.transformer.cfml.evaluator.FunctionEvaluator;
import railo.transformer.library.function.FunctionLibFunction;

public class GetTickCount implements FunctionEvaluator{

	public void evaluate(BIF bif, FunctionLibFunction flf) throws TemplateException {
		Argument[] args = bif.getArguments();
		if(ArrayUtil.isEmpty(args)) return;
		
		Argument arg = args[0];
		Expression value = arg.getValue();
		if(value instanceof LitString) {
			String unit=((LitString)value).getString();
			if("nano".equalsIgnoreCase(unit))
				arg.setValue(LitDouble.toExprDouble(railo.runtime.functions.other.GetTickCount.UNIT_NANO), "number");
			else if("milli".equalsIgnoreCase(unit))
				arg.setValue(LitDouble.toExprDouble(railo.runtime.functions.other.GetTickCount.UNIT_MILLI), "number");
			else if("micro".equalsIgnoreCase(unit))
				arg.setValue(LitDouble.toExprDouble(railo.runtime.functions.other.GetTickCount.UNIT_MICRO), "number");
			else if("second".equalsIgnoreCase(unit))
				arg.setValue(LitDouble.toExprDouble(railo.runtime.functions.other.GetTickCount.UNIT_SECOND), "number");
		}
	}

}
