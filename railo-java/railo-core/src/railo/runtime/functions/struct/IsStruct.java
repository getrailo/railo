/**
 * Implements the CFML Function isstruct
 */
package railo.runtime.functions.struct;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Decision;

public final class IsStruct extends BIF {
	
	private static final long serialVersionUID = 4269284162523082182L;

	public static boolean call(PageContext pc , Object object) {
		return Decision.isStruct(object);
	}
	
	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		return call(pc,args[0]);
	}
}