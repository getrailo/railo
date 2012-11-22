/**
 * Implements the CFML Function replacenocase
 */
package railo.runtime.functions.string;

import java.util.Map;
import railo.commons.lang.StringUtil;
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
		StringBuilder sb=new StringBuilder( sub2.length() > sub1.length() ? (int)Math.ceil( str.length() * 1.2 ) : str.length() );
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


	public static String call( PageContext pc, String input, Object find, String repl, String scope ) throws ExpressionException {
		
		if ( !( find instanceof String ) )
			throw new ExpressionException("When passing three parameters or more, the second parameter must be a String.");
		
		if ( ((String)find).isEmpty() )
			throw new ExpressionException("The string length of parameter 2 of function replace must be greater than 0");
		
		return call( pc , input, (String)find, repl, scope );
	}

	
	public static String call( PageContext pc, String input, Object find, String repl ) throws ExpressionException {
		
		if ( !( find instanceof String ) )
			throw new ExpressionException("When passing three parameters or more, the second parameter must be a String.");
		
		if ( ((String)find).isEmpty() )
			throw new ExpressionException("The string length of parameter 2 of function replace must be greater than 0");
		
		return call( pc , input, (String)find, repl, "one" );
	}
	
	
	public static String call( PageContext pc, String input, Object struct ) throws ExpressionException {
		
		if ( !( struct instanceof Map ) )
			throw new ExpressionException("When passing only two parameters, the second parameter must be a Struct.");		
		
		return StringUtil.replaceMap( input, (Map)struct, false );
	}
	
}