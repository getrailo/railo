/**
 * Implements the CFML Function rereplace
 */
package railo.runtime.functions.string;

import org.apache.oro.text.regex.MalformedPatternException;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.regex.Perl5Util;

public final class REReplace extends BIF {

	private static final long serialVersionUID = -1140669656936340678L;

	public static String call(String string, String regExp, String replace) throws ExpressionException { // MUST is this really needed?
	    try {
			return Perl5Util.replace(string,regExp,replace,true,false);
		} catch (MalformedPatternException e) {
			throw new ExpressionException("reReplace"+"second"+"regularExpression"+e.getMessage());
		}
	}
	public static String call(PageContext pc , String string, String regExp, String replace) throws ExpressionException {
	    try {
			return Perl5Util.replace(string,regExp,replace,true,false);
		} catch (MalformedPatternException e) {
			throw new FunctionException(pc,"reReplace",2,"regularExpression",e.getMessage());
		}
	}
	public static String call(PageContext pc , String string, String regExp, String replace, String scope) throws ExpressionException {
		try {
			if(scope.equalsIgnoreCase("all"))return Perl5Util.replace(string,regExp,replace,true,true);
			return Perl5Util.replace(string,regExp,replace,true,false);
		} catch (MalformedPatternException e) {
		    throw new FunctionException(pc,"reReplace",2,"regularExpression",e.getMessage());
		}
	}

    @Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
    	if(args.length==3)
			return call(pc, Caster.toString(args[0]), Caster.toString(args[1]), Caster.toString(args[2]));
    	if(args.length==4)
			return call(pc, Caster.toString(args[0]), Caster.toString(args[1]), Caster.toString(args[2]), Caster.toString(args[3]));
    	
		throw new FunctionException(pc, "REReplace", 3, 4, args.length);
	}

}

