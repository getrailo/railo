package railo.runtime.functions.dynamicEvaluation;


import railo.runtime.PageContext;
import railo.runtime.converter.ConverterException;
import railo.runtime.converter.ScriptConverter;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;

/**
 * Implements the CFML Function Serialize
 */
public final class Serialize implements Function {
	
	public static String call(PageContext pc , Object o) throws PageException {
	    try {
            return new ScriptConverter().serialize(o);
        } catch (ConverterException e) {
            throw Caster.toPageException(e);
        }
	}
	
}