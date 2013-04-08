package railo.runtime.functions.arrays;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.type.Array;

public class JsonArray extends BIF {

	private static final long serialVersionUID = -6612774374307676590L;

	/**
	 * @param pc
	 * @param objArr
	 * @return
	 * @throws ExpressionException
	 */
	public static Array call(PageContext pc , Object[] objArr) {
		return Array_.call(pc, objArr);
	}
	
	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		return call(pc,(Object[])args[0]);
	}
}
