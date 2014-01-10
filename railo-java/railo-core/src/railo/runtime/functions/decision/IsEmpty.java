package railo.runtime.functions.decision;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.functions.string.Len;
import railo.runtime.op.Caster;

public class IsEmpty implements Function {

	private static final long serialVersionUID = -2839407878650099024L;

	public static boolean call(PageContext pc , Object value) throws PageException {
		if(value==null) return true;
		if(value instanceof Boolean) return !Caster.toBoolean(value);
		if(value instanceof Number) return Caster.toDoubleValue(value)==0D;
		
		
		double len=Len.invoke(value, -1);
		if(len==-1)throw new FunctionException(pc,"isEmpty",1,"variable","this type  ["+Caster.toTypeName(value)+"] is not supported");
		return len==0;
	}

}
