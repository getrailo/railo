/**
 * Implements the Cold Fusion Function tobinary
 */
package railo.runtime.functions.other;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;

public final class ToBinary implements Function {
	public static byte[] call(PageContext pc , Object object) throws ExpressionException {
		return Caster.toBinary(object);
	}
}