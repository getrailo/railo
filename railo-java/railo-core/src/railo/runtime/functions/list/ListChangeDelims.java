/**
 * Implements the Cold Fusion Function listchangedelims
 */
package railo.runtime.functions.list;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.List;

public final class ListChangeDelims implements Function {
	public static String call(PageContext pc , String list, String newDel) throws PageException {
		return call(pc , list, newDel, ",");
	}
	public static String call(PageContext pc , String list, String newDel, String oldDel) throws PageException {
		return List.arrayToList(List.listToArrayRemoveEmpty(list,oldDel),newDel);
	}
}