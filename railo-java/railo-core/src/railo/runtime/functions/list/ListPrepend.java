/**
 * Implements the Cold Fusion Function listprepend
 */
package railo.runtime.functions.list;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

public final class ListPrepend implements Function {

	public static String call(PageContext pc , String list, String value) {
		if(list.length()==0) return value;
		return new StringBuffer(value).append(',').append(list).toString();
	}
	public static String call(PageContext pc , String list, String value, String delimeter) {
		if(list.length()==0) return value;
		if(delimeter.length()==0) {
		    return call(pc,list,value);
        }
        return new StringBuffer(value).append(delimeter.charAt(0)).append(list).toString();
	}
}