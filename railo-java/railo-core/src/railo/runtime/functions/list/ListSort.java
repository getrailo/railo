/**
 * Implements the Cold Fusion Function listsort
 */
package railo.runtime.functions.list;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.List;

public final class ListSort implements Function {
	// ListSort(list, sort_type [, sortOrder] [, delimiters ]) 
	public static String call(PageContext pc , String list, String sortType) throws PageException {
		return call(pc,list,sortType,"asc",",");
	}
	public static String call(PageContext pc , String list, String sortType, String sortOrder) throws PageException {
		return call(pc,list,sortType,sortOrder,",");
	}
	public static String call(PageContext pc , String list, String sortType, String 	sortOrder, String delimiter) throws PageException {
		return List.sortIgnoreEmpty(List.trim(list,delimiter),sortType, sortOrder,delimiter);
	}
}
