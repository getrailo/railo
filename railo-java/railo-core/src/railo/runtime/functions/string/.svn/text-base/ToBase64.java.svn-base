package railo.runtime.functions.string;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;

/**
 * Implements the Cold Fusion Function tobase64
 */
public final class ToBase64 implements Function {
	/**
	 * @param pc
	 * @param object
	 * @return base64 value as string
	 * @throws PageException
	 */
	public static String call(PageContext pc , Object object) throws PageException {
		return call(pc,object,pc. getHttpServletResponse().getCharacterEncoding());
	}
	/**
	 * @param pc
	 * @param object
	 * @param encoding
	 * @return base 64 value as string
	 * @throws PageException
	 */
	public static String call(PageContext pc , Object object, String encoding) throws PageException {
		return Caster.toBase64(object);
		//throw new ExpressionException("method tobase64 not implemented yet");
	}
}