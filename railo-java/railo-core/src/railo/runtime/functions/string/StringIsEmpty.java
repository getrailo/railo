package railo.runtime.functions.string;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;

/**
 * implements the String member method isEmpty()
 */
public class StringIsEmpty extends BIF {


	public static boolean call(PageContext pc , String value) throws PageException {

		return StringUtil.isEmpty(value);
	}

	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {

		if (args.length != 1)
			throw new FunctionException(pc, "IsEmpty", 1, 1, args.length);

		return call(pc, Caster.toString(args[0]));
	}
}
