package railo.runtime.functions.dynamicEvaluation;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;

/**
 * Implements the Cold Fusion Function evaluate
 */
public final class Evaluate implements Function {

	/*public static Object call(PageContext pc , Object o) throws PageException {
		if(o instanceof Number) return o;
		return pc.evaluate(Caster.toString(o));
	}*/
	
	
	public static Object call(PageContext pc , Object[] objs) throws PageException {
		Object rst=null;
		for(int i=0;i<objs.length;i++) {
			if(objs[i] instanceof Number) rst= objs[i];
			else rst= pc.evaluate(Caster.toString(objs[i]));
		}
		return rst;
		//return CFMLExpressionInterpreter.interpret(pc,Caster.toString(o));
	}
}