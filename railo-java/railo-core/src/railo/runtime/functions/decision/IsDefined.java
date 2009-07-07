/**
 * Implements the Cold Fusion Function isdefined
 */
package railo.runtime.functions.decision;


import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.interpreter.VariableInterpreter;

public final class IsDefined implements Function {
	public static boolean call(PageContext pc , String varName) {
		
        return VariableInterpreter.isDefined(pc,varName);
		//return pc.isDefined(varName);
	}
}