/**
 * Implements the CFML Function replacelist
 */
package railo.runtime.functions.string;

import java.util.Iterator;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;
import railo.runtime.type.util.ListUtil;

public final class ReplaceList implements Function {
	
	private static final long serialVersionUID = -3935300433837460732L;

	public static String call(PageContext pc , String str, String list1, String list2) {
		return call(pc, str, list1, list2, ",", ",");
	}
	
	public static String call(PageContext pc , String str, String list1, String list2, String delimiter_list1) {
		if(delimiter_list1==null) delimiter_list1=",";
		
		return call(pc, str, list1, list2, delimiter_list1, delimiter_list1);
	}
	
	public static String call(PageContext pc , String str, String list1, String list2, String delimiter_list1, String delimiter_list2) {
		if(delimiter_list1==null) delimiter_list1=",";
		if(delimiter_list2==null) delimiter_list2=",";

		Array arr1=ListUtil.listToArray(list1, delimiter_list1);
		Array arr2=ListUtil.listToArray(list2, delimiter_list2);

		Iterator<Object> it1 = arr1.valueIterator();
		Iterator<Object> it2 = arr2.valueIterator();
		
        while(it1.hasNext()) {
            str=StringUtil.replace(str,Caster.toString(it1.next(),null),((it2.hasNext())?Caster.toString(it2.next(),null):""),false);
        }
		return str;
	}
}