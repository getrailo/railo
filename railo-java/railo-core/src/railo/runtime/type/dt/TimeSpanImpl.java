package railo.runtime.type.dt;

import railo.runtime.PageContext;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.dump.DumpTable;
import railo.runtime.dump.SimpleDumpData;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.op.Operator;

/**
 * TimeSpan Object, represent a timespan
 */
public final class TimeSpanImpl implements TimeSpan {

	private double value;
	private long valueMillis;
	
	private int day;
	private int hour;
	private int minute;
	private int second;
	private int milli;
	

	public static TimeSpan fromDays(double value){
		return new TimeSpanImpl(value);
	}
	public static TimeSpan fromMillis(long value){
		return new TimeSpanImpl(value);
	}

    private TimeSpanImpl(double valueDays) {
    	this((long)(valueDays*86400000D));
    }
	
    private TimeSpanImpl(long valueMillis) {
    	value=valueMillis/86400000D;
    	long tmp=valueMillis;
    	day=(int) (valueMillis/86400000L);
		tmp-=day*86400000;
		hour=(int) (tmp/3600000);
		tmp-=hour*3600000;
		minute=(int) (tmp/60000);
		tmp-=minute*60000;
		second=(int) (tmp/1000);
		tmp-=second*1000;
		milli=(int) tmp;
		
		this.valueMillis=valueMillis;
		/*day=(int)value;
		double diff=value-day;
		diff*=24;
		hour=(int)diff;
		diff=diff-hour;
		diff*=60;
		minute=(int)diff;
		diff=diff-minute;
		diff*=60;
		second=(int)diff;
		this.value=value;
		milli=(int)(valueMillis-((second+(minute*60L)+(hour*3600L)+(day*3600L*24L))*1000));
		*/
		//print.out("a("+hashCode()+"):"+day+":"+hour+":"+minute+":"+second+"+"+milli);
		
		
		//total=(second+(minute*60L)+(hour*3600L)+(day*3600L*24L))*1000;
		//total=(second+(minute*60L)+(hour*3600L)+(day*3600L*24L))*1000;
		
    }
		
	/**
	 * constructor of the timespan class
	 * @param day
	 * @param hour
	 * @param minute
	 * @param second
	 */
	public TimeSpanImpl(int day, int hour, int minute, int second) {
		
		this.day=day;
		this.hour=hour;
		this.minute=minute;
		this.second=second;
		value=day+(((double)hour)/24)+(((double)minute)/24/60)+(((double)second)/24/60/60);
		valueMillis=(second+(minute*60L)+(hour*3600L)+(day*3600L*24L))*1000;
	}
	
	/**
	 * constructor of the timespan class
	 * @param day
	 * @param hour
	 * @param minute
	 * @param second
	 */
	public TimeSpanImpl(int day, int hour, int minute, int second, int millisecond) {
		this.day=day;
		this.hour=hour;
		this.minute=minute;
		this.second=second;
		this.milli=millisecond;
		value=day+(((double)hour)/24)+(((double)minute)/24/60)+(((double)second)/24/60/60)+(((double)millisecond)/24/60/60/1000);
		valueMillis=((second+(minute*60L)+(hour*3600L)+(day*3600L*24L))*1000)+millisecond;
	}
	
    @Override
	public String castToString() {
		return Caster.toString(value);
	}

	@Override
	public String castToString(String defaultValue) {
		return Caster.toString(value);
	}

	@Override
	public boolean castToBooleanValue() throws ExpressionException {
		throw new ExpressionException("can't cast Timespan to boolean");
	}
    
    @Override
    public Boolean castToBoolean(Boolean defaultValue) {
        return defaultValue;
    }

	@Override
	public double castToDoubleValue() {
		return value;
	}
    
    @Override
    public double castToDoubleValue(double defaultValue) {
        return value;
    }

	@Override
	public DateTime castToDateTime() throws ExpressionException {
		throw new ExpressionException("can't cast Timespan to date");
	}
    
    @Override
    public DateTime castToDateTime(DateTime defaultValue) {
        return defaultValue;
    }


	@Override
	public int compareTo(boolean b) {
		return Operator.compare(value, b?1D:0D);
	}

	@Override
	public int compareTo(DateTime dt) throws PageException {
		return Operator.compare(value, dt.castToDoubleValue());
	}

	@Override
	public int compareTo(double d) throws PageException {
		return Operator.compare(value, d);
	}

	@Override
	public int compareTo(String str) throws PageException {
		return Operator.compare(value, str);
	}

	@Override
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
		DumpTable table=new DumpTable("timespan","#ff9900","#ffcc00","#000000");
		if(milli>0)table.appendRow(1, new SimpleDumpData("Timespan"), new SimpleDumpData("createTimeSpan("+day+","+hour+","+minute+","+second+","+milli+")"));
		else table.appendRow(1, new SimpleDumpData("Timespan"), new SimpleDumpData("createTimeSpan("+day+","+hour+","+minute+","+second+")"));
		
		
		
		return table;
	}
	@Override
	public long getMillis() {
		return valueMillis;
	}
	public long getMilli() {
		return milli;
	}
	
	@Override
	public long getSeconds() {
		return valueMillis/1000;
	}
	
	@Override
	public String toString() {
		if(milli>0)
			return "createTimeSpan("+day+","+hour+","+minute+","+second+","+milli+")";
		return "createTimeSpan("+day+","+hour+","+minute+","+second+")";
	}

    @Override
    public int getDay() {
        return day;
    }
    @Override
    public int getHour() {
        return hour;
    }
    @Override
    public int getMinute() {
        return minute;
    }
    @Override
    public int getSecond() {
        return second;
    }

	@Override
	public boolean equals(Object obj) {
		if(this==obj) return true;
		if(!(obj instanceof TimeSpan)) return false;
		return getMillis()==((TimeSpan)obj).getMillis();
	}
}