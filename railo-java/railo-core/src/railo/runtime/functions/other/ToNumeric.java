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
		if(oRadix==null) return call(pc, value);
		int radix;
		if(Decision.isNumeric(oRadix)){
			radix=Caster.toIntValue(oRadix);
			if(radix<Character.MIN_RADIX || radix>Character.MAX_RADIX)
				throw invalidRadix(pc, Caster.toString(radix));
		}
		else {
			String str = Caster.toString(oRadix).trim().toLowerCase();
			if("bin".equals(str)) radix=2;
			else if("oct".equals(str)) radix=8;
			else if("dec".equals(str)) radix=10;
			else if("hex".equals(str)) radix=16;
			else throw invalidRadix(pc,str);
		}
		
		return Integer.parseInt(Caster.toString(value), radix);
	}
	
	private static FunctionException invalidRadix(PageContext pc , String radix) {
		return new FunctionException(pc, "ToNumeric", 2, "radix", "invalid value ["+radix+"], valid values are ["+Character.MIN_RADIX+"-"+Character.MAX_RADIX+",bin,oct,dec,hex]");
	}
}
