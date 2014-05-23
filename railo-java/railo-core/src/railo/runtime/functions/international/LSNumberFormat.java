/** DIFF 23
 * Implements the CFML Function lsnumberformat
 */
package railo.runtime.functions.international;

import java.util.Locale;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.functions.displayFormatting.NumberFormat;
import railo.runtime.util.InvalidMaskException;

public final class LSNumberFormat implements Function {
	
	private static final long serialVersionUID = -7981883050285346336L;

	public static String call(PageContext pc , Object object) throws PageException {
		return _call(pc, object, null, pc.getLocale());
	}
	
	public static String call(PageContext pc , Object object, String mask) throws PageException {
	    return _call(pc, object, mask, pc.getLocale());
	}
	
	public static String call(PageContext pc , Object object, String mask, Locale locale) throws PageException {
		return _call(pc, object, mask, 
				locale==null?pc.getLocale():locale);
	}
	
	private static String _call(PageContext pc , Object object, String mask, Locale locale) throws PageException {
		try {
            if(mask==null) 
            	return new railo.runtime.util.NumberFormat().format(locale,NumberFormat.toNumber(pc,object));
			return new railo.runtime.util.NumberFormat().format(locale,NumberFormat.toNumber(pc,object),mask);
        } 
        catch (InvalidMaskException e) {
            throw new FunctionException(pc,"lsnumberFormat",1,"number",e.getMessage());
        }
	}
}