
package railo.runtime.type.dt;

import java.text.SimpleDateFormat;
import java.util.Locale;

import railo.commons.date.DateTimeUtil;
import railo.runtime.PageContext;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.dump.DumpTable;
import railo.runtime.dump.SimpleDumpData;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Operator;
import railo.runtime.type.SimpleValue;



/**
 * Printable and Castable Time Object (at the moment, same as DateTime)
 */
public final class TimeImpl extends Time implements SimpleValue {
    
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
	

    @Override
	public String castToString() {
		synchronized (railoFormatter) {
        	railoFormatter.setTimeZone(ThreadLocalPageContext.getTimeZone());
            return "{t '"+railoFormatter.format(this)+"'}";
        }
	}

	@Override
	public String castToString(String defaultValue) {
		synchronized (railoFormatter) {
        	railoFormatter.setTimeZone(ThreadLocalPageContext.getTimeZone());
            return "{t '"+railoFormatter.format(this)+"'}";
        }
	}


	@Override
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
		String str=castToString("");
        DumpTable table=new DumpTable("date","#ff9900","#ffcc00","#000000");
        table.appendRow(1, new SimpleDumpData("Time"), new SimpleDumpData(str));
        return table;
    }
    
    @Override
    public boolean castToBooleanValue() throws ExpressionException {
        return DateTimeUtil.getInstance().toBooleanValue(this);
    }
    
    @Override
    public Boolean castToBoolean(Boolean defaultValue) {
        return defaultValue;
    }

    @Override
    public double castToDoubleValue() {
        return toDoubleValue();
    }
    
    @Override
    public double castToDoubleValue(double defaultValue) {
        return toDoubleValue();
    }

    @Override
    public DateTime castToDateTime() {
        return this;
    }
    
    @Override
    public DateTime castToDateTime(DateTime defaultValue) {
        return this;
    }
    
    @Override
    public double toDoubleValue() {
        return DateTimeUtil.getInstance().toDoubleValue(this);
    }


	@Override
	public int compareTo(boolean b) {
		return Operator.compare(castToDoubleValue(), b?1D:0D);
	}

	@Override
	public int compareTo(DateTime dt) throws PageException {
		return Operator.compare((java.util.Date)this, (java.util.Date)dt);
	}

	@Override
	public int compareTo(double d) throws PageException {
		return Operator.compare(castToDoubleValue(), d);
	}

	@Override
	public int compareTo(String str) throws PageException {
		return Operator.compare(castToString(), str);
	}
}