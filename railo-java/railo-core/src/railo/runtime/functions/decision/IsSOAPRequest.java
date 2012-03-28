package railo.runtime.functions.decision;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.net.rpc.AxisUtil;

/**
 * 
 */
public final class IsSOAPRequest implements Function {
	
	private static final long serialVersionUID = 5616044662863702066L;

	public static boolean call(PageContext pc) {
		return AxisUtil.isSOAPRequest();
	}
}