package railo.runtime.engine;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.op.Caster;

public abstract class ExecutionLogSupport implements ExecutionLog {
	
	protected static final short UNIT_NANO=1;
	protected static final short UNIT_MICRO=2;
	protected static final short UNIT_MILLI=4;
	protected static final short UNIT_UNDEFINED=0;
	
	private Map<String,Pair> map=Collections.synchronizedMap(new HashMap<String,Pair>());
	protected long min=Long.MIN_VALUE;
	protected short unit=UNIT_UNDEFINED;
	
	public void init(PageContext pc,Map<String,String> arguments) {
		// min
		if(min==Long.MIN_VALUE) {
			min=toNanos(arguments.get("min-time"),0);
		}
		// unit
		if(UNIT_UNDEFINED==unit){
			unit=UNIT_NANO;
			// unit
			String _unit=arguments.get("unit");
			if(_unit!=null) {
				_unit=_unit.trim();
				if(_unit.equalsIgnoreCase("micro"))		unit=UNIT_MICRO;
				else if(_unit.equalsIgnoreCase("�s"))	unit=UNIT_MICRO;
				else if(_unit.equalsIgnoreCase("milli"))unit=UNIT_MILLI;
				else if(_unit.equalsIgnoreCase("ms"))	unit=UNIT_MILLI;
			}
		}
		_init(pc,arguments);
	}

	private static long toNanos(String str, int defaultValue) {
		if(StringUtil.isEmpty(str)) return defaultValue;
		str=str.trim().toLowerCase();
		long l = Caster.toLongValue(str,Long.MIN_VALUE);
		if(l!=Long.MIN_VALUE) return l;
		
		if(str.endsWith("ns")) {
			String sub=str.substring(0,str.length()-2);
			l = Caster.toLongValue(sub.trim(),Long.MIN_VALUE);
			if(l!=Long.MIN_VALUE) return l;
		}
		else if(str.endsWith("�s")) {
			String sub=str.substring(0,str.length()-2);
			double d = Caster.toDoubleValue(sub.trim(),Double.NaN);
			if(!Double.isNaN(d)) return (long)(d*1000);
		}
		else if(str.endsWith("ms")) {
			String sub=str.substring(0,str.length()-2);
			double d = Caster.toDoubleValue(sub.trim(),Double.NaN);
			if(!Double.isNaN(d)) return (long)(d*1000*1000);
		}
		else if(str.endsWith("s")) {
			String sub=str.substring(0,str.length()-1);
			double d = Caster.toDoubleValue(sub.trim(),Double.NaN);
			if(!Double.isNaN(d)) return (long)(d*1000*1000);
		}
		
		return defaultValue;
	}
	
	/*public static void main(String[] args) {
		print.e(toNanos("123456"));
		print.e(toNanos("123456ns"));
		print.e(toNanos("123.456�s"));
		print.e(toNanos("0.123456 ms"));
		print.e(toNanos("123456 ns"));
	}*/
	

	public final void release() {
		map.clear();
		_release();
	}
	
	public final void start(int pos,String id) {
		long current = System.nanoTime();
		map.put(id,new Pair(current,pos));
	}

	public final void end(int pos,String id) {
		long current = System.nanoTime();
		Pair pair=map.remove(id);
		if(pair!=null) {
			if((current-pair.time)>=min)
				_log(pair.pos,pos,pair.time,current);
		}
	}
	protected abstract void _init(PageContext pc, Map<String, String> arguments);
	protected abstract void _log(int startPos, int endPos, long startTime, long endTime);
	protected abstract void _release();


	protected String timeLongToString(long current) {
		if(unit==UNIT_MICRO) return (current/1000L)+" �s";
		if(unit==UNIT_MILLI) return (current/1000000L)+" ms";
		return current+" ns";
	}
	protected static String unitShortToString(short unit) {
		if(unit==UNIT_MICRO) return "�s";
		if(unit==UNIT_MILLI) return "ms";
		return "ns";
	}
	
	private final static class Pair {
		private final long time;
		private final int pos;
		public Pair(long time, int pos) {
			this.time=time;
			this.pos=pos;
		}
	}
}
