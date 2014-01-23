package railo.commons.math;


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

}
