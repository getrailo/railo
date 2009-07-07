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
public final class BinaryEncode implements Function {
	
	/**
	 * @param pc
	 * @param binary
	 * @param binaryencoding
	 * @return encoded string
	 * @throws PageException
	 */
	public static String call(PageContext pc, byte[] binary, String binaryencoding) throws PageException {
		try {
			return Coder.encode(binaryencoding,binary);
		} catch (CoderException e) {
			throw Caster.toPageException(e);
		}
	}
}