package railo.runtime.type;

import java.util.Date;

import railo.runtime.PageContext;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.dump.DumpTable;
import railo.runtime.dump.DumpTablePro;
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

	/**
	 *
	 * @throws PageException 
	 * @see railo.runtime.type.StructImpl#castToBooleanValue()
	 */
	public boolean castToBooleanValue() throws PageException {
		if(value==null) return super.castToBooleanValue();
		return Caster.toBooleanValue(value);
		
	}
    
    /**
     * @see railo.runtime.op.Castable#castToBoolean(java.lang.Boolean)
     */
    public Boolean castToBoolean(Boolean defaultValue) {
    	if(value==null) return super.castToBoolean(defaultValue);
		return Caster.toBoolean(value,defaultValue);
    }

	/**
	 *
	 * @throws PageException 
	 * @see railo.runtime.type.StructImpl#castToDateTime()
	 */
	public DateTime castToDateTime() throws PageException {
		if(value==null) return super.castToDateTime();
		return Caster.toDate(value, null);
	}
    
    /**
     * @see railo.runtime.op.Castable#castToDateTime(railo.runtime.type.dt.DateTime)
     */
    public DateTime castToDateTime(DateTime defaultValue) {
    	if(value==null) return super.castToDateTime(defaultValue);
		return DateCaster.toDateAdvanced(value,true,null,defaultValue);
    }

	/**
	 *
	 * @throws PageException 
	 * @see railo.runtime.type.StructImpl#castToDoubleValue()
	 */
	public double castToDoubleValue() throws PageException {
		if(value==null) return super.castToDoubleValue();
		return Caster.toDoubleValue(value);
	}
    
    /**
     * @see railo.runtime.op.Castable#castToDoubleValue(double)
     */
    public double castToDoubleValue(double defaultValue) {
    	if(value==null) return super.castToDoubleValue(defaultValue);
		return Caster.toDoubleValue(value,defaultValue);
    }

	/**
	 *
	 * @throws PageException 
	 * @see railo.runtime.type.StructImpl#castToString()
	 */
	public String castToString() throws PageException {
		if(value==null) return super.castToString();
		return Caster.toString(value);
	}

	/**
	 * @see railo.runtime.type.util.StructSupport#castToString(java.lang.String)
	 */
	public String castToString(String defaultValue) {
		if(value==null) return super.castToString(defaultValue);
		return Caster.toString(value,defaultValue);
	}

	/**
	 *
	 * @throws PageException 
	 * @see railo.runtime.type.StructImpl#compareTo(boolean)
	 */
	public int compareTo(boolean b) throws PageException {
		if(value==null) return super.compareTo(b);
		return Operator.compare(value, b);
	}

	/**
	 *
	 * @see railo.runtime.type.StructImpl#compareTo(railo.runtime.type.dt.DateTime)
	 */
	public int compareTo(DateTime dt) throws PageException {
		if(value==null) return super.compareTo(dt);
		return Operator.compare(value, (Date)dt);
	}

	/**
	 *
	 * @see railo.runtime.type.StructImpl#compareTo(double)
	 */
	public int compareTo(double d) throws PageException {
		if(value==null) return super.compareTo(d);
		return Operator.compare(value,d);
	}

	/**
	 *
	 * @see railo.runtime.type.StructImpl#compareTo(java.lang.String)
	 */
	public int compareTo(String str) throws PageException {
		if(value==null) return super.compareTo(str);
		return Operator.compare(value, str);
	}

	/**
	 *
	 * @see railo.runtime.type.StructImpl#duplicate(boolean)
	 */
	public Collection duplicate(boolean deepCopy) {
		if(value==null) return super.duplicate(deepCopy);
		Struct sct=new CastableStruct(deepCopy?Duplicator.duplicate(value,deepCopy):value);
		copy(this,sct,deepCopy);
		return sct;
	}

	/**
	 *
	 * @see railo.runtime.type.StructImpl#toDumpData(railo.runtime.PageContext, int)
	 */
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
		if(value==null) return super.toDumpData(pageContext, maxlevel,dp);
		DumpTable table = new DumpTablePro("struct","#9999ff","#ccccff","#000000");
		table.setTitle("Value Struct");
		maxlevel--;
		table.appendRow(1,new SimpleDumpData("value"),DumpUtil.toDumpData(value, pageContext,maxlevel,dp));
		table.appendRow(1,new SimpleDumpData("struct"),super.toDumpData(pageContext, maxlevel,dp));
		
		return table;
	}

	

}
