/**
 * Implements the Cold Fusion Function javacast
 */
package railo.runtime.functions.string;

import java.math.BigDecimal;
import java.math.BigInteger;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;
import railo.runtime.op.Constants;

public final class JavaCast implements Function {
	public static Object calls(PageContext pc , String string, Object object) throws PageException {
		throw new ExpressionException("method javacast not implemented yet");
	}
	public static Object call(PageContext pc , String type, Object obj) throws PageException {
		type=StringUtil.toLowerCase(type);
		if(type.equals("boolean"))return Caster.toBoolean(obj); 
		else if(type.equals("byte"))return Caster.toByte(obj); 
		else if(type.equals("short"))return Caster.toShort(obj); 
		else if(type.equals("int"))return Constants.Integer(Caster.toDouble(obj).intValue()); 
		else if(type.equals("long"))return new Long(Caster.toDouble(obj).longValue()); 
		else if(type.equals("float"))return new Float(Caster.toDouble(obj).floatValue()); 
		else if(type.equals("double"))return Caster.toDouble(obj); 
		else if(type.equals("string"))return Caster.toString(obj); 
		else if(type.equals("null"))return Caster.toNull(obj); 
		else if(type.equals("char"))return Caster.toCharacter(obj); 
		else if(type.equals("bigdecimal"))return new BigDecimal(Caster.toString(obj)); 
		else if(type.equals("biginteger"))return new BigInteger(Caster.toString(obj)); 
		
		throw new ExpressionException("can't cast only to the following data types (bigdecimal,int, long, float ,double ,boolean ,string,null ), "+type+" is invalid");
	}
}