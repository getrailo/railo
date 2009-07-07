/**
 * Implements the Cold Fusion Function lsiscurrency
 */
package railo.runtime.functions.international;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;

public final class LSIsNumeric implements Function {
	public static boolean call(PageContext pc , String string) {
		try {
			LSParseNumber.call(pc,string);
			return true;
		} catch (PageException e) {
			return false;
		}
	}
}