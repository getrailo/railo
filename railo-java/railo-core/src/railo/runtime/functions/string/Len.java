/**
 * Implements the CFML Function len
 */
package railo.runtime.functions.string;

import java.util.List;
import java.util.Map;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;
import railo.runtime.type.Collection;
import railo.runtime.type.Query;

public final class Len implements Function {
	public static double call(PageContext pc , String string) {
		return string.length();
	}
	public static double call(PageContext pc , Object obj) throws FunctionException {
		double len=invoke(obj, -1);
		if(len==-1)throw new FunctionException(pc,"len",1,"object","this type  ["+Caster.toTypeName(obj)+"] is not supported for returning the len");
		return len;
	}
	
	public static double invoke(Object obj, double defaultValue) {
		if(obj instanceof String)return ((String)obj).length();
		if(obj instanceof Query)return ((Query)obj).getRecordcount();
		if(obj instanceof Collection)return ((Collection)obj).size();
		if(obj instanceof Map)return ((Map)obj).size();
		if(obj instanceof List)return ((List)obj).size();
		if(obj instanceof Object[])return ((Object[])obj).length;
		if(obj instanceof short[])return ((short[])obj).length;
		if(obj instanceof int[])return ((int[])obj).length;
		if(obj instanceof float[])return ((float[])obj).length;
		if(obj instanceof double[])return ((double[])obj).length;
		if(obj instanceof long[])return ((long[])obj).length;
		if(obj instanceof char[])return ((char[])obj).length;
		if(obj instanceof boolean[])return ((boolean[])obj).length;
		if(obj instanceof StringBuffer)return ((StringBuffer)obj).length();
		if(obj instanceof StringBuilder)return ((StringBuilder)obj).length();
		return defaultValue;
	}
}