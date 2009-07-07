package railo.runtime.type;

import java.util.Iterator;

import railo.runtime.PageContext;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.dump.DumpTable;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.op.Operator;
import railo.runtime.type.dt.DateTime;

public class DoubleStruct extends StructImpl  {
	
	/**
	 * @see railo.runtime.type.util.StructSupport#castToBoolean(java.lang.Boolean)
	 */
	public Boolean castToBoolean(Boolean defaultValue) {
		try {
			return Caster.toBoolean(castToBooleanValue());
		} catch (PageException e) {
			return defaultValue;
		}
	}

	/**
	 * @see railo.runtime.type.util.StructSupport#castToDateTime(railo.runtime.type.dt.DateTime)
	 */
	public DateTime castToDateTime(DateTime defaultValue) {
		try {
			return castToDateTime();
		} catch (PageException e) {
			return defaultValue;
		}
	}

	/**
	 * @see railo.runtime.type.util.StructSupport#castToDoubleValue(double)
	 */
	public double castToDoubleValue(double defaultValue) {
		try {
			return castToDoubleValue();
		} catch (PageException e) {
			return defaultValue;
		}
	}

	/**
	 * @see railo.runtime.type.util.StructSupport#castToString(java.lang.String)
	 */
	public String castToString(String defaultValue) {
		try {
			return castToString();
		} catch (PageException e) {
			return defaultValue;
		}
	}

	/**
	 *
	 * @throws PageException 
	 * @see railo.runtime.type.StructImpl#castToBooleanValue()
	 */
	public boolean castToBooleanValue() throws PageException {
		return Caster.toBooleanValue(castToDoubleValue());
		
	}

	/**
	 *
	 * @throws PageException 
	 * @see railo.runtime.type.StructImpl#castToDateTime()
	 */
	public DateTime castToDateTime() throws PageException {
		return Caster.toDate(castToDateTime(),null);
	}

	/**
	 *
	 * @throws PageException 
	 * @see railo.runtime.type.StructImpl#castToDoubleValue()
	 */
	public double castToDoubleValue() throws PageException {
		Iterator it = valueIterator();
		double value=0;
		while(it.hasNext()){
			value+=Caster.toDoubleValue(it.next());
		}
		return value;
	}

	/**
	 *
	 * @throws PageException 
	 * @see railo.runtime.type.StructImpl#castToString()
	 */
	public String castToString() throws PageException {
		return Caster.toString(castToDoubleValue());
	}

	/**
	 *
	 * @throws PageException 
	 * @see railo.runtime.type.StructImpl#compareTo(boolean)
	 */
	public int compareTo(boolean b) throws PageException {
		return Operator.compare(castToDoubleValue(), b);
	}

	/**
	 *
	 * @see railo.runtime.type.StructImpl#compareTo(railo.runtime.type.dt.DateTime)
	 */
	public int compareTo(DateTime dt) throws PageException {
		return Operator.compare(castToDoubleValue(), dt);
	}

	/**
	 *
	 * @see railo.runtime.type.StructImpl#compareTo(double)
	 */
	public int compareTo(double d) throws PageException {
		return Operator.compare(castToDoubleValue(),d);
	}

	/**
	 *
	 * @see railo.runtime.type.StructImpl#compareTo(java.lang.String)
	 */
	public int compareTo(String str) throws PageException {
		return Operator.compare(castToDoubleValue(), str);
	}

	/**
	 *
	 * @see railo.runtime.type.StructImpl#duplicate(boolean)
	 */
	public Collection duplicate(boolean deepCopy) {
		Struct sct=new DoubleStruct();
		copy(this,sct,deepCopy);
		return sct;
	}

	/**
	 *
	 * @see railo.runtime.type.StructImpl#toDumpData(railo.runtime.PageContext, int)
	 */
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
		DumpTable table = (DumpTable) super.toDumpData(pageContext, maxlevel, dp);
		try{
			table.setTitle("Double Struct ("+castToString()+")");
		}
		catch(PageException pe){}
		return table;
	}
}
