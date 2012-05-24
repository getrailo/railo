/**
 * Implements the Cold Fusion Function replacenocase
 */
package railo.runtime.functions.string;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.ext.function.Function;

public final class ReplaceNoCase implements Function {

	public static String call(PageContext pc , String str, String sub1, String sub2) throws ExpressionException {
		return call(pc , str, sub1, sub2, "one");
	}

	public static String call(PageContext pc , String str, String sub1, String sub2, String scope) throws ExpressionException {
		if(sub1.length()==0){
			throw new ExpressionException("the string length of Parameter 2 of function replaceNoCase which is now ["+sub1.length()+"] must be greater than 0");
		}
		//if(sub1.equals(sub2)) return str;
		boolean doAll=scope.equalsIgnoreCase("all");
		
		
		String lcStr=str.toLowerCase();
		String lcSub1=sub1.toLowerCase();
		StringBuffer sb=new StringBuffer();
		int start=0;
		int pos;
		int sub1Length=sub1.length();
		while((pos=lcStr.indexOf(lcSub1,start))!=-1){
			sb.append(str.substring(start,pos));
			sb.append(sub2);
			start=pos+sub1Length;
			if(!doAll)break;
		}
		sb.append(str.substring(start));
		
		return sb.toString();
	}
	
	
}