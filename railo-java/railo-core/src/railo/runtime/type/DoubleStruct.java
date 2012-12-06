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

public final class DoubleStruct extends StructImpl  {
	
	@Override
	public Boolean castToBoolean(Boolean defaultValue) {
		try {
			return Caster.toBoolean(castToBooleanValue());
		} catch (PageException e) {
			return defaultValue;
		}
	}

	@Override
	public DateTime castToDateTime(DateTime defaultValue) {
		try {
			return castToDateTime();
		} catch (PageException e) {
			return defaultValue;
		}
	}

	@Override
	public double castToDoubleValue(double defaultValue) {
		try {
			return castToDoubleValue();
		} catch (PageException e) {
			return defaultValue;
		}
	}

	@Override
	public String castToString(String defaultValue) {
		try {
			return castToString();
		} catch (PageException e) {
			return defaultValue;
		}
	}

	@Override
	public boolean castToBooleanValue() throws PageException {
		return Caster.toBooleanValue(castToDoubleValue());
		
	}

	@Override
	public DateTime castToDateTime() throws PageException {
		return Caster.toDate(castToDateTime(),null);
	}

	@Override
	public double castToDoubleValue() throws PageException {
		Iterator it = valueIterator();
		double value=0;
		while(it.hasNext()){
			value+=Caster.toDoubleValue(it.next());
		}
		return value;
	}

	@Override
	public String castToString() throws PageException {
		return Caster.toString(castToDoubleValue());
	}

	@Override
	public int compareTo(boolean b) throws PageException {
		return Operator.compare(castToDoubleValue(), b);
	}

	@Override
	public int compareTo(DateTime dt) throws PageException {
		return Operator.compare(castToDoubleValue(), dt);
	}

	@Override
	public int compareTo(double d) throws PageException {
		return Operator.compare(castToDoubleValue(),d);
	}

	@Override
	public int compareTo(String str) throws PageException {
		return Operator.compare(castToDoubleValue(), str);
	}

	@Override
	public Collection duplicate(boolean deepCopy) {
		Struct sct=new DoubleStruct();
		copy(this,sct,deepCopy);
		return sct;
	}

	@Override
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
		DumpTable table = (DumpTable) super.toDumpData(pageContext, maxlevel, dp);
		try{
			table.setTitle("Double Struct ("+castToString()+")");
		}
		catch(PageException pe){}
		return table;
	}
}
