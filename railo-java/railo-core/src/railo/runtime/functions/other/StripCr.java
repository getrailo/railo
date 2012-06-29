/**
 * Implements the CFML Function stripcr
 */
package railo.runtime.functions.other;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

public final class StripCr implements Function {

	public static String call(PageContext pc , String string) {
		StringBuffer sb=new StringBuffer(string.length());
		int start=0;
		int pos=0;
		
		while((pos=string.indexOf('\r',start))!=-1) {
			sb.append(string.substring(start,pos));
			start=pos+1;
		}
		if(start<string.length())sb.append(string.substring(start,string.length()));
		
		return sb.toString();
	}	
}