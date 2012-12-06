package railo.runtime.type;

import java.util.Date;

import railo.runtime.PageContext;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.dump.DumpTable;
import railo.runtime.dump.DumpUtil;
import railo.runtime.dump.SimpleDumpData;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.op.Duplicator;
import railo.runtime.op.Operator;
import railo.runtime.op.date.DateCaster;
import railo.runtime.type.dt.DateTime;

public final class CastableStruct extends StructImpl  {
	
	private Object value;

	public CastableStruct() { 
	}
	public CastableStruct(Object value) {
		this.value=value;
	}
	public CastableStruct(Object value, int type) {
		super(type);
		this.value=value;
	}

	/**
	 * @return the value
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(Object value) {
		this.value = value;
	}

	@Override
	public boolean castToBooleanValue() throws PageException {
		if(value==null) return super.castToBooleanValue();
		return Caster.toBooleanValue(value);
		
	}
    
    @Override
    public Boolean castToBoolean(Boolean defaultValue) {
    	if(value==null) return super.castToBoolean(defaultValue);
		return Caster.toBoolean(value,defaultValue);
    }

	@Override
	public DateTime castToDateTime() throws PageException {
		if(value==null) return super.castToDateTime();
		return Caster.toDate(value, null);
	}
    
    @Override
    public DateTime castToDateTime(DateTime defaultValue) {
    	if(value==null) return super.castToDateTime(defaultValue);
		return DateCaster.toDateAdvanced(value,true,null,defaultValue);
    }

	@Override
	public double castToDoubleValue() throws PageException {
		if(value==null) return super.castToDoubleValue();
		return Caster.toDoubleValue(value);
	}
    
    @Override
    public double castToDoubleValue(double defaultValue) {
    	if(value==null) return super.castToDoubleValue(defaultValue);
		return Caster.toDoubleValue(value,defaultValue);
    }

	@Override
	public String castToString() throws PageException {
		if(value==null) return super.castToString();
		return Caster.toString(value);
	}

	@Override
	public String castToString(String defaultValue) {
		if(value==null) return super.castToString(defaultValue);
		return Caster.toString(value,defaultValue);
	}

	@Override
	public int compareTo(boolean b) throws PageException {
		if(value==null) return super.compareTo(b);
		return Operator.compare(value, b);
	}

	@Override
	public int compareTo(DateTime dt) throws PageException {
		if(value==null) return super.compareTo(dt);
		return Operator.compare(value, (Date)dt);
	}

	@Override
	public int compareTo(double d) throws PageException {
		if(value==null) return super.compareTo(d);
		return Operator.compare(value,d);
	}

	@Override
	public int compareTo(String str) throws PageException {
		if(value==null) return super.compareTo(str);
		return Operator.compare(value, str);
	}

	@Override
	public Collection duplicate(boolean deepCopy) {
		if(value==null) return super.duplicate(deepCopy);
		Struct sct=new CastableStruct(deepCopy?Duplicator.duplicate(value,deepCopy):value);
		copy(this,sct,deepCopy);
		return sct;
	}

	@Override
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
		if(value==null) return super.toDumpData(pageContext, maxlevel,dp);
		DumpTable table = new DumpTable("struct","#9999ff","#ccccff","#000000");
		table.setTitle("Value Struct");
		maxlevel--;
		table.appendRow(1,new SimpleDumpData("value"),DumpUtil.toDumpData(value, pageContext,maxlevel,dp));
		table.appendRow(1,new SimpleDumpData("struct"),super.toDumpData(pageContext, maxlevel,dp));
		
		return table;
	}

	

}
