package railo.runtime.functions.math;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.FunctionException;
import railo.runtime.ext.function.Function;

/**
 * Implements the CFML Function inputbasen
 */
public final class InputBaseN implements Function {
	public static double call(PageContext pc , String string, double radix) throws ExpressionException {
		if(radix<2 || radix>36)
			throw new FunctionException(pc,"inputBaseN",2,"radix","radix must be between 2 an 36");
		
		string=string.trim().toLowerCase();
		if(string.startsWith("0x")) string=string.substring(2, string.length());
		
		if(string.length()>32)
			throw new FunctionException(pc,"inputBaseN",1,"string","argument is to large can be a maximum of 32 digits (-0x at start)");
		
        //print.ln(string+"-"+radix);
		return (int)Long.parseLong(string, (int)radix);
		
        
        
   
  
	}
    
    
    
    
}