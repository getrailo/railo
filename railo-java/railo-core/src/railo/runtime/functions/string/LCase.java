/**
 * Implements the CFML Function lcase
 */
package railo.runtime.functions.string;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;

public final class LCase extends BIF {

	private static final long serialVersionUID = -2423113719056207022L;

	public static String call(PageContext pc , String string) {
        return StringUtil.toLowerCase(string);
    }

    @Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		if(args.length==1)
			return call(pc, Caster.toString(args[0]));

		throw new FunctionException(pc, "LCase", 1, 1, args.length);
	}
}