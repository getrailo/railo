/**
 * Implements the CFML Function ltrim
 */
package railo.runtime.functions.string;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;

public final class LTrim extends BIF {

	private static final long serialVersionUID = -2643049222690145727L;

	public static String call(PageContext pc , String str) {
		return StringUtil.ltrim(str,"");
	}

    @Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		if(args.length==1)
			return call(pc, Caster.toString(args[0]));

		throw new FunctionException(pc, "LTrim", 1, 1, args.length);
	}
}