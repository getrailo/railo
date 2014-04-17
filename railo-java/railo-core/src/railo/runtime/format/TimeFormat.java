package railo.runtime.format;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import railo.commons.date.JREDateTimeUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.functions.dateTime.Beat;
import railo.runtime.op.Caster;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.dt.DateTimeImpl;

public final class TimeFormat extends BaseFormat implements Format {
	
	
	
	/**
	 * constructor of the class
	 * @param locale
	 */
	public TimeFormat(Locale locale) {
		super(locale);
	}
	

	/**
	 * formats a date to a cfml date format (short)
	 * @param date
	 * @return formated date
	 */
	public String format(Date date) {
		return format(date,"short");
	}
	
	/**
	 * formats a date to a cfml date format
	 * @param date
	 * @param mask
	 * @return formated date
	 */
	public String format(Date date,String mask) {
		DateTime dt=(date instanceof DateTime)?(DateTime)date:new DateTimeImpl(date.getTime(),false);
		return format(dt,mask,null);
	}
	

	public String format(DateTime date,String mask, TimeZone tz) {
		return format(date.getTime(), mask, tz);
	}
	public String format(long time,String mask, TimeZone tz) {
		Calendar calendar = JREDateTimeUtil.getThreadCalendar(getLocale(),tz);
		calendar.setTimeInMillis(time);

		String lcMask=StringUtil.toLowerCase(mask);
		if(lcMask.equals("short"))			return getAsString(calendar,DateFormat.SHORT,tz);
		else if(lcMask.equals("medium"))	return getAsString(calendar,DateFormat.MEDIUM,tz);
		else if(lcMask.equals("long")) 		return getAsString(calendar,DateFormat.LONG,tz);
		else if(lcMask.equals("full"))		return getAsString(calendar,DateFormat.FULL,tz);
		else if(lcMask.equals("beat"))	{
			return Caster.toString(Beat.format(time)); 
		}
		
		int len=mask.length();
		int pos=0;
		if(len==0) return "";
		
		StringBuilder formated=new StringBuilder();
		
		
		
		for(;pos<len;pos++) {
			char c=mask.charAt(pos);
			char next=(len>pos+1)?mask.charAt(pos+1):(char)0;
			
			switch(c) {

			// h: Hours; no leading zero for single-digit hours (12-hour clock) 
			// hh: Hours; leading zero for single-digit hours. (12-hour clock) 
				case 'h':
					int hour1=calendar.get(Calendar.HOUR_OF_DAY);
					if(hour1==0)hour1=12;
					if(hour1>12)hour1=hour1-12;
					if(next=='h') {
						formated.append(hour1<10?"0"+hour1:""+hour1);
						pos++;
					}
					else {
						formated.append(hour1);
					}					
				break;

			// H: Hours; no leading zero for single-digit hours (24-hour clock) 
			// HH: Hours; leading zero for single-digit hours (24-hour clock) 
				case 'H':
					int hour2=calendar.get(Calendar.HOUR_OF_DAY);
					if(next=='H') {
						formated.append(hour2<10?"0"+hour2:""+hour2);
						pos++;
					}
					else {
						formated.append(hour2);
					}					
				break;

			// m: Minutes; no leading zero for single-digit minutes 
			// mm: Minutes; leading zero for single-digit minutes 
				case 'M':
				case 'm':
					int minute=calendar.get(Calendar.MINUTE);
					if(next=='M' || next=='m') {
						formated.append(minute<10?"0"+minute:""+minute);
						pos++;
					}
					else {
						formated.append(minute);
					}					
				break;

			// s: Seconds; no leading zero for single-digit seconds 
			// ss: Seconds; leading zero for single-digit seconds 
				case 's':
				case 'S':
					int second=calendar.get(Calendar.SECOND);
					if(next=='S' || next=='s') {
						formated.append(second<10?"0"+second:""+second);
						pos++;
					}
					else {
						formated.append(second);
					}					
				break;

			// l: Milliseconds 
				case 'l':
				case 'L':
					char nextnext=(len>pos+2)?mask.charAt(pos+2):(char)0;

					String millis=Caster.toString(calendar.get(Calendar.MILLISECOND));
					if(next=='L' || next=='l') {
						if(millis.length()==1)millis="0"+millis;
						pos++;
					}
					if(nextnext=='L' || nextnext=='l') {
						if(millis.length()==2)millis="0"+millis;
						pos++;
					}
					formated.append(millis);	
					
					
					
				break;

			// t: One-character time marker string, such as A or P. 
			// tt: Multiple-character time marker string, such as AM or PM 
				case 't':
				case 'T':
					boolean isAm=calendar.get(Calendar.HOUR_OF_DAY)<12;
					if(next=='T' || next=='t') {
						formated.append(isAm?"AM":"PM");
						pos++;
					}
					else {
						formated.append(isAm?"A":"P");
					}					
				break;
				case 'z':
				case 'Z':
					// count next z and jump to last z (max 6)
					int start=pos;
					while((pos+1)<len && Character.toLowerCase(mask.charAt(pos+1))=='z'){
						pos++;
						if(pos-start>4)break;
					}
					if(pos-start>2)formated.append(tz.getDisplayName(getLocale()));	
					else formated.append(tz.getID());	
					
				break;
				
			// Otherwise
				default:
					formated.append(c);
			}
		}
		return formated.toString();
	}

	private String getAsString(Calendar c, int style,TimeZone tz) {
		DateFormat df = DateFormat.getTimeInstance(style,getLocale());
		df.setTimeZone(tz);
		return df.format(c.getTime());	
	}

}