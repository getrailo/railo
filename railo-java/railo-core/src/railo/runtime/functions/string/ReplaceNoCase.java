/**
 * Implements the CFML Function replacenocase
 */
package railo.runtime.functions.string;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;

public final class ReplaceNoCase implements Function {

	private static final long serialVersionUID = -8001488161940178031L;

	public static String call(PageContext pc , String str, String sub1, String sub2) throws FunctionException {
		return _call(pc, str, sub1, sub2, true);
	}
	
	public static String call(PageContext pc , String str, String sub1, String sub2, String scope) throws FunctionException {
		return _call(pc, str, sub1, sub2, !scope.equalsIgnoreCase("all"));
	}

	public static String call( PageContext pc, String input, Object find, String repl, String scope ) throws PageException {
		return _call(pc, input, find, repl, !scope.equalsIgnoreCase("all") );
	}

	public static String call( PageContext pc, String input, Object find, String repl ) throws PageException {
		return _call(pc, input, find, repl, true);
	}
	
	private static String _call(PageContext pc , String str, String sub1, String sub2, boolean onlyFirst) throws FunctionException {
		if (StringUtil.isEmpty(sub1))
			throw new FunctionException(pc,"ReplaceNoCase",2,"sub1","The string length must be greater than 0");
		return StringUtil.replace(str, sub1, sub2, onlyFirst, true);
	}
	
	private static String _call( PageContext pc, String input, Object find, String repl , boolean onlyFirst) throws PageException {
		if(!Decision.isSimpleValue(find ) )
			throw new FunctionException(pc,"ReplaceNoCase",2,"sub1","When passing three parameters or more, the second parameter must be a String.");
		return _call(pc, input, Caster.toString(find), repl, onlyFirst);
	}
	
	public static String call( PageContext pc, String input, Object struct ) throws PageException {
		if(!Decision.isStruct(struct))
			throw new FunctionException(pc,"ReplaceNoCase",2,"sub1","When passing only two parameters, the second parameter must be a Struct.");
		return StringUtil.replaceMap( input, Caster.toStruct(struct), true );
	}
	
}