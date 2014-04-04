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

public final class REFind extends BIF {
	public static Object call(PageContext pc , String regExpr, String str) throws ExpressionException {
		return call(pc,regExpr,str,1,false);
	}
	public static Object call(PageContext pc , String regExpr, String str, double start) throws ExpressionException {
		return call(pc,regExpr,str,start,false);
	}
	public static Object call(PageContext pc , String regExpr, String str, double start, boolean returnsubexpressions) throws ExpressionException {
		try {
			if(returnsubexpressions)
				return Perl5Util.find(regExpr,str,(int)start,true);
			return new Double(Perl5Util.indexOf(regExpr,str,(int)start,true));
		} catch (MalformedPatternException e) {
			throw new FunctionException(pc,"reFind",1,"regularExpression",e.getMessage());
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
    	
		throw new FunctionException(pc, "REFind", 2, 4, args.length);
	}
}