package railo.runtime.functions.system;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.ext.function.Function;
import railo.runtime.functions.string.Len;
import railo.runtime.interpreter.VariableInterpreter;
import railo.runtime.op.Caster;

public class Empty implements Function {

	private static final long serialVersionUID = 3780957672985941192L;
	

	public static boolean call(PageContext pc , String variableName) throws FunctionException {
		Object res = VariableInterpreter.getVariableEL(pc, variableName,null);
		
		if(res==null) return true;
		double len=Len.invoke(res, -1);
		if(len==-1)throw new FunctionException(pc,"empty",1,"variable","this type  ["+Caster.toTypeName(res)+"] is not supported");
		return len==0;
	}
}
