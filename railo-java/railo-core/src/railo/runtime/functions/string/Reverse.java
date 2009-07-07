/**
 * Implements the Cold Fusion Function reverse
 */
package railo.runtime.functions.string;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

public final class Reverse implements Function {
	public static String call(PageContext pc , String string) {
		char[] arr=string.toCharArray();
		StringBuffer sb=new StringBuffer(arr.length);
		
		for(int i=arr.length-1;i>=0;i--) {
			sb.append(arr[i]);
		}
		return sb.toString();
	}
}