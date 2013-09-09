package railo.runtime.functions.string;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;

public class ParseNumber {
	private static final int BIN=2;
	private static final int OCT=8;
	private static final int DEC=10;
	private static final int HEX=16;
	
	public static double call(PageContext pc , String strNumber) throws PageException {
		return call(pc,strNumber,null);
	}

	public static double call(PageContext pc , String strNumber,String strRadix) throws PageException {
		return invoke(strNumber, strRadix);
	}

	public static double invoke(String strNumber,String strRadix, double defaultValue) {
		try {
			return invoke(strNumber, strRadix);
		} catch (PageException e) {
			return defaultValue;
		}
	}
	public static double invoke(String strNumber,String strRadix) throws PageException {
		strNumber=strNumber.trim();
		int radix=DEC;
		if(strRadix==null) {
			if(StringUtil.startsWithIgnoreCase(strNumber, "0x")){
				radix=HEX;
				strNumber=strNumber.substring(2);
			}
			else if(strNumber.startsWith("#")){
				radix=HEX;
				strNumber=strNumber.substring(1);
			}
			else if(strNumber.startsWith("0") && strNumber.length()>1){
				radix=OCT;
				strNumber=strNumber.substring(1);
			}
		}
		else {
			strRadix=strRadix.trim().toLowerCase();

			if(strRadix.startsWith("bin"))radix=BIN;
			else if(strRadix.startsWith("oct"))radix=OCT;
			else if(strRadix.startsWith("dec"))radix=DEC;
			else if(strRadix.startsWith("hex")){
				if(StringUtil.startsWithIgnoreCase(strNumber, "0x")) strNumber=strNumber.substring(2);
				else if(strNumber.startsWith("#")) strNumber=strNumber.substring(1);
				
				radix=HEX;
			}
			else throw new ExpressionException("invalid radix defintions, valid vales are [bin,oct,dec,hex]");
			
		}
		
		if(radix==OCT && strNumber.indexOf('9')!=-1)
			throw new ExpressionException("digit [9] is out of range for a octal number");
		
		
		if(strNumber.indexOf('.')!=-1 && radix!=DEC)
			throw new ExpressionException("the radix con only be [dec] for floating point numbers");
			
		
		if(radix==DEC) {
			return Caster.toDoubleValue(strNumber);
		}
		return Integer.parseInt(strNumber,radix);	
	}
	
}
