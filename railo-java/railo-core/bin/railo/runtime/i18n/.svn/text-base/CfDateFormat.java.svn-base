package railo.runtime.i18n;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class CfDateFormat extends SimpleDateFormat {

	private String pattern;
	private Locale locale;

	public CfDateFormat(String pattern, Locale locale) {
		super(pattern,locale);
		this.pattern=pattern;
		this.locale=locale;
	}

	/**
	 * @return the pattern
	 */
	public String getPattern() {
		return pattern;
	}

	/**
	 * @return the locale
	 */
	public Locale getLocale() {
		return locale;
	}

	/**
	 *
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return this.getLocale().getDisplayName()+"-"+toPattern();
	}
}
