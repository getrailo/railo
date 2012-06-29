/**
 * Implements the CFML Function comparenocase
 */
package railo.runtime.functions.string;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

public final class CompareNoCase implements Function {
	public static double call(PageContext pc , String str1, String str2) {
		int compare=str1.compareToIgnoreCase(str2);
		if(compare==0) return 0;
		return compare>0?1:-1;
		
		//return Compare.call(pc,str1.toLowerCase(),str2.toLowerCase());
	}
}