package railo.runtime.functions.other;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;

public class ToNumeric {
	public static double call(PageContext pc , Object value) throws PageException {
		return Caster.toDoubleValue(value);
	}
	public static double call(PageContext pc , Object value, Object oRadix) throws PageException {
		int radix;
		if(Decision.isNumeric(oRadix)){
			radix=Caster.toIntValue(oRadix);
		}
		else {
			String str = Caster.toString(oRadix).trim().toLowerCase();
			if("bin".equals(str)) radix=2;
			else if("oct".equals(str)) radix=8;
			else if("dec".equals(str)) radix=10;
			else if("hex".equals(str)) radix=16;
			else throw new FunctionException(pc, "ToNumeric", 2, "radix", "invalid value ["+str+"], valid values are [<number>,bin,oct,dec.hex]");
		}
		
		return (double)Integer.parseInt(Caster.toString(value), radix);
	}
}
