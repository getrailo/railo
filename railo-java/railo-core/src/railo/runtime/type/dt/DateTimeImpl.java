package railo.runtime.type.dt;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import railo.runtime.PageContext;
import railo.runtime.config.Config;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.dateTime.DateUtil;
import railo.runtime.op.Operator;
import railo.runtime.type.SimpleValue;

/**
 * Printable and Castable DateTime Object
 */
public final class DateTimeImpl extends DateTime implements SimpleValue,Localized {
	private static SimpleDateFormat railoFormatter=	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.US);
	//public static SimpleDateFormat javaFormatter=	new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy",Locale.US);

	//private TimeZone timezone;


	public DateTimeImpl(PageContext pc) {
		this(pc,System.currentTimeMillis(),true);
	}
	
	public DateTimeImpl(Config config) {
		this(config,System.currentTimeMillis());
	}
	
	public DateTimeImpl() {
		this(System.currentTimeMillis(),true);
	}
	
	public DateTimeImpl(PageContext pc, long utcTime, boolean doOffset) {
		super(doOffset?addOffset(ThreadLocalPageContext.getConfig(pc), utcTime):utcTime);
		//this.timezone=ThreadLocalPageContext.getTimeZone(pc);
	}
	
	public DateTimeImpl(Config config, long utcTime) {
		super(addOffset(ThreadLocalPageContext.getConfig(config),utcTime));
		//this.timezone=ThreadLocalPageContext.getTimeZone(config);
	}

	public DateTimeImpl(Date date) {
		this(date.getTime(),false);
	}
	
	public DateTimeImpl(long utcTime, boolean doOffset) {
		super(doOffset?addOffset(ThreadLocalPageContext.getConfig(), utcTime):utcTime);
	}
	
	public DateTimeImpl(Calendar calendar) {
		super(calendar.getTimeInMillis());
		//this.timezone=ThreadLocalPageContext.getTimeZone(calendar.getTimeZone());
	}

	public static long addOffset(Config config, long utcTime) {
		if(config!=null) return utcTime+config.getTimeServerOffset();
		return utcTime;
	}
	
	
	/**
	 * @see railo.runtime.dump.Dumpable#toDumpData(railo.runtime.PageContext, int)
	 */
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
		return DateUtil.toDumpData(this);
	}

	/**
	 * @see railo.runtime.op.Castable#castToString()
	 */
	public String castToString() {
		return castToString((TimeZone)null);
    }


    /**
     * @see railo.runtime.op.Castable#castToString(java.lang.String)
     */
    public String castToString(String defaultValue) {
        return castToString((TimeZone)null);
    }
    
	public String castToString(TimeZone tz) {
		synchronized (railoFormatter) {
        	railoFormatter.setTimeZone(ThreadLocalPageContext.getTimeZone(tz));
            return "{ts '"+railoFormatter.format(this)+"'}";
        }
	}
	
	/**
	 * @see railo.runtime.op.Castable#castToBooleanValue()
	 */
	public boolean castToBooleanValue() throws ExpressionException {
        return DateUtil.toBooleanValue(this);
	}
    
    /**
     * @see railo.runtime.op.Castable#castToBoolean(java.lang.Boolean)
     */
    public Boolean castToBoolean(Boolean defaultValue) {
        return defaultValue;
    }

	/**
	 * @see railo.runtime.op.Castable#castToDoubleValue()
	 */
	public double castToDoubleValue() {
	    return toDoubleValue();
	}
    
    /**
     * @see railo.runtime.op.Castable#castToDoubleValue(double)
     */
    public double castToDoubleValue(double defaultValue) {
        return toDoubleValue();
    }

	/**
	 * @see railo.runtime.op.Castable#castToDateTime()
	 */
	public DateTime castToDateTime() {
		return this;
	}
    
    /**
     * @see railo.runtime.op.Castable#castToDateTime(railo.runtime.type.dt.DateTime)
     */
    public DateTime castToDateTime(DateTime defaultValue) {
        return this;
    }
	
	/**
     * @see railo.runtime.type.dt.DateTime#toDoubleValue()
     */
	public double toDoubleValue() {
	    return DateUtil.toDoubleValue(this);
	}


	/**
	 * @see railo.runtime.op.Castable#compare(boolean)
	 */
	public int compareTo(boolean b) {
		return Operator.compare(castToDoubleValue(), b?1D:0D);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(railo.runtime.type.dt.DateTime)
	 */
	public int compareTo(DateTime dt) throws PageException {
		return Operator.compare((java.util.Date)this, (java.util.Date)dt);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(double)
	 */
	public int compareTo(double d) throws PageException {
		return Operator.compare(castToDoubleValue(), d);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(java.lang.String)
	 */
	public int compareTo(String str) {
		return Operator.compare(castToString(), str);
	}

	/* *
	 * FUTURE add to interface
	 * @return the timezone
	 * /
	public TimeZone getTimezone() {
		return timezone;
	}*/

	/* *
	 * FUTURE add to interface
	 * @param timezone the timezone to set
	 * /
	public void setTimezone(TimeZone timezone) {
		this.timezone = timezone;
	}*/
	
	public String toString() {
		return castToString();
        /*synchronized (javaFormatter) {
        	javaFormatter.setTimeZone(timezone);
            return javaFormatter.format(this);
        }*/
	}
	
	
}