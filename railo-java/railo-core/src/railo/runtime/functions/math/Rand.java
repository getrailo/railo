/**
 * Implements the CFML Function rand
 */
package railo.runtime.functions.math;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Random;

import railo.commons.collection.HashMapPro;
import railo.runtime.PageContext;
import railo.runtime.crypt.CFMXCompat;
import railo.runtime.exp.ExpressionException;
import railo.runtime.ext.function.Function;

public final class Rand implements Function {

    private static Map<String, Random> randoms = new HashMapPro<String, Random>();

	public static double call(PageContext pc ) throws ExpressionException {

		return getRandom( CFMXCompat.ALGORITHM_NAME, Double.NaN ).nextDouble();
	}

	public static double call(PageContext pc, String algorithm) throws ExpressionException {

        return getRandom( algorithm, Double.NaN ).nextDouble();
	}

    static synchronized Random getRandom(String algorithm, Double seed) throws ExpressionException {

        algorithm = algorithm.toLowerCase();

        Random result = randoms.get( algorithm );

        if ( result == null || !seed.isNaN() ) {
            if (CFMXCompat.ALGORITHM_NAME.equalsIgnoreCase( algorithm )) {

                result = new Random();
            }
            else {

                try {

                    result = SecureRandom.getInstance( algorithm );
                }
                catch (NoSuchAlgorithmException e) {
                    throw new ExpressionException("random algorithm ["+algorithm+"] is not installed on the system",e.getMessage());
                }
            }

            if ( !seed.isNaN() )
                result.setSeed( seed.longValue() );

            randoms.put( algorithm, result );
        }

        return result;
    }
}