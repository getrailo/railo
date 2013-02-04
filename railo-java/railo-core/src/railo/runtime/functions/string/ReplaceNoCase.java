/**
 * Implements the CFML Function replacenocase
 */
package railo.runtime.functions.string;

import java.util.Map;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;

public final class ReplaceNoCase implements Function {

	
	public static String call(PageContext pc , String str, String sub1, String sub2) throws ExpressionException {
		
		if ( sub1.isEmpty() )
			throw new ExpressionException("The string length of parameter 2 of function replace must be greater than 0");
		
		return StringUtil.replace(str, sub1, sub2, true, true);
	}
	

	public static String call(PageContext pc , String str, String sub1, String sub2, String scope) throws ExpressionException {
		
		if ( sub1.isEmpty() )
			throw new ExpressionException("The string length of parameter 2 of function replace must be greater than 0");
		
		return StringUtil.replace(str, sub1, sub2, !scope.equalsIgnoreCase("all"), true);
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
	
	
	public static String call( PageContext pc, String input, Object struct ) throws PageException {
		
		if ( !( struct instanceof Map ) )
			throw new ExpressionException("When passing only two parameters, the second parameter must be a Struct.");		
		
		return StringUtil.replaceMap( input, (Map)struct, true );
	}
	
}