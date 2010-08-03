package railo.runtime.functions.decision;

import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternMatcherInput;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;

/**
 * 
 */
public final class IsValid implements Function {

	// others
	/**
	 * check for many diff types
	 * @param pc
	 * @param type
	 * @param value
	 * @return
	 * @throws ExpressionException
	 */
	public static boolean call(PageContext pc, String type, Object value) throws ExpressionException {
		type=type.trim().toLowerCase();
		if("range".equals(type))
			throw new FunctionException(pc,"isValid",1,"type","for [range] you have to define a min and max value [isValid(\"range\",value,min,max)]");
		if("regex".equals(type) || "regular_expression".equals(type))
			throw new FunctionException(pc,"isValid",1,"type","for [regex] you have to define a pattern [isValid(\"regex\",value,pattern)]");

		return Decision.isValid(type, value);
	}
	
	/**
	 * regex check
	 * @param pc
	 * @param type
	 * @param value
	 * @param pattern_or_min
	 * @return
	 * @throws PageException 
	 */
	public static boolean call(PageContext pc, String type, Object value, Object objPattern) throws PageException {
		type=type.trim().toLowerCase();
		if(!"regex".equals(type) && !"regular_expression".equals(type))
			throw new FunctionException(pc,"isValid",1,"type","wrong attribute count for type ["+type+"]");
		
		String str=Caster.toString(value,null);
		if(str==null)
			return false;
		
		try {
			Pattern pattern = new Perl5Compiler().compile(Caster.toString(objPattern), Perl5Compiler.MULTILINE_MASK);
	        PatternMatcherInput input = new PatternMatcherInput(str);
	        return new Perl5Matcher().matches(input, pattern);
		} catch (MalformedPatternException e) {
			return false;
		}
	}
	
	
	
	/**
	 * range check
	 * @param pc
	 * @param type
	 * @param value
	 * @param pattern_or_min
	 * @param max
	 * @return
	 * @throws ExpressionException
	 */
	public static boolean call(PageContext pc, String type, Object value, Object objMin, Object objMax) throws ExpressionException {
		type=type.trim().toLowerCase();
		if(!"range".equals(type))
			throw new FunctionException(pc,"isValid",1,"type","wrong attribute count for type ["+type+"]");

		double number=Caster.toDoubleValue(value,Double.NaN);
		if(!Decision.isValid(number)) return false;
		
		double min=Caster.toDoubleValue(objMin,Double.NaN);
		if(!Decision.isValid(min))
			throw new FunctionException(pc,"isValid",3,"min","value must be numeric");

		double max=Caster.toDoubleValue(objMax,Double.NaN);
		if(!Decision.isValid(max))
			throw new FunctionException(pc,"isValid",4,"max","value must be numeric");

		return number>=min && number<=max;
	}
}