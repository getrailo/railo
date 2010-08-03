package railo.runtime.functions.decision;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Decision;

/**
 * Implements the Cold Fusion Function isleapyear
 */
public final class IsLeapYear implements Function {
	public static boolean call(PageContext pc , double year) {
		return Decision.isLeapYear((int)year);
	}

}