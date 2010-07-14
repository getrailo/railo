package railo.runtime.video;

import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;

public class Range {

	public static final Range TRUE=new Range(0,-1);
	public static final Range FALSE=new Range(0,0);
	private double from;
	private double to;

	public Range(double from, double to) {
		this.from=from;
		this.to=to;
	}


	public static Range toRange(String def) throws PageException {
		def=def.trim();
		// boolean
		if(Decision.isBoolean(def)) { 
			return Caster.toBooleanValue(def)?TRUE:FALSE;
		}
		
		int index = def.indexOf(',');
		// single value 
		if(index==-1) {
			return new Range(toSeconds(def),-1);
		}
		
		// double value
		if(def.startsWith(","))def="0"+def;
		if(def.endsWith(","))def+="-1";
		
		return new Range(toSeconds(def.substring(0,index)),toSeconds(def.substring(index+1)));
		
		
	}
	
	private static double toSeconds(String str) throws PageException {
		str=str.trim().toLowerCase();
		
		if(str.endsWith("ms"))	return Caster.toDoubleValue(str.substring(0,str.length()-2))/1000D;
		else if(str.endsWith("s"))		return Caster.toDoubleValue(str.substring(0,str.length()-1));
		else return Caster.toDoubleValue(str)/1000D;
		// TODO if(str.endsWith("f")) 			this.startFrame=VideoConfig.toLong(str.substring(0,str.length()-1));
		
	}

	/**
	 * @return the from
	 */
	public double getFrom() {
		return from;
	}
	
	public String getFromAsString() {
		return Caster.toString(from);
	}


	/**
	 * @return the to
	 */
	public double getTo() {
		return to;
	}
	
	public String getToAsString() {
		return Caster.toString(to);
	}


	/**
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if(obj==this) return true;
		if(!(obj instanceof Range)) return false;
		Range other=(Range) obj;
		return other.from==from && other.to==to;
	}

	/**
	 *
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return ""+from+":"+to+"";
	}


	public boolean show() {
		return !equals(Range.FALSE);
	}
}
