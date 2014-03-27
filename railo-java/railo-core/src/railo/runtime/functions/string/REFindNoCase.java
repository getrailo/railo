
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

/**
 * Implements the CFML Function refindnocase
 */
public final class REFindNoCase extends BIF {

	private static final long serialVersionUID = 1562665117076202965L;

	public static Object call(PageContext pc , String regExpr, String str) throws ExpressionException {
		return call(pc,regExpr,str,1,false);
	}
	public static Object call(PageContext pc , String regExpr, String str, double start) throws ExpressionException {
		return call(pc,regExpr,str,start,false);
	}
	public static Object call(PageContext pc , String regExpr, String str, double start, boolean returnsubexpressions) throws ExpressionException {
		try {
			if(returnsubexpressions)
				return Perl5Util.find(regExpr,str,(int)start,false);
			return new Double(Perl5Util.indexOf(regExpr,str,(int)start,false));
		} catch (MalformedPatternException e) {
			throw new FunctionException(pc,"reFindNoCase",1,"regularExpression",e.getMessage());
		}
	}

    @Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
    	if(args.length==2)
			return call(pc, Caster.toString(args[0]), Caster.toString(args[1]));
    	if(args.length==3)
			return call(pc, Caster.toString(args[0]), Caster.toString(args[1]), Caster.toDoubleValue(args[2]));
    	if(args.length==4)
			return call(pc, Caster.toString(args[0]), Caster.toString(args[1]), Caster.toDoubleValue(args[2]), Caster.toBooleanValue(args[3]));
    	
		throw new FunctionException(pc, "REFindNoCase", 2, 4, args.length);
	}
}