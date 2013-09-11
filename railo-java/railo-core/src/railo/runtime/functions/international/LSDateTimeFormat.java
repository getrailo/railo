package railo.runtime.functions.international;


import railo.commons.date.TimeZoneUtil;
import railo.runtime.PageContext;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.ext.function.Function;
import railo.runtime.functions.displayFormatting.DateTimeFormat;
import railo.runtime.i18n.LocaleFactory;

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

	public static String call(PageContext pc , Object object, String mask,String strLocale) throws ExpressionException {
		return DateTimeFormat.invoke(pc,object, mask,LocaleFactory.getLocale(strLocale),ThreadLocalPageContext.getTimeZone(pc));
	}

	public static String call(PageContext pc , Object object, String mask,String strLocale,String strTimezone) throws ExpressionException {
		return DateTimeFormat.invoke(
				pc,object,mask, 
				strLocale==null?pc.getLocale():LocaleFactory.getLocale(strLocale),
				strTimezone==null?ThreadLocalPageContext.getTimeZone(pc):TimeZoneUtil.toTimeZone(strTimezone));
	}
	
}