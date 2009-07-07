/**
 * Implements the Cold Fusion Function replacelist
 */
package railo.runtime.functions.string;

import railo.commons.lang.StringList;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.type.List;

public final class ReplaceList implements Function {
	
	public static String call(PageContext pc , String str, String list1, String list2) {

        StringList l1 = List.toListTrim(list1,',');
        StringList l2 = List.toListTrim(list2,',');
        
        
        while(l1.hasNext()) {
            str=StringUtil.replace(str,l1.next(),((l2.hasNext())?l2.next():""),false);
        }
		return str;
	}
}