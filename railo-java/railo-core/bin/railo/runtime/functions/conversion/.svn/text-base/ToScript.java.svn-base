package railo.runtime.functions.conversion;

import railo.runtime.PageContext;
import railo.runtime.converter.ConverterException;
import railo.runtime.converter.JSConverter;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;

/**
 * 
 */
public final class ToScript implements Function {

	public static String call(PageContext pc, Object var, String jsName) throws PageException {
		return call(pc,var,jsName,true,false);
	}
	public static String call(PageContext pc, Object var, String jsName,boolean outputFormat) throws PageException {
		return call(pc,var,jsName,outputFormat,false);
	}
	public static String call(PageContext pc, Object var, String jsName,boolean outputFormat, boolean asFormat) throws PageException {
		//if(!Decision.isVariableName(jsName))
		//	throw new FunctionException(pc,"toScript",2,"jsName","value does not contain a valid variable String");
		
		JSConverter converter = new JSConverter();
		converter.useShortcuts(asFormat); 
		converter.useWDDX(outputFormat); 
		
		
		try {
			return converter.serialize(var,jsName);
		} 
		catch (ConverterException e) {
			throw Caster.toPageException(e);
		}
	}
}
