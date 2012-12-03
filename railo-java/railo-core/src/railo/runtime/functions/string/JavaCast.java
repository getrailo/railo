/**
 * Implements the CFML Function javacast
 */
package railo.runtime.functions.string;

import java.math.BigDecimal;
import java.math.BigInteger;

import railo.commons.lang.ClassException;
import railo.commons.lang.ClassUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;

public final class JavaCast implements Function {
	public static Object calls(PageContext pc , String string, Object object) throws PageException {
		throw new ExpressionException("method javacast not implemented yet"); // MUST ????
	}
	public static Object call(PageContext pc , String type, Object obj) throws PageException {
		type=type.trim();
		String lcType=StringUtil.toLowerCase(type);
		
		if(type.endsWith("[]")){
			return toArray(pc,type, lcType, obj);
			
		}
		Class clazz = toClass(pc, lcType, type);
		return to(pc,obj,clazz);
		
	}
	
	public static Object toArray(PageContext pc,String type,String lcType, Object obj) throws PageException {
		lcType=lcType.substring(0,lcType.length()-2);
		type=type.substring(0,type.length()-2);
		
		
		
		Array arr = Caster.toArray(obj);
		Class clazz = toClass(pc, lcType, type);
		Object trg= java.lang.reflect.Array.newInstance(clazz, arr.size());
		
		
		for(int i=arr.size()-1;i>=0;i--) {
			java.lang.reflect.Array.set(trg, i,to(pc,arr.getE(i+1),clazz));
			
		}
		return trg;
	}
	
	
	private static Object to(PageContext pc, Object obj,Class trgClass) throws PageException {
		if(trgClass==null)return Caster.toNull(obj); 
		else if(trgClass==BigDecimal.class)return new BigDecimal(Caster.toString(obj)); 
		else if(trgClass==BigInteger.class)return new BigInteger(Caster.toString(obj)); 
		return Caster.castTo(pc, trgClass, obj);
		//throw new ExpressionException("can't cast only to the following data types (bigdecimal,int, long, float ,double ,boolean ,string,null ), "+lcType+" is invalid");
	}
	
	private static Class toClass(PageContext pc,String lcType, String type) throws PageException {
		 
		if(lcType.equals("null")){
			return null; 
		}  
		if(lcType.equals("biginteger")){
			return BigInteger.class; 
		}  
		if(lcType.equals("bigdecimal")){
			return BigDecimal.class; 
		} 
		try {
			return ClassUtil.toClass(type);
		} catch (ClassException e) {
			throw Caster.toPageException(e);
		}
	}
	
}