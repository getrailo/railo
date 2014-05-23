package railo.runtime.functions.international;


import java.util.Locale;
import java.util.TimeZone;

import railo.runtime.PageContext;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.ext.function.Function;
import railo.runtime.functions.displayFormatting.DateTimeFormat;

/**
 * Implements the CFML Function dateformat
 */
public final class LSDateTimeFormat implements Function {

	private static final long serialVersionUID = -1677384484943178492L;

	public static String call(PageContext pc , Object object) throws ExpressionException {
		return DateTimeFormat.invoke(pc,object, null,pc.getLocale(),ThreadLocalPageContext.getTimeZone(pc));
	}
	
	public static String call(PageContext pc , Object object, String mask) throws ExpressionException {
		return DateTimeFormat.invoke(pc,object, mask,pc.getLocale(),ThreadLocalPageContext.getTimeZone(pc));
	}

	public static String call(PageContext pc , Object object, String mask, Locale locale) throws ExpressionException {
		return DateTimeFormat.invoke(pc,object, mask,locale,ThreadLocalPageContext.getTimeZone(pc));
	}

	public static String call(PageContext pc , Object object, String mask, Locale locale, TimeZone tz) throws ExpressionException {
		return DateTimeFormat.invoke(
				pc,object,mask, 
				locale==null?pc.getLocale():locale,
				tz==null?ThreadLocalPageContext.getTimeZone(pc):tz);
	}
	
}