/**
 * Implements the CFML Function listsort
 */
package railo.runtime.functions.list;


import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.util.ListUtil;

public final class ListSort implements Function {
	
	private static final long serialVersionUID = -1153055612742304078L;
	
	public static String call(PageContext pc , String list, String sortType) throws PageException {
		return call(pc,list,sortType,"asc",",",false);
	}
	public static String call(PageContext pc , String list, String sortType, String sortOrder) throws PageException {
		return call(pc,list,sortType,sortOrder,",",false);
	}
	public static String call(PageContext pc , String list, String sortType, String sortOrder, String delimiter) throws PageException {
		return call(pc,list,sortType,sortOrder,delimiter,false);
	}
	public static String call(PageContext pc , String list, String sortType, String sortOrder, String delimiter , boolean includeEmptyFields) throws PageException {
		if(includeEmptyFields) return ListUtil.sort(list,sortType, sortOrder,delimiter);
		return ListUtil.sortIgnoreEmpty(ListUtil.trim(list,delimiter),sortType, sortOrder,delimiter);
	}
}
