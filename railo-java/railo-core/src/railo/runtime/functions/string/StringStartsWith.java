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

	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {

		if (args.length < 2 || args.length > 3)
			throw new FunctionException(pc, "startsWith", 2, 3, args.length);

		String input = Caster.toString(args[0]);
		String subs  = Caster.toString(args[1]);

		if (args.length == 2 || !Caster.toBoolean(args[2]))
			return input.startsWith(subs);

		return input.regionMatches(true, 0, subs, 0, subs.length());
	}
}
