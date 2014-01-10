/**
 * Implements the CFML Function isquery
 */
package railo.runtime.functions.query;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.type.Query;

public final class IsQuery extends BIF {

	private static final long serialVersionUID = -3645047640224276123L;

	public static boolean call(PageContext pc , Object object) {
		return object instanceof Query;
	}
	
	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		return call(pc,args[0]);
	}
}