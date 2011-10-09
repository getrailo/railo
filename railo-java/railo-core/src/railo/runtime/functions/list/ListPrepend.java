/**
 * Implements the Cold Fusion Function listprepend
 */
package railo.runtime.functions.list;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

public final class ListPrepend implements Function {

	private static final long serialVersionUID = -4252541560957800011L;
	
	public static String call(PageContext pc , String list, String value) {
		if(list.length()==0) return value;
		return new StringBuffer(value).append(',').append(list).toString();
	}
	public static String call(PageContext pc , String list, String value, String delimeter) {
		if(list.length()==0) return value;
		if(StringUtil.isEmpty(delimeter)) {
		    return call(pc,list,value);
        }
        return new StringBuffer(value).append(delimeter.charAt(0)).append(list).toString();
	}
}