
package railo.runtime.type.dt;

import java.text.SimpleDateFormat;
import java.util.Locale;

import railo.commons.date.DateTimeUtil;
import railo.runtime.PageContext;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.dump.DumpTable;
import railo.runtime.dump.DumpTablePro;
import railo.runtime.dump.SimpleDumpData;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Operator;
import railo.runtime.type.SimpleValue;



/**
 * Printable and Castable Time Object (at the moment, same as DateTime)
 */
public final class TimeImpl extends Time implements SimpleValue,Localized {
    
	private static SimpleDateFormat railoFormatter=	new SimpleDateFormat("HH:mm:ss",Locale.US);

	//private TimeZone timezone;
	public TimeImpl(long utcTime) {
		this(null,utcTime,false); 
	}

	public TimeImpl(boolean addOffset) { 
		this(null,System.currentTimeMillis(),addOffset); 
	}
	
	public TimeImpl(long utcTime, boolean addOffset) {
		this(null,utcTime,addOffset); 
	}

	public TimeImpl(PageContext pc, boolean addOffset) { 
		this(pc,System.currentTimeMillis(),addOffset); 
	}

	public TimeImpl(PageContext pc, long utcTime, boolean addOffset) { 
		super(addOffset?DateTimeImpl.addOffset(ThreadLocalPageContext.getConfig(pc), utcTime):utcTime);  
	}
	
	public TimeImpl(java.util.Date date) {
		this(date.getTime(),false);
	}
	
	
    
    /* *
     * constructor of the class
     * @param hour
     * @param minute
     * @param second
     * /
    public TimeImpl(TimeZone tz, int hour, int minute, int second) {
		this(tz,hour,minute,second,0);
	}*/
    
    /* *
     * constructor of the class
     * @param hour
     * @param minute
     * @param second
     * @param millis 
     * /
    public TimeImpl(int hour, int minute, int second, int millis) {
    	this(null,DateUtil.toLong(tz,1899,12,30,hour,minute,second,0)+millis,tz);
	}*/
	

    /**
	 * @see railo.runtime.op.Castable#castToString()
	 */
	public String castToString() {
		synchronized (railoFormatter) {
        	railoFormatter.setTimeZone(ThreadLocalPageContext.getTimeZone());
            return "{t '"+railoFormatter.format(this)+"'}";
        }
	}

	/**
	 * @see railo.runtime.op.Castable#castToString(java.lang.String)
	 */
	public String castToString(String defaultValue) {
		synchronized (railoFormatter) {
        	railoFormatter.setTimeZone(ThreadLocalPageContext.getTimeZone());
            return "{t '"+railoFormatter.format(this)+"'}";
        }
	}


	/**
	 * @see railo.runtime.dump.Dumpable#toDumpData(railo.runtime.PageContext, int)
	 */
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
		String str=castToString("");
        DumpTable table=new DumpTablePro("date","#ffb200","#ffcc00","#263300");
        table.appendRow(1, new SimpleDumpData("Time"), new SimpleDumpData(str));
        return table;
    }
    
    /**
     * @see railo.runtime.op.Castable#castToBooleanValue()
     */
    public boolean castToBooleanValue() throws ExpressionException {
        return DateTimeUtil.getInstance().toBooleanValue(this);
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
        return DateTimeUtil.getInstance().toDoubleValue(this);
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
	public int compareTo(String str) throws PageException {
		return Operator.compare(castToString(), str);
	}
}