/**
 * Implements the CFML Function replace
 */
package railo.runtime.functions.string;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;

public final class Replace extends BIF {
	
	private static final long serialVersionUID = -313884944032266348L;

	public static String call(PageContext pc , String str, String sub1, String sub2) {
		return _call(pc, str, sub1, sub2, true);
	}
	
	public static String call(PageContext pc , String str, String sub1, String sub2, String scope) {
		return _call(pc, str, sub1, sub2, !scope.equalsIgnoreCase("all"));
	}

	public static String call( PageContext pc, String input, Object find, String repl, String scope ) throws PageException {
		return _call(pc, input, find, repl, !scope.equalsIgnoreCase("all"));
	}

	public static String call( PageContext pc, String input, Object find, String repl ) throws PageException {
		return _call(pc, input, find, repl, true);
	}
	
	public static String call( PageContext pc, String input, Object struct ) throws PageException {
		if(!Decision.isStruct(struct))
			throw new FunctionException(pc,"replace",2,"sub1","When passing only two parameters, the second parameter must be a Struct.");
		return StringUtil.replaceMap( input, Caster.toMap(struct), false );
	}
	
	private static String _call(PageContext pc , String str, String sub1, String sub2, boolean firstOnly) {
		if (StringUtil.isEmpty(sub1))
			return str;
		return StringUtil.replace(str, sub1, sub2, firstOnly);
	}
	
	private static String _call( PageContext pc, String input, Object find, String repl, boolean onlyFirst) throws PageException {
		if(!Decision.isSimpleValue(find ) )
			throw new FunctionException(pc,"replace",2,"sub1","When passing three parameters or more, the second parameter must be a simple value.");
		return _call(pc, input, Caster.toString(find), repl, onlyFirst);
	}

    @Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
    	if(args.length==2)
			return call(pc, Caster.toString(args[0]), args[1]);
    	if(args.length==3)
			return call(pc, Caster.toString(args[0]), args[1],Caster.toString(args[2]));
    	if(args.length==4)
			return call(pc, Caster.toString(args[0]), args[1],Caster.toString(args[2]),Caster.toString(args[3]));
    	
		throw new FunctionException(pc, "Replace", 2, 4, args.length);
	}

}