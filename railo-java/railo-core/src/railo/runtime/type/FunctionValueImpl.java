package railo.runtime.type;

import java.util.Date;

import railo.runtime.PageContext;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.dump.DumpUtil;
import railo.runtime.dump.Dumpable;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.op.Operator;
import railo.runtime.op.date.DateCaster;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.util.ListUtil;

/**
 * represent a named function value for a functions
 */
public final class FunctionValueImpl implements FunctionValue,Dumpable {
	

	private final Collection.Key name;
	private final String[] names;
	private final Object value;

	/**
	 * @param name
	 * @param value
	 * @return
	 */
	public static FunctionValue newInstance(String name,Object value) {
		return new FunctionValueImpl(name,value);
	}
	
	public static FunctionValue newInstance(String[] name,Object value) {
		return new FunctionValueImpl(name,value);
	}

	public static FunctionValue newInstance(Collection.Key name,Object value) {
		return new FunctionValueImpl(name,value);
	}
	
	/**
	 * constructor of the class
	 * @param name name of the value
	 * @param value value himself
	 */
	public FunctionValueImpl(String name,Object value) {
        this.name=KeyImpl.init(name);
		this.value=value;
		names=null;
	} 

	public FunctionValueImpl(Collection.Key name,Object value) {
        this.name=name;
		this.value=value;
		names=null;
	} 
	
	public FunctionValueImpl(String[] names,Object value) {
        this.names=names;
		this.value=value;
		name=null;
	} 
	
	@Override
	public String getName() {
		return getNameAsString();
	}
	
	@Override
	public String getNameAsString() {
		if(name==null){
			return ListUtil.arrayToList(names, ".");
		}
		return name.getString();
	}
	
	@Override
	public Collection.Key getNameAsKey() {
		if(name==null){
			return KeyImpl.init(ListUtil.arrayToList(names, "."));
		}
		return name;
	}
	
	
	
	public String[] getNames() {
		return names;
	}
	
	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public String castToString() throws PageException {
		return Caster.toString(value);
	}

    @Override
    public String castToString(String defaultValue) {
        return Caster.toString(value,defaultValue);
    }
    
	@Override
	public boolean castToBooleanValue() throws PageException {
		return Caster.toBooleanValue(value);
	}
    
    @Override
    public Boolean castToBoolean(Boolean defaultValue) {
        return Caster.toBoolean(value,defaultValue);
    }
	
	@Override
	public double castToDoubleValue() throws PageException {
		return Caster.toDoubleValue(value);
	}
    
    @Override
    public double castToDoubleValue(double defaultValue) {
        return Caster.toDoubleValue(value,defaultValue);
    }
    
	@Override
	public DateTime castToDateTime() throws PageException {
		return DateCaster.toDateSimple(value,true,true,null);
	}
    
    @Override
    public DateTime castToDateTime(DateTime defaultValue) {
        return DateCaster.toDateSimple(value,true,true,null,defaultValue);
    }

	@Override
	public int compareTo(boolean b) throws PageException {
		return Operator.compare(value, b?1D:0D);
	}

	@Override
	public int compareTo(DateTime dt) throws PageException {
		return Operator.compare(value, (Date)dt);
	}

	@Override
	public int compareTo(double d) throws PageException {
		return Operator.compare(value, d);
	}

	@Override
	public int compareTo(String str) throws PageException {
		return Operator.compare(value, str);
	}

	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties properties) {
		return DumpUtil.toDumpData(value, pageContext, maxlevel, properties);
	}

	@Override
	public String toString() {
		return name+":"+value;
	}

	public static Struct toStruct(FunctionValueImpl fv1){
		StructImpl sct = new StructImpl(StructImpl.TYPE_LINKED);
		sct.setEL(fv1.getNameAsKey(), fv1);
		return sct;
	}
	public static Struct toStruct(FunctionValueImpl fv1,FunctionValueImpl fv2){
		StructImpl sct = new StructImpl(StructImpl.TYPE_LINKED);
		sct.setEL(fv1.getNameAsKey(), fv1);
		sct.setEL(fv2.getNameAsKey(), fv2);
		return sct;
	}
	public static Struct toStruct(FunctionValueImpl fv1,FunctionValueImpl fv2,FunctionValueImpl fv3){
		StructImpl sct = new StructImpl(StructImpl.TYPE_LINKED);
		sct.setEL(fv1.getNameAsKey(), fv1);
		sct.setEL(fv2.getNameAsKey(), fv2);
		sct.setEL(fv3.getNameAsKey(), fv3);
		return sct;
	}
	public static Struct toStruct(FunctionValueImpl fv1,FunctionValueImpl fv2,FunctionValueImpl fv3,FunctionValueImpl fv4){
		StructImpl sct = new StructImpl(StructImpl.TYPE_LINKED);
		sct.setEL(fv1.getNameAsKey(), fv1);
		sct.setEL(fv2.getNameAsKey(), fv2);
		sct.setEL(fv3.getNameAsKey(), fv3);
		sct.setEL(fv4.getNameAsKey(), fv4);
		return sct;
	}
	
}