package railo.commons.date;

import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;


public class JodaDateTimeUtil extends DateTimeUtil {
	
	public static Map zones=new HashMap();
	public static JREDateTimeUtil jreUtil=new JREDateTimeUtil();
	
	JodaDateTimeUtil() {	
	}
	
	long _toTime(TimeZone tz, int year, int month, int day, int hour,int minute, int second, int milliSecond) {
		try{
			return new DateTime(year, month, day, hour, minute, second, milliSecond, getDateTimeZone(tz)).getMillis();
		}
		catch(Throwable t){
			t.printStackTrace();
			return jreUtil._toTime(tz, year, month, day, hour, minute, second, milliSecond);
		}
	}
	
	/**
	 * @see railo.commons.date.DateTimeUtil#year(java.util.TimeZone, railo.runtime.type.dt.DateTime)
	 */
	public int getYear(TimeZone tz,railo.runtime.type.dt.DateTime dt){
		return new DateTime(dt.getTime(),getDateTimeZone(tz)).getYear();	
	}
	
	/**
	 * @see railo.commons.date.DateTimeUtil#getWeek(java.util.TimeZone, railo.runtime.type.dt.DateTime)
	 */
	public int getWeek(TimeZone tz,railo.runtime.type.dt.DateTime dt){
		return new DateTime(dt.getTime(),getDateTimeZone(tz)).getWeekOfWeekyear();	
	}

	/**
	 * @see railo.commons.date.DateTimeUtil#getMonth(java.util.TimeZone, railo.runtime.type.dt.DateTime)
	 */
	public int getMonth(TimeZone tz,railo.runtime.type.dt.DateTime dt){
		return new DateTime(dt.getTime(),getDateTimeZone(tz)).getMonthOfYear();
	}
	
	/**
	 * @see railo.commons.date.DateTimeUtil#getDay(java.util.TimeZone, railo.runtime.type.dt.DateTime)
	 */
	public int getDay(TimeZone tz,railo.runtime.type.dt.DateTime dt){
		return new DateTime(dt.getTime(),getDateTimeZone(tz)).getDayOfMonth();
	}

	/**
	 * @see railo.commons.date.DateTimeUtil#getHour(java.util.TimeZone, railo.runtime.type.dt.DateTime)
	 */
	public int getHour(TimeZone tz,railo.runtime.type.dt.DateTime dt){
		return new DateTime(dt.getTime(),getDateTimeZone(tz)).getHourOfDay();
	}
	
	/**
	 * @see railo.commons.date.DateTimeUtil#getMinute(java.util.TimeZone, railo.runtime.type.dt.DateTime)
	 */
	public int getMinute(TimeZone tz,railo.runtime.type.dt.DateTime dt){
		return new DateTime(dt.getTime(),getDateTimeZone(tz)).getMinuteOfHour();
	}
	
	/**
	 * @see railo.commons.date.DateTimeUtil#getSecond(java.util.TimeZone, railo.runtime.type.dt.DateTime)
	 */
	public int getSecond(TimeZone tz,railo.runtime.type.dt.DateTime dt){
		return new DateTime(dt.getTime(),getDateTimeZone(tz)).getSecondOfMinute();
	}
	
	/**
	 * @see railo.commons.date.DateTimeUtil#getMilliSecond(java.util.TimeZone, railo.runtime.type.dt.DateTime)
	 */
	public int getMilliSecond(TimeZone tz,railo.runtime.type.dt.DateTime dt){
		return new DateTime(dt.getTime(),getDateTimeZone(tz)).getMillisOfSecond();
	}

	/**
	 * @see railo.commons.date.DateTimeUtil#getDaysInMonth(java.util.TimeZone, railo.runtime.type.dt.DateTime)
	 */
	public int getDaysInMonth(TimeZone tz, railo.runtime.type.dt.DateTime date) {
		DateTime dt = new DateTime(date.getTime(),getDateTimeZone(tz));
		return daysInMonth(dt.getYear(), dt.getMonthOfYear());
	}

	/**
	 * @see railo.commons.date.DateTimeUtil#getDayOfYear(java.util.TimeZone, railo.runtime.type.dt.DateTime)
	 */
	public int getDayOfYear(TimeZone tz, railo.runtime.type.dt.DateTime dt) {
		return new DateTime(dt.getTime(),getDateTimeZone(tz)).getDayOfYear();
	}

	/**
	 * @see railo.commons.date.DateTimeUtil#getDayOfWeek(java.util.TimeZone, railo.runtime.type.dt.DateTime)
	 */
	public int getDayOfWeek(TimeZone tz, railo.runtime.type.dt.DateTime dt) {
		int dow=new DateTime(dt.getTime(),getDateTimeZone(tz)).getDayOfWeek()+1;
		if(dow==8) return 1;
		return dow;
	}

	/**
	 * @see railo.commons.date.DateTimeUtil#getMilliSecondsInDay(java.util.TimeZone, railo.runtime.type.dt.DateTime)
	 */
	public long getMilliSecondsInDay(TimeZone tz,long time) {
		return new DateTime(time,getDateTimeZone(tz)).getMillisOfDay();
	}

	private DateTimeZone getDateTimeZone(TimeZone tz) {
		DateTimeZone dtz=(DateTimeZone) zones.get(tz);
		if(dtz==null){
			dtz=DateTimeZone.forTimeZone(tz);
			zones.put(tz, dtz);
		}
		return dtz;
	}

	/**
	 * @see railo.commons.date.DateTimeUtil#getDiff(java.util.TimeZone, int, railo.runtime.type.dt.DateTime, railo.runtime.type.dt.DateTime)
	 */
	public long getDiff(TimeZone tz, int datePart,railo.runtime.type.dt.DateTime left,railo.runtime.type.dt.DateTime right) {
		return jreUtil.getDiff(tz, datePart, left, right);
	}

	/**
	 * @see railo.commons.date.DateTimeUtil#toString(railo.runtime.type.dt.DateTime, java.util.TimeZone)
	 */
	public String toString(railo.runtime.type.dt.DateTime date, TimeZone tz) {
		//return jreUtil.toString(date, tz);
		/*DateTime dt = new DateTime(date.getTime(),getDateTimeZone(tz));
		return "{ts '"+dt.getYear()+
    	"-"+dt.getMonthOfYear()+
    	"-"+dt.getDayOfMonth()+
    	" "+dt.getHourOfDay()+
    	":"+dt.getMinuteOfHour()+
    	":"+dt.getSecondOfMinute()+"'}";*/
		
		StringBuffer sb=new StringBuffer();
		DateTime dt = new DateTime(date.getTime(),getDateTimeZone(tz));
    	sb.append("{ts '");
    	jreUtil.toString(sb,dt.getYear(),4);
    	sb.append("-");
    	jreUtil.toString(sb,dt.getMonthOfYear(),2);
    	sb.append("-");
    	jreUtil.toString(sb,dt.getDayOfMonth(),2);
    	sb.append(" ");
    	jreUtil.toString(sb,dt.getHourOfDay(),2);
    	sb.append(":");
    	jreUtil.toString(sb,dt.getMinuteOfHour(),2);
    	sb.append(":");
    	jreUtil.toString(sb,dt.getSecondOfMinute(),2);
    	sb.append("'}");
    	 
    	return sb.toString();
	}
}
