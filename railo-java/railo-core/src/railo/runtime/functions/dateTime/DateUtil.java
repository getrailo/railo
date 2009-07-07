package railo.runtime.functions.dateTime;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpTable;
import railo.runtime.dump.SimpleDumpData;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.op.Decision;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.dt.DateTimeImpl;

/**
 * date utility functions
 */
public final class DateUtil {

    private static final double DAY_MILLIS = 86400000D;
	private static final long CF_UNIX_OFFSET = 2209161600000L;
	private static Calendar calendar;

    /* *
     * return minute of this date
     * @param date
     * @return minutes in hour
     * /
    public static int minute(Date date) {
		if (calendar == null)
	    	calendar=Calendar.getInstance();
	    synchronized (calendar) {
	    	calendar.clear();
	    	DateUtil.setTimeZone(null,calendar,date);      
    		calendar.setTime(date);         
			return calendar.get(Calendar.MINUTE);
	    }
	}*/
    
    /**
     * return minute of this date
     * @param date
     * @return minutes in hour
     */
    public static long millisInDay(Date date,TimeZone tz) {
    	return millisInDay(date.getTime(),ThreadLocalPageContext.getTimeZone(tz));
    }
    
    public static long millisMidnight(Date date,TimeZone tz) {
    	return date.getTime()-millisInDay(date,tz);
    }
    
    /**
     * return minute of this date
     * @param time
     * @return minutes in hour
     */
    private static long millisInDay(long time,TimeZone tz) {
        if (calendar == null)
            calendar=Calendar.getInstance();
        synchronized (calendar) {
            calendar.clear();
            calendar.setTimeZone(ThreadLocalPageContext.getTimeZone(tz));
            calendar.setTimeInMillis(time);         
            return  (calendar.get(Calendar.HOUR_OF_DAY)*3600000)+
                    (calendar.get(Calendar.MINUTE)*60000)+
                    (calendar.get(Calendar.SECOND)*1000)+
                    (calendar.get(Calendar.MILLISECOND));
        }
    }
    
    public static long millisMidnight(long time,TimeZone tz) {
        return time-millisInDay(time,tz);
    }

    /**
     * returns a calendar object matching given time
     * @param time
     * @return calendar
     */
    public static Calendar getCalendar(TimeZone tz,long time) {
    	tz=ThreadLocalPageContext.getTimeZone(tz);
        Calendar c=Calendar.getInstance();
        c.setTimeZone(tz);
        c.setTimeInMillis(time);
        return c;
    }
    
    /* *
     * translate current local Time to time matching given TimeZone
     * @param tz
     * @return time in given timezone
     * /
    public static long translateCurrentLocaleTime(TimeZone tz) {
        long current=System.currentTimeMillis();
        return (current-localeTimeZone.getOffset(current))+tz.getOffset(current);
    }*/
    
    /* *
     * return offset from local time to time of timezone defined in config plus Time server offset
     * @param config
     * @return offset from local to railo config time
     * /
    public static long getOffset(Config config) {
        long current=System.currentTimeMillis();
        return (((current-localeTimeZone.getOffset(current))+config.getTimeZone().getOffset(current))+config.getTimeServerOffset())-current;
    }*/
    
    /* *
     * translate given locale Time to time matching given TimeZone
     * @param localTime
     * @param tz
     * @return time in given timezone
     * /
    public static long translateLocaleTime(long localTime,TimeZone tz) {
    	int offset = tz!=null?tz.getOffset(localTime):0;
        return (localTime-localeTimeZone.getOffset(localTime))+offset;
    }*/
    
    /*public static long translateTime(long localTime,TimeZone src,TimeZone trg) {
        return (localTime-src.getOffset(localTime))+trg.getOffset(localTime);
    }*/
    
    
    /**
     * returns a date time instance by a number, the conversion from the double to 
     * date is o the base of the coldfusion rules.
     * @param days double value to convert to a number
     * @return DateTime Instance
     */
    public static DateTimeImpl getDateTimeInstance(double days) {	
    	long utc=(long)(days*DAY_MILLIS);
    	utc-=CF_UNIX_OFFSET;
    	utc-=getLocalTimeZoneOffset(utc);
    	return new DateTimeImpl(utc,false);
    	
    	
    	//long millis=-2209165200000L+(long)(86400000D*days);
    	//return new DateTimeImpl(millis,false);
    }
    
    /**
     * to double value
     * @return double value
     */
    public static double toDoubleValue(DateTime dateTime) {
    	long utc = dateTime.getTime();
    	utc+=getLocalTimeZoneOffset	(utc);
    	utc+=CF_UNIX_OFFSET;
    	return utc/DAY_MILLIS;
    }
    
    /*public static double toDoubleValueOld(DateTime dateTime) {
    	long utc = dateTime.getTime();
    	print.out(utc);
    	utc+=getLocalTimeZoneOffset	(utc);
    	print.out(getLocalTimeZoneOffset(utc));
    	print.out(utc);
    	return ((utc+2209165200000D))/
    	DAY_MILLIS;
    }*/
    
    public static long getLocalTimeZoneOffset(long utc){
    	return ThreadLocalPageContext.getTimeZone().getOffset(utc);
    }
    
    
    
    /**
     * cast boolean value
     * @param dateTime 
     * @return boolean value
     * @throws ExpressionException
     */
    public static boolean toBooleanValue(DateTime dateTime) throws ExpressionException {
        throw new ExpressionException("can't cast Date ["+dateTime.toGMTString()+"] to boolean value");
    }
    
    public static DumpData toDumpData(DateTime dateTime) {
        String str=dateTime.castToString("");
        
        
        DumpTable table=new DumpTable("#ffb200","#ffcc00","#263300");
        table.appendRow(1, new SimpleDumpData("Date Time"), new SimpleDumpData(str));
        return table;
    }
    
    /**
     * returns long from given date parts
     * @param year
     * @param month
     * @param day
     * @return
     */
    public static long toLong(TimeZone tz,int year, int month, int day, long defaultValue) {
    	return toLong(tz,year, month, day, 0, 0, 0, defaultValue);
    }
    
    /**
     * returns long from given date parts
     * @param year
     * @param month
     * @param day
     * @param hour
     * @param minute
     * @param second
     * @return
     */
    public static long toLong(TimeZone tz,int year, int month, int day, int hour, int minute, int second, long defaultValue) {
    	tz=ThreadLocalPageContext.getTimeZone(tz);
    	//print.out(year+":"+month+";"+day);
        if(year<100) {
            if(year<21)year=year+=2000;
            else year=year+=1900;
        }
        
        if(month>12) 	{
        	if(day<=12) {
        		int tmp = day;
        		day=month;
        		month=tmp;
        	}
        	else return defaultValue;
        }
        
        //print.out(year+":"+month+";"+day);
        
        if(month<1)		return defaultValue;
        if(month>12)		return defaultValue;
        if(day<1) 		return defaultValue;
        
        if(hour<0) 		return defaultValue;
        if(minute<0) 	return defaultValue;
        if(second<0) 	return defaultValue;
        
        if(hour>24) 	return defaultValue;
        if(minute>59) 	return defaultValue;
        if(second>59) 	return defaultValue;
        
        switch(month) {
        case 1:
        case 3:
        case 5:
        case 7:
        case 8:
        case 10:
        case 12:
        	if(day>31)return defaultValue;
        break;
        case 4:
        case 6:
        case 9:
        case 11:
            if(day>30)return defaultValue;
        break;
        case 2:
        	if(day>(Decision.isLeapYear(year)?29:28))return defaultValue;
        break;
        }
        
        if(month>(Decision.isLeapYear(year)?29:28)) return defaultValue;
        
        if (calendar == null) calendar=Calendar.getInstance();
        synchronized (calendar) {
        	calendar.setTimeZone(tz);
        	calendar.clear();
            calendar.set(year,month-1,day,hour,minute,second);
            return calendar.getTimeInMillis();
        }        
    }

    public static DateTime toDateTime(TimeZone tz,int year, int month, int day) throws ExpressionException {
		return toDateTime(tz,year, month, day, 0, 0, 0); 
	}

    public static DateTime toDateTime(TimeZone tz,int year, int month, int day, int hour, int minute, int second) throws ExpressionException {
    	DateTime dt = toDateTime(tz, year, month, day, hour, minute, second, null);
    	if(dt==null) throw new ExpressionException("can't cast input to a valid date","values have a invalid range");
		return dt;
	}	
    
    public static DateTime toDateTime(TimeZone tz,int year, int month, int day, int hour, int minute, int second,int millis) throws ExpressionException {
    	DateTime dt = toDateTime(tz, year, month, day, hour, minute, second, millis, null);
    	if(dt==null) throw new ExpressionException("can't cast input to a valid date","values have a invalid range");
		return dt;
	}	
    
    public static DateTime toDateTime(TimeZone tz,int year, int month, int day, DateTime defaultValue) {
		return toDateTime(tz,year, month, day, 0, 0, 0, defaultValue);
	}	

    public static DateTime toDateTime(TimeZone tz,int year, int month, int day, int hour, int minute, int second, DateTime defaultValue) {
		return toDateTime(tz, year, month, day, hour, minute, second, 0, defaultValue);
	}	

	public static DateTime toDateTime(TimeZone tz,int year, int month, int day, int hour, int minute, int second, int millis, DateTime defaultValue) {
		
		
		long time = toLong(tz,year, month, day, hour, minute, second, Long.MIN_VALUE);
		if(time==Long.MIN_VALUE) return defaultValue;
		return new DateTimeImpl(time+millis,false);
	}	


	public static void setTimeZone(Calendar calendar, Date date,TimeZone tz) {
    	calendar.setTimeZone(ThreadLocalPageContext.getTimeZone(tz));   
	}
	
	public static long fromSystemToRailo(long time) {
		int railoOffset = ThreadLocalPageContext.getTimeZone().getRawOffset();
		int systemOffset = TimeZone.getDefault().getRawOffset();
		return (time-railoOffset)+systemOffset;
	}
	public static long fromRailoToSystem(long time) {
		int railoOffset = ThreadLocalPageContext.getTimeZone().getRawOffset();
		int systemOffset = TimeZone.getDefault().getRawOffset();
		return (time+railoOffset)-systemOffset;
	}

	public static int getCurrentYear() {
		return Year.invoke(new DateTimeImpl(), null);
	}
	
	public static int daysInMonth(int year,int month){
		switch(month) {
	        case 1:
	        case 3:
	        case 5:
	        case 7:
	        case 8:
	        case 10:
	        case 12:
	        	return 31;
	        case 4:
	        case 6:
	        case 11:
	        	return 30;
	        case 2:
	        	return Decision.isLeapYear(year)?29:28;
        }
		return -1;
	}
	
}