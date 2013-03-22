package railo.runtime.functions.conversion;

import java.io.UnsupportedEncodingException;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;

/**
 * Decodes Binary Data that are encoded as String
 */
public final class CharsetEncode implements Function {
	
	public static String call(PageContext pc, byte[] binary,String encoding) throws PageException {
	    try {
            return new String(binary, encoding);
        } catch (UnsupportedEncodingException e) {
            throw Caster.toPageException(e);
        }
	}
}