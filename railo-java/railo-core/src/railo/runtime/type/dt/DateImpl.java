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
import railo.runtime.exp.PageException;
import railo.runtime.op.Operator;
import railo.runtime.type.SimpleValue;


/**
 * Printable and Castable Date Object (no visible time)
 */
public final class DateImpl extends  Date implements SimpleValue {
	
	private static SimpleDateFormat railoFormatter=	new SimpleDateFormat("yyyy-MM-dd",Locale.US);
	
	//private TimeZone timezone;
	

	public DateImpl() {
		this(null,System.currentTimeMillis());
	}
	public DateImpl(long utcTime) {
		this(null,utcTime);
	}

	public DateImpl(PageContext pc) {
		this(pc,System.currentTimeMillis());
	}
	public DateImpl(PageContext pc, long utcTime) {
		super(DateTimeImpl.addOffset(ThreadLocalPageContext.getConfig(pc), utcTime));
		//this.timezone=ThreadLocalPageContext.getTimeZone(pc);
	}
	
	public DateImpl(java.util.Date date) {
		super(date.getTime());
		/*if(date instanceof Localized) {
			Localized l=(Localized) date;
			this.timezone=l.getTimezone();
		}
		else timezone=ThreadLocalPageContext.getTimeZone();*/
	}

	@Override
	public String castToString() {
		synchronized (railoFormatter) {
        	railoFormatter.setTimeZone(ThreadLocalPageContext.getTimeZone());
            return "{d '"+railoFormatter.format(this)+"'}";
        }
	}

    @Override
    public String castToString(String defaultValue) {
        return castToString();
    }

    @Override
    public double toDoubleValue() {
        return DateTimeUtil.getInstance().toDoubleValue(this);
    }
    
    @Override
    public double castToDoubleValue(double defaultValue) {
        return DateTimeUtil.getInstance().toDoubleValue(this);
    }

    @Override
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
		String str=castToString("");
        DumpTable table=new DumpTable("date","#ff9900","#ffcc00","#000000");
        table.appendRow(1, new SimpleDumpData("Date"), new SimpleDumpData(str));
        return table;
    }

    @Override
    public boolean castToBooleanValue() throws PageException {
        return DateTimeUtil.getInstance().toBooleanValue(this);
    }
    
    @Override
    public Boolean castToBoolean(Boolean defaultValue) {
        return defaultValue;
    }

    @Override
    public double castToDoubleValue() {
        return DateTimeUtil.getInstance().toDoubleValue(this);
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
	public int compareTo(String str) {
		return Operator.compare(castToString(), str);
	}
}