/** DIFF 23
 * Implements the Cold Fusion Function lsnumberformat
 */
package railo.runtime.functions.international;

import java.util.Locale;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.functions.displayFormatting.NumberFormat;
import railo.runtime.i18n.LocaleFactory;
import railo.runtime.util.InvalidMaskException;

public final class LSNumberFormat implements Function {
	public static String call(PageContext pc , Object object) throws PageException {
		return new railo.runtime.util.NumberFormat().format(pc.getLocale(),NumberFormat.toNumber(pc,object));
	}
	public static String call(PageContext pc , Object object, String mask) throws PageException {
	    //return NumberFormat.call(pc,object,string);
	    return call(pc, object, mask, pc.getLocale());
	}
	
	public static String call(PageContext pc , Object object, String mask, String locale) throws PageException {
		return call(pc, object, mask, LocaleFactory.getLocale(locale));
	}
	
	private static String call(PageContext pc , Object object, String mask, Locale locale) throws PageException {
		try {
            return new railo.runtime.util.NumberFormat().format(locale,NumberFormat.toNumber(pc,object),mask);
        } 
        catch (InvalidMaskException e) {
            throw new FunctionException(pc,"lsnumberFormat",1,"number",e.getMessage());
        }
	}
}