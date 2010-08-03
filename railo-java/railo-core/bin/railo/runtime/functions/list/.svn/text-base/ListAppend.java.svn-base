/**
 * Implements the Cold Fusion Function listappend
 */
package railo.runtime.functions.list;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

public final class ListAppend implements Function {
	public static String call(PageContext pc , String list, String value) {
		if(list.length()==0) return value;
		return new StringBuffer(list).append(',').append(value).toString();
	}
	public static String call(PageContext pc , String list, String value, String delimeter) {
		if(list.length()==0) return value;
        switch(delimeter.length()) {
        case 0:return list;
        case 1:return new StringBuffer(list).append(delimeter).append(value).toString();
        }
        return new StringBuffer(list).append(delimeter.charAt(0)).append(value).toString();
	}
	
}