package railo.runtime.format;

import java.util.Date;
/**
 * 
 */
public interface Format {
	/**
	 * formats a date to a cfml date format (short)
	 * @param date
	 * @return formated String
	 */
	public abstract String format(Date date);
	/**
	 * formats a date to a cfml date format
	 * @param date
	 * @param mask
	 * @return formated string
	 */
	public abstract String format(Date date, String mask);
}