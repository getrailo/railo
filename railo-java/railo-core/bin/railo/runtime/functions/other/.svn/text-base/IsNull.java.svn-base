/**
 * Implements the Cold Fusion Function isnotmap
 */
package railo.runtime.functions.other;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;

public final class IsNull implements Function {
	public static boolean call(PageContext pc , Object object) {
		return object==null;
	}
	// caled by modifed call from translation time evaluator
	public static boolean call(PageContext pc , String str) {
		
		try {
			return pc.evaluate(str)==null;
		} 
		catch (PageException e) {
			return true;
		}
	}
}