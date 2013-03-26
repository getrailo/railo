package railo.runtime.functions.displayFormatting;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

import railo.commons.date.TimeZoneUtil;
import railo.runtime.PageContext;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.dt.DateTime;

/**
 * Implements the CFML Function dateformat
 */
public final class DateTimeFormat extends BIF {

	private static final long serialVersionUID = 134840879454373440L;
	public static final String DEFAULT_MASK = "dd-MMM-yyyy HH:mm:ss";

	/**
	 * @param pc
	 * @param object
	 * @return Formated Time Object as String
	 * @throws ExpressionException
	 */
	public static String call(PageContext pc , Object object) throws ExpressionException {
		return invoke(pc,object, DEFAULT_MASK,Locale.US,ThreadLocalPageContext.getTimeZone(pc));
	}
	
	/**
	 * @param pc
	 * @param object
	 * @param mask Characters that show how CFML displays a date:
	 * @return Formated Time Object as String
	 * @throws ExpressionException
	 */
	public static String call(PageContext pc , Object object, String mask) throws ExpressionException {
		return invoke(pc,object,mask,Locale.US,ThreadLocalPageContext.getTimeZone(pc));
	}

	public static String call(PageContext pc , Object object, String mask,String strTimezone) throws ExpressionException {
		return invoke(pc,object,mask, Locale.US,strTimezone==null?ThreadLocalPageContext.getTimeZone(pc):TimeZoneUtil.toTimeZone(strTimezone));
	}
	
	public static String invoke(PageContext pc , Object object, String mask,Locale locale,TimeZone tz) throws ExpressionException {
		if(mask==null) mask=DEFAULT_MASK;
		if(locale==null) locale=Locale.US;
		DateTime datetime = Caster.toDate(object,true,tz,null);
		if(datetime==null) {
		    if(object.toString().trim().length()==0) return "";
		    throw new ExpressionException("can't convert value "+object+" to a datetime value");
		}
		
		SimpleDateFormat format = new SimpleDateFormat(mask, locale);
		format.setTimeZone(tz);
        return format.format(datetime);
	}
	
	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		if(args.length==1)return call(pc,args[0]);
		if(args.length==2)return call(pc,args[0],Caster.toString(args[1]));
		return call(pc,args[0],Caster.toString(args[1]),Caster.toString(args[2]));
	}
}