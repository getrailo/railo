package railo.runtime.type.trace;

import railo.runtime.PageContext;
import railo.runtime.debug.Debugger;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.exp.PageException;
import railo.runtime.type.Query;
import railo.runtime.type.QueryImpl;
import railo.runtime.type.dt.DateTime;

public class TODateTime extends DateTime implements TraceObject {

	private DateTime dt;
	//private Debugger debugger;
	private Query qry=new QueryImpl(
            new String[]{"label","action","params","template","line","time"},
            0,"traceObjects");
	private int type;
	private String category;
	private String text;
	private Debugger debugger;

	

	public TODateTime(Debugger debugger,DateTime dt, int type, String category, String text){
		this.dt=dt;
		this.debugger=debugger;
		this.type=type;
		this.category=category;
		this.text=text;
	}
	
	@Override
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties properties) {
		log();
		return dt.toDumpData(pageContext, maxlevel, properties);
	}

	@Override
	public String castToString() throws PageException {
		log();
		return dt.castToString();
	}

	@Override
	public String castToString(String defaultValue) {
		log();
		return dt.castToString(defaultValue);
	}

	@Override
	public boolean castToBooleanValue() throws PageException {
		log();
		return dt.castToBooleanValue();
	}

	@Override
	public Boolean castToBoolean(Boolean defaultValue) {
		log();
		return dt.castToBoolean(defaultValue);
	}

	@Override
	public double castToDoubleValue() throws PageException {
		log();
		return dt.castToDoubleValue();
	}

	@Override
	public double castToDoubleValue(double defaultValue) {
		log();
		return dt.castToDoubleValue(defaultValue);
	}

	@Override
	public DateTime castToDateTime() throws PageException {
		log();
		return this;
	}

	@Override
	public DateTime castToDateTime(DateTime defaultValue) {
		log();
		return this;
	}

	@Override
	public int compareTo(String str) throws PageException {
		log();
		return dt.compareTo(str);
	}

	@Override
	public int compareTo(boolean b) throws PageException {
		log();
		return dt.compareTo(b);
	}

	@Override
	public int compareTo(double d) throws PageException {
		log();
		return dt.compareTo(d);
	}

	@Override
	public int compareTo(DateTime dt) throws PageException {
		log();
		return dt.compareTo(dt);
	}

	@Override
	public double toDoubleValue() {
		log();
		return this.dt.toDoubleValue();
	}
	

	protected void log() {
		TraceObjectSupport.log(debugger,type,category,text,null,null);
	}

	public Query getDebugData() {
		return qry;
	}
}
