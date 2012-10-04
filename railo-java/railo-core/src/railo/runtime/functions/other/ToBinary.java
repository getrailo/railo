/**
 * Implements the CFML Function tobinary
 */
package railo.runtime.functions.other;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;

public final class ToBinary implements Function {

	private static final long serialVersionUID = 4541724601337401920L;

	public static byte[] call(PageContext pc , Object data) throws PageException {
		return Caster.toBinary(data);
	}
}