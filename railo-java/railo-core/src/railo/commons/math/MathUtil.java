package railo.commons.math;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;


/**
 * Math Util
 */
public final class MathUtil {

    /**
     * abs
     * @param number
     * @return abs value
     */
    public static double abs(double number) {
        return (number <= 0.0D) ? 0.0D - number : number;
    }

    public static double sgn(double number) {
        return number != 0.0d ? number >= 0.0d ? 1 : -1 : 0;
    }

	public static int nextPowerOf2(int value) {

		int result = 1;
		while (result < value)
			result = result << 1;

		return result;
	}

	public static BigDecimal divide(BigDecimal left, BigDecimal right) {
		try {
			return left.divide(right,BigDecimal.ROUND_UNNECESSARY);
		}
		catch (ArithmeticException ex) {
			return left.divide(right,MathContext.DECIMAL128);
		}
	}

	public static BigDecimal add(BigDecimal left, BigDecimal right) {
		try {
			return left.add(right,MathContext.UNLIMITED);
		}
		catch (ArithmeticException ex) {
			return left.add(right,MathContext.DECIMAL128);
		}
	}

	public static BigDecimal subtract(BigDecimal left, BigDecimal right) {
		try {
			return left.subtract(right,MathContext.UNLIMITED);
		}
		catch (ArithmeticException ex) {
			return left.subtract(right,MathContext.DECIMAL128);
		}
	}

	public static BigDecimal multiply(BigDecimal left, BigDecimal right) {
		try {
			return left.multiply(right,MathContext.UNLIMITED);
		}
		catch (ArithmeticException ex) {
			return left.multiply(right,MathContext.DECIMAL128);
		}
	}
}
