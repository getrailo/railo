package railo.runtime.util;

import railo.runtime.exp.ExpressionException;

/**
 * checks for a Number range
 */
public final class NumberRange {
	
	/**
	 * checks if number between from and to (inlude from and to)
	 * @param number
	 * @param from
	 * @param to
	 * @return given number when range ok
	 * @throws ExpressionException
	 */
	public static double range(double number, double from, double to) throws ExpressionException {
		if(number>=from && number<=to) return number;
		throw new ExpressionException("number must between ["+from+" - "+to+"] now "+number+"");
	} 
	/**
	 * checks if number is greater than from (inlude from)
	 * @param number
	 * @param from
	 * @return  given number when range ok
	 * @throws ExpressionException
	 */
	public static double range(double number, double from) throws ExpressionException {
		if(number>=from) return number;
		throw new ExpressionException("number must be greater than ["+from+"] now "+number+"");
	} 
	
	public static int range(int number, int from) throws ExpressionException {
		if(number>=from) return number;
		throw new ExpressionException("number must be greater than ["+from+"] now "+number+"");
	} 

}