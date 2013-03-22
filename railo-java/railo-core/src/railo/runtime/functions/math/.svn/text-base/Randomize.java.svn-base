/**
 * Implements the Cold Fusion Function randomize
 */
package railo.runtime.functions.math;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.ext.function.Function;

public final class Randomize implements Function {
	
	private static Random simpleRandom=null;
	
	public static double call(PageContext pc , double number) {
		return new Random((long)number).nextDouble();
	}
	public static double call(PageContext pc , double number, String algorithm) throws ExpressionException {
		algorithm=StringUtil.toLowerCase(algorithm);
		if("cfmx_compat".equals(algorithm)) {
			return new Random((long)number).nextDouble();
		}
		
		try {
			SecureRandom secRandom = SecureRandom.getInstance(algorithm);
			secRandom.setSeed((long)number);
			return secRandom.nextDouble();
		} catch (NoSuchAlgorithmException e) {
			throw new ExpressionException("random algorithm ["+algorithm+"] is not installed on the system",e.getMessage());
		}	
		//return new Random((long)number).nextDouble();
	}
	
}