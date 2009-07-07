package railo.runtime.functions.conversion;

import railo.runtime.PageContext;
import railo.runtime.coder.Coder;
import railo.runtime.coder.CoderException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;

/**
 * Decodes Binary Data that are encoded as String
 */
public final class BinaryDecode implements Function {

	public static byte[] call(PageContext pc, String encoded_binary,String binaryencoding) throws PageException {
		try {
			return Coder.decode(binaryencoding,encoded_binary);
		} catch (CoderException e) {
			throw Caster.toPageException(e);
		}
	}
}