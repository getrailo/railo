package railo.runtime.type.trace;

import railo.commons.io.log.LogResource;
import railo.runtime.PageContext;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.exp.PageException;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.dt.DateTimeImpl;

public class TODateTime extends DateTime implements TraceObject {

	private DateTime dt;
	private String label;
	private LogResource log;

	public TODateTime(DateTime dt, String label,LogResource log){
		this.dt=dt;
		this.log=log;
		this.label=label;
	}
	
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties properties) {
		log(null);
		return dt.toDumpData(pageContext, maxlevel, properties);
	}

	public String castToString() throws PageException {
		log(null);
		return dt.castToString();
	}

	public String castToString(String defaultValue) {
		log(null);
		return dt.castToString(defaultValue);
	}

	public boolean castToBooleanValue() throws PageException {
		log(null);
		return dt.castToBooleanValue();
	}

	public Boolean castToBoolean(Boolean defaultValue) {
		log(null);
		return dt.castToBoolean(defaultValue);
	}

	public double castToDoubleValue() throws PageException {
		log(null);
		return dt.castToDoubleValue();
	}

	public double castToDoubleValue(double defaultValue) {
		log(null);
		return dt.castToDoubleValue(defaultValue);
	}

	public DateTime castToDateTime() throws PageException {
		log(null);
		return this;
	}

	public DateTime castToDateTime(DateTime defaultValue) {
		log(null);
		return this;
	}

	public int compareTo(String str) throws PageException {
		log(null);
		return dt.compareTo(str);
	}

	public int compareTo(boolean b) throws PageException {
		log(null);
		return dt.compareTo(b);
	}

	public int compareTo(double d) throws PageException {
		log(null);
		return dt.compareTo(d);
	}

	public int compareTo(DateTime dt) throws PageException {
		log(null);
		return dt.compareTo(dt);
	}

	public double toDoubleValue() {
		log(null);
		return this.dt.toDoubleValue();
	}
	

	protected void log(String addional) {
		TraceObjectSupport.log(log,label,addional);
	}
}
