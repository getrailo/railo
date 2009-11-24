package railo.commons.date;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.op.Caster;
import railo.runtime.type.dt.DateTime;

public class JREDateTimeUtil extends DateTimeUtil {

	
	private Calendar year;
	private Calendar month;
	private Calendar day;
	private Calendar hour;
	private Calendar minute;
	private Calendar second;
	private Calendar milliSecond;
	private Calendar dayOfYear;
	private Calendar dayOfWeek;
	private Calendar week;
	//private Calendar time;
	private Calendar milliSecondsInDay;
	private Calendar daysInMonth;
	
	private static Map tzCalendars=new HashMap();

	JREDateTimeUtil() {
		
	}

	 long _toTime(TimeZone tz, int year, int month, int day, int hour,int minute, int second, int milliSecond) {
		tz=ThreadLocalPageContext.getTimeZone(tz);
		Calendar time = getCalendar(tz);
		synchronized(time){
			//time = Calendar.getInstance();
			//time.setTimeZone(tz);
			//time.set(Calendar.MONTH, month-1);
			time.set(year,month-1,day,hour,minute,second);
			time.set(Calendar.MILLISECOND,milliSecond);  
	        return time.getTimeInMillis();
		}
    	
	}

	private static int get(Calendar calendar,TimeZone tz, DateTime dt, int field) {
		synchronized (calendar) {
			//calendar.clear();
        	calendar.setTimeZone(tz); 
        	calendar.setTimeInMillis(dt.getTime());         
    		return calendar.get(field);
        }
	}

	/**
	 * @see railo.commons.date.DateTimeUtil#getYear(java.util.TimeZone, railo.runtime.type.dt.DateTime)
	 */
	public  int getYear(TimeZone tz, DateTime dt) {
		if(year==null)year=Calendar.getInstance();
		return get(year,tz,dt,Calendar.YEAR);
		
	}

	/**
	 * @see railo.commons.date.DateTimeUtil#getMonth(java.util.TimeZone, railo.runtime.type.dt.DateTime)
	 */
	public int getMonth(TimeZone tz, DateTime dt) {
		if(month==null)month=Calendar.getInstance();
		return get(month,tz,dt,Calendar.MONTH)+1;
	}

	/**
	 * @see railo.commons.date.DateTimeUtil#getWeek(java.util.TimeZone, railo.runtime.type.dt.DateTime)
	 */
	public int getWeek(TimeZone tz, DateTime dt) {
		if(week==null)week=Calendar.getInstance();
		return get(week,tz,dt,Calendar.WEEK_OF_YEAR);
	}

	/**
	 * @see railo.commons.date.DateTimeUtil#getDay(java.util.TimeZone, railo.runtime.type.dt.DateTime)
	 */
	public int getDay(TimeZone tz, DateTime dt) {
		if(day==null)day=Calendar.getInstance();
		return get(day,tz,dt,Calendar.DAY_OF_MONTH);
	}

	/**
	 * @see railo.commons.date.DateTimeUtil#getHour(java.util.TimeZone, railo.runtime.type.dt.DateTime)
	 */
	public int getHour(TimeZone tz, DateTime dt) {
		if(hour==null)hour=Calendar.getInstance();
		return get(hour,tz,dt,Calendar.HOUR_OF_DAY);
	}

	/**
	 * @see railo.commons.date.DateTimeUtil#getMinute(java.util.TimeZone, railo.runtime.type.dt.DateTime)
	 */
	public int getMinute(TimeZone tz, DateTime dt) {
		if(minute==null)minute=Calendar.getInstance();
		return get(minute,tz,dt,Calendar.MINUTE);
	}

	/**
	 * @see railo.commons.date.DateTimeUtil#getSecond(java.util.TimeZone, railo.runtime.type.dt.DateTime)
	 */
	public int getSecond(TimeZone tz, DateTime dt) {
		if(second==null)second=Calendar.getInstance();
		return get(second,tz,dt,Calendar.SECOND);
	}

	/**
	 * @see railo.commons.date.DateTimeUtil#getMilliSecond(java.util.TimeZone, railo.runtime.type.dt.DateTime)
	 */
	public int getMilliSecond(TimeZone tz, DateTime dt) {
		if(milliSecond==null)milliSecond=Calendar.getInstance();
		return get(milliSecond,tz,dt,Calendar.MILLISECOND);
	}

	/**
	 * @see railo.commons.date.DateTimeUtil#getDayOfYear(java.util.TimeZone, railo.runtime.type.dt.DateTime)
	 */
	public int getDayOfYear(TimeZone tz, DateTime dt) {
		if(dayOfYear==null)dayOfYear=Calendar.getInstance();
		return get(dayOfYear,tz,dt,Calendar.DAY_OF_YEAR);
	}

	/**
	 * @see railo.commons.date.DateTimeUtil#getDayOfYear(java.util.TimeZone, railo.runtime.type.dt.DateTime)
	 */
	public int getDayOfWeek(TimeZone tz, DateTime dt) {
		if(dayOfWeek==null)dayOfWeek=Calendar.getInstance();
		return get(dayOfWeek,tz,dt,Calendar.DAY_OF_WEEK);
	}

	public long getMilliSecondsInDay(TimeZone tz,long time) {
		if(milliSecondsInDay==null)milliSecondsInDay=Calendar.getInstance();
		synchronized (milliSecondsInDay) {
			milliSecondsInDay.clear();
			milliSecondsInDay.setTimeZone(ThreadLocalPageContext.getTimeZone(tz));
			milliSecondsInDay.setTimeInMillis(time);         
            return  (milliSecondsInDay.get(Calendar.HOUR_OF_DAY)*3600000)+
                    (milliSecondsInDay.get(Calendar.MINUTE)*60000)+
                    (milliSecondsInDay.get(Calendar.SECOND)*1000)+
                    (milliSecondsInDay.get(Calendar.MILLISECOND));
        }
    }

	public int getDaysInMonth(TimeZone tz, DateTime dt) {
		if(daysInMonth==null)daysInMonth=Calendar.getInstance(); 
		synchronized (daysInMonth) {
			daysInMonth.clear();
			daysInMonth.setTimeZone(tz); 
			daysInMonth.setTime(dt);         
    		return daysInMonth(daysInMonth.get(Calendar.YEAR), daysInMonth.get(Calendar.MONTH)+1);
        }
	}

	public long getDiff(TimeZone tz, int datePart, DateTime left, DateTime right) {
		
		return 0;
	}

	public String toString(DateTime dt, TimeZone tz) {
		tz=ThreadLocalPageContext.getTimeZone(tz);
		Calendar c=getCalendar(tz);
		synchronized (c) {
        	c.setTimeInMillis(dt.getTime());
			//"HH:mm:ss"
        	StringBuffer sb=new StringBuffer();
    		
        	sb.append("{ts '");
        	toString(sb,c.get(Calendar.YEAR),4);
        	sb.append("-");
        	toString(sb,c.get(Calendar.MONTH)+1,2);
        	sb.append("-");
        	toString(sb,c.get(Calendar.DATE),2);
        	sb.append(" ");
        	toString(sb,c.get(Calendar.HOUR_OF_DAY),2);
        	sb.append(":");
        	toString(sb,c.get(Calendar.MINUTE),2);
        	sb.append(":");
        	toString(sb,c.get(Calendar.SECOND),2);
        	sb.append("'}");
        	 
        	return sb.toString();
        }
	}

	private Calendar getCalendar(TimeZone tz) {
		Calendar c=(Calendar) tzCalendars.get(tz.getID());
		if(c==null){
			c=Calendar.getInstance(tz);
			tzCalendars.put(tz.getID(), c);
		}
		return c;
	}

	void toString(StringBuffer sb,int i, int amount) {
		String str = Caster.toString(i);
		while(str.length()<(amount--)){
			sb.append( '0');
		}
		sb.append(str);
	}

}
