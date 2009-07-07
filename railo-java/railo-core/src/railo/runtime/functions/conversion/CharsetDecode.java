package railo.runtime.functions.conversion;

import java.io.UnsupportedEncodingException;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;

/**
 * Decodes Binary Data that are encoded as String
 */
public final class CharsetDecode implements Function {

	public static byte[] call(PageContext pc, String encoded_binary,String encoding) throws PageException {
	    try {
            return encoded_binary.getBytes(encoding);
        } catch (UnsupportedEncodingException e) {
            throw Caster.toPageException(e);
        }
	}
}