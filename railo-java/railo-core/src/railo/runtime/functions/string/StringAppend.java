package railo.runtime.functions.string;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.op.Operator;

public class StringAppend {
	public static Object call(PageContext pc, Object left, Object right) throws PageException {
		return Operator.concat(Caster.toCharSequence(left),Caster.toCharSequence(right));
	}
}
