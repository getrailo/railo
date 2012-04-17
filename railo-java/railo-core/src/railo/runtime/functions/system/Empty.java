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
		
		/*if(res instanceof Number) return ((Number)res).doubleValue()==0.0d;
		if(res instanceof String) {
			String str=(String) res;
			if(str.length()==0)return true;
			if(Decision.isBoolean(str)) return false;
			return Caster.toDoubleValue(str,1)==0.0d;
		}
		
		if(res instanceof Boolean) return false;
		if(res instanceof Query)return ((Query)res).getRecordcount()==0;
		if(res instanceof Map)return ((Map)res).size()==0;
		if(res instanceof List)return ((List)res).size()==0;
		if(res instanceof Collection)return ((Collection)res).size()==0;
		
		if(res instanceof Object[])return ((Object[])res).length==0;
		if(res instanceof short[])return ((short[])res).length==0;
		if(res instanceof int[])return ((int[])res).length==0;
		if(res instanceof float[])return ((float[])res).length==0;
		if(res instanceof double[])return ((double[])res).length==0;
		if(res instanceof long[])return ((long[])res).length==0;
		if(res instanceof char[])return ((char[])res).length==0;
		if(res instanceof boolean[])return ((boolean[])res).length==0;
		if(res instanceof StringBuffer)return call(pc,res.toString());
		if(res instanceof StringBuilder)return call(pc,res.toString());
		
		if(res instanceof Castable) return call(pc,((Castable)res).castToString("1"));
		
		return false;*/
	}
}
