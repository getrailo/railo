package railo.runtime.functions.conversion;

import railo.runtime.PageContext;
import railo.runtime.converter.ConverterException;
import railo.runtime.converter.JSONConverter;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;

/**
 * Decodes Binary Data that are encoded as String
 */
public final class SerializeJSON implements Function {

	public static String call(PageContext pc, Object var) throws PageException {
	    return call(pc,var,false);
	}
	public static String call(PageContext pc, Object var,boolean serializeQueryByColumns) throws PageException {
		try {
            return new JSONConverter(true).serialize(pc,var,serializeQueryByColumns);
        } catch (ConverterException e) {
            throw Caster.toPageException(e);
        }
		//throw new FunctionNotSupported("SerializeJSON");
	}
}