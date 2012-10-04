/**
 * Implements the CFML Function rand
 */
package railo.runtime.functions.math;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.ext.function.Function;

public final class Rand implements Function {
	
	public static double call(PageContext pc )  {
		return StrictMath.random();
	}
	public static double call(PageContext pc , String algorithm) throws ExpressionException {
		algorithm=StringUtil.toLowerCase(algorithm);
		if("cfmx_compat".equals(algorithm)) { // TODO install  IBMSecureRandom to support it
			return StrictMath.random();
		}
		try {
			return SecureRandom.getInstance(algorithm).nextDouble();
		} 
		catch (NoSuchAlgorithmException e) {
			throw new ExpressionException("random algorithm ["+algorithm+"] is not installed on the system",e.getMessage());
		}	
	}
}