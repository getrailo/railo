package railo.runtime.functions.displayFormatting;

import java.util.Locale;
import java.util.TimeZone;

import railo.commons.date.TimeZoneUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.CasterException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.date.DateCaster;
import railo.runtime.type.dt.DateTime;

/**
 * Implements the CFML Function dateformat
 */
public final class DateFormat implements Function {
	
	public static String call(PageContext pc , Object object) throws PageException {
		return _call(pc,object,"dd-mmm-yy",ThreadLocalPageContext.getTimeZone(pc));
	}
	public static String call(PageContext pc , Object object, String mask) throws PageException {
		return _call(pc, object, mask, ThreadLocalPageContext.getTimeZone(pc));
	}
	public static String call(PageContext pc , Object object, String mask,String strTimezone) throws PageException {
		return _call(pc, object, mask, strTimezone==null?ThreadLocalPageContext.getTimeZone(pc):TimeZoneUtil.toTimeZone(strTimezone));
	}
	private static String _call(PageContext pc , Object object, String mask,TimeZone tz) throws PageException {
	    Locale locale=Locale.US;
	    
		DateTime datetime = DateCaster.toDateAdvanced(object,tz,null);
			//Caster.toDate(object,true,tz,null);
		if(datetime==null) {
			if(StringUtil.isEmpty(object,true)) return "";
		    throw new CasterException(object,"datetime");
		    //if(!Decision.isSimpleValue(object))
		    //    throw new ExpressionException("can't convert object of type "+Type.getName(object)+" to a datetime value");
		    //throw new ExpressionException("can't convert value "+object+" to a datetime value");
		}
		return new railo.runtime.format.DateFormat(locale).format(datetime,mask,tz);
	}
}