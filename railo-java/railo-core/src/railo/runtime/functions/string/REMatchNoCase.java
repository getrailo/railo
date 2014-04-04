/**
 * Implements the CFML Function refind
 */
package railo.runtime.functions.string;

import org.apache.oro.text.regex.MalformedPatternException;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.regex.Perl5Util;
import railo.runtime.type.Array;

public final class REMatchNoCase extends BIF {
	
	private static final long serialVersionUID = 7300917722574558505L;

	public static Array call(PageContext pc , String regExpr, String str) throws ExpressionException {
		try {
			return Perl5Util.match(regExpr, str, 1, false);
		} 
		catch (MalformedPatternException e) {
			throw new FunctionException(pc,"REMatchNoCase",1,"regularExpression",e.getMessage());
		}
	}

    @Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
    	if(args.length==2)
			return call(pc, Caster.toString(args[0]), Caster.toString(args[1]));
    	
		throw new FunctionException(pc, "REMatchNoCase", 2, 2, args.length);
	}
}