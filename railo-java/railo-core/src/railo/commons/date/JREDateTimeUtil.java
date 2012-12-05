package railo.commons.date;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.op.Caster;
import railo.runtime.type.dt.DateTime;

public class JREDateTimeUtil extends DateTimeUtil {
	
	private static CalendarThreadLocal calendar=new CalendarThreadLocal();
	
	private Calendar year;
	private Calendar month;
	private Calendar day;
	private Calendar hour;
	private Calendar minute;
	private Calendar second;
	private Calendar milliSecond;
	//private Calendar dayOfYear;
	//private Calendar dayOfWeek;
	//private Calendar week;
	//private Calendar time;
	private Calendar milliSecondsInDay;
	private Calendar daysInMonth;
	Calendar string;

	//private static Map tzCalendars=new HashMap();
	private static Map<Locale,Calendar> localeCalendars=new HashMap<Locale,Calendar>();
	
	
	
	JREDateTimeUtil() {
		
	}

	 long _toTime(TimeZone tz, int year, int month, int day, int hour,int minute, int second, int milliSecond) {
		tz=ThreadLocalPageContext.getTimeZone(tz);
		Calendar time = getCalendar(tz);
		synchronized(time){
			time.set(year,month-1,day,hour,minute,second);
			time.set(Calendar.MILLISECOND,milliSecond);  
	        return time.getTimeInMillis();
		}
    	
	}

	private static int get(Calendar calendar,TimeZone tz, DateTime dt, int field) {
		//synchronized (calendar) {
			calendar.setTimeZone(tz); 
        	calendar.setTimeInMillis(dt.getTime());         
    		return calendar.get(field);
        //}
	}

	@Override
	public synchronized int getYear(TimeZone tz, DateTime dt) {
		if(year==null)year=newInstance();
		return get(year,tz,dt,Calendar.YEAR);
	}

	@Override
	public synchronized int getMonth(TimeZone tz, DateTime dt) {
		if(month==null)month=newInstance();
		return get(month,tz,dt,Calendar.MONTH)+1;
	}

	@Override
	public synchronized int getDay(TimeZone tz, DateTime dt) {
		if(day==null)day=newInstance();
		return get(day,tz,dt,Calendar.DAY_OF_MONTH);
	}

	@Override
	public synchronized int getHour(TimeZone tz, DateTime dt) {
		if(hour==null)hour=newInstance();
		return get(hour,tz,dt,Calendar.HOUR_OF_DAY);
	}

	@Override
	public synchronized int getMinute(TimeZone tz, DateTime dt) {
		if(minute==null)minute=newInstance();
		return get(minute,tz,dt,Calendar.MINUTE);
	}

	@Override
	public synchronized int getSecond(TimeZone tz, DateTime dt) {
		if(second==null)second=newInstance();
		return get(second,tz,dt,Calendar.SECOND);
	}

	@Override
	public synchronized int getMilliSecond(TimeZone tz, DateTime dt) {
		if(milliSecond==null)milliSecond=newInstance();
		return get(milliSecond,tz,dt,Calendar.MILLISECOND);
	}

	@Override
	public synchronized int getWeekOfYear(Locale locale,TimeZone tz, DateTime dt) {
		Calendar c=getCalendar(locale);
		synchronized (c) {
			c.setTimeZone(tz); 
        	c.setTimeInMillis(dt.getTime());         
    		int week=c.get(Calendar.WEEK_OF_YEAR);
			
			if(week==1 && c.get(Calendar.MONTH)==Calendar.DECEMBER) {
				if(isLeapYear(c.get(Calendar.YEAR)) && c.get(Calendar.DAY_OF_WEEK)==1){
					return 54;
				}
				return 53;
			}
			return week;
		}
	}

	@Override
	public synchronized int getDayOfYear(Locale locale,TimeZone tz, DateTime dt) {
		Calendar c=getCalendar(locale);
		synchronized (c) {
			return get(c,tz,dt,Calendar.DAY_OF_YEAR);
		}
	}

	@Override
	public synchronized int getDayOfWeek(Locale locale,TimeZone tz, DateTime dt) {
		Calendar c=getCalendar(locale);
		synchronized (c) {
			return get(c,tz,dt,Calendar.DAY_OF_WEEK);
		}
	}

	public synchronized long getMilliSecondsInDay(TimeZone tz,long time) {
		if(milliSecondsInDay==null)milliSecondsInDay=newInstance();
		milliSecondsInDay.clear();
		milliSecondsInDay.setTimeZone(ThreadLocalPageContext.getTimeZone(tz));
		milliSecondsInDay.setTimeInMillis(time);         
        return  (milliSecondsInDay.get(Calendar.HOUR_OF_DAY)*3600000)+
                (milliSecondsInDay.get(Calendar.MINUTE)*60000)+
                (milliSecondsInDay.get(Calendar.SECOND)*1000)+
                (milliSecondsInDay.get(Calendar.MILLISECOND));
    }

	public synchronized int getDaysInMonth(TimeZone tz, DateTime dt) {
		if(daysInMonth==null)daysInMonth=newInstance();
		daysInMonth.clear();
		daysInMonth.setTimeZone(tz); 
		daysInMonth.setTime(dt);         
		return daysInMonth(daysInMonth.get(Calendar.YEAR), daysInMonth.get(Calendar.MONTH)+1);
	}

	public long getDiff(TimeZone tz, int datePart, DateTime left, DateTime right) {
		
		return 0;
	}

	public synchronized String toString(DateTime dt, TimeZone tz) {
		if(string==null)string=newInstance();
		//synchronized (string) {
			tz=ThreadLocalPageContext.getTimeZone(tz);
			string.setTimeZone(tz);
			string.setTimeInMillis(dt.getTime());
			//"HH:mm:ss"
			StringBuilder sb=new StringBuilder();
    		
        	sb.append("{ts '");
        	toString(sb,string.get(Calendar.YEAR),4);
        	sb.append("-");
        	toString(sb,string.get(Calendar.MONTH)+1,2);
        	sb.append("-");
        	toString(sb,string.get(Calendar.DATE),2);
        	sb.append(" ");
        	toString(sb,string.get(Calendar.HOUR_OF_DAY),2);
        	sb.append(":");
        	toString(sb,string.get(Calendar.MINUTE),2);
        	sb.append(":");
        	toString(sb,string.get(Calendar.SECOND),2);
        	sb.append("'}");
        	 
        	return sb.toString();
        //}
	}


	public static Calendar newInstance() {
		return Calendar.getInstance(Locale.US);
	}
	
	public static Calendar newInstance(Locale l) {
		return Calendar.getInstance(l);
	}

	public static Calendar newInstance(TimeZone tz) {
		return Calendar.getInstance(tz,Locale.US);
	}

	public static Calendar getCalendar(Locale l) {
		Calendar c=localeCalendars.get(l);
		if(c==null){
			c=Calendar.getInstance(l);
			localeCalendars.put(l, c);
		}
		return c;
	}
	
	public static Calendar getCalendar(){
		return calendar.get();
	}
	
	public static Calendar getCalendar(TimeZone tz){
		Calendar c=getCalendar();
		c.setTimeZone(tz);
		return c;
	}
	
	
	/*public  static Calendar newInstance(Locale l) {
		Calendar c=Calendar.getInstance(l);
		return c;
	}*/

	void toString(StringBuilder sb,int i, int amount) {
		String str = Caster.toString(i);
		while(str.length()<(amount--)){
			sb.append( '0');
		}
		sb.append(str);
	}
}


class CalendarThreadLocal extends ThreadLocal<Calendar> {
	protected synchronized Calendar initialValue() {
        return Calendar.getInstance();
    }
}
