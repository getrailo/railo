/**
 * Implements the CFML Function listchangedelims
 */
package railo.runtime.functions.list;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.util.ListUtil;

public final class ListChangeDelims implements Function {
	
	private static final long serialVersionUID = 8979553735693035787L;
	
	public static String call(PageContext pc , String list, String newDel) throws PageException {
		return call(pc , list, newDel, ",",false);
	}
	public static String call(PageContext pc , String list, String newDel, String oldDel) throws PageException {
		return call(pc, list, newDel, oldDel, false);
	}
	public static String call(PageContext pc , String list, String newDel, String oldDel, boolean includeEmptyFields) throws PageException {
		if(includeEmptyFields)return ListUtil.arrayToList(ListUtil.listToArray(list,oldDel),newDel);
		return ListUtil.arrayToList(ListUtil.listToArrayRemoveEmpty(list,oldDel),newDel);
	}
}