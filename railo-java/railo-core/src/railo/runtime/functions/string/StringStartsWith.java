package railo.runtime.functions.string;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;

/**
 * implements the member method String.startWith(prefix, ignoreCase)
 */
public class StringStartsWith extends BIF {

	public static boolean call(PageContext pc, String input, String subs, boolean ignoreCase) {

		if (ignoreCase)
			return input.regionMatches(true, 0, subs, 0, subs.length());

		return input.startsWith(subs);
	}

	public static boolean call(PageContext pc, String input, String subs) {

		return call(pc, input, subs, false);
	}

	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {

		if (args.length < 2 || args.length > 3)
			throw new FunctionException(pc, "startsWith", 2, 3, args.length);

		return call(pc, Caster.toString(args[0]), Caster.toString(args[1]), args.length == 3 ? Caster.toBoolean(args[2]) : false);
	}
}
