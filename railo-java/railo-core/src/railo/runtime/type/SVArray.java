package railo.runtime.type;

import java.util.Date;

import railo.runtime.PageContext;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.dump.DumpTable;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.op.Operator;
import railo.runtime.op.date.DateCaster;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.ref.Reference;

/**
 * Simple Value Array, a Array that can't cast to a Simple Value
 */
public final class SVArray extends ArrayImpl implements Reference {
    
    private int position=1;

    /**
     * Constructor of the class
     */
    public SVArray() {
        super();
    }

    /**
     * Constructor of the class
     * @param dimension
     * @throws ExpressionException
     */
    public SVArray(int dimension) throws ExpressionException {
        super(dimension);
    }

    /**
     * Constructor of the class
     * @param objects
     */
    public SVArray(Object[] objects) {
        super(objects);
    }

    /**
     * @return Returns the position.
     */
    public int getPosition() {
        return position;
    }

    /**
     * @param position The position to set.
     */
    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public Collection.Key getKey() {
        return KeyImpl.init(Caster.toString(position));
    }

    @Override
    public String getKeyAsString() {
        return Caster.toString(position);
    }

    @Override
    public Object get(PageContext pc) throws PageException {
        return getE(position);
    }

    @Override
    public Object get(PageContext pc, Object defaultValue) {
        return get(position,defaultValue);
    }

    @Override
    public Object touch(PageContext pc) throws PageException {
        Object o=get(position,null);
        if(o!=null) return o;
        return setE(position,new StructImpl());
    }
    
    @Override
    public Object touchEL(PageContext pc) {
        Object o=get(position,null);
        if(o!=null) return o;
        return setEL(position,new StructImpl());
    }

    @Override
    public Object set(PageContext pc, Object value) throws PageException {
        return setE(position,value);
    }
    
    @Override
    public Object setEL(PageContext pc, Object value) {
        return setEL(position,value);
    }

    @Override
    public Object remove(PageContext pc) throws PageException {
        return removeE(position);
    }
    
    @Override
    public Object removeEL(PageContext pc) {
        return removeEL(position);
    }

    @Override
    public Object getParent() {
        return this;
    }

    @Override
    public String castToString() throws PageException {
        return Caster.toString(getE(position));
    }
    
	@Override
	public String castToString(String defaultValue) {
		Object value = get(position,null);
		if(value==null) return defaultValue;
		return Caster.toString(value,defaultValue);
	}

    @Override
    public boolean castToBooleanValue() throws PageException {
        return Caster.toBooleanValue(getE(position));
    }
    
    @Override
    public Boolean castToBoolean(Boolean defaultValue) {
    	Object value = get(position,defaultValue); 
    	if(value==null)return defaultValue;
        return Caster.toBoolean(value,defaultValue);
    }

    @Override
    public double castToDoubleValue() throws PageException {
        return Caster.toDoubleValue(getE(position));
    }
    
    @Override
    public double castToDoubleValue(double defaultValue) {
    	Object value = get(position,null);
    	if(value==null)return defaultValue;
        return Caster.toDoubleValue(value,defaultValue);
    }

    @Override
    public DateTime castToDateTime() throws PageException {
        return Caster.toDate(getE(position),null);
    }
    
    @Override
    public DateTime castToDateTime(DateTime defaultValue) {
        Object value = get(position,defaultValue);
        if(value==null)return defaultValue;
    	return DateCaster.toDateAdvanced(value, true, null, defaultValue); 
    }


	@Override
	public int compareTo(boolean b) throws PageException {
		return Operator.compare(castToBooleanValue(), b);
	}

	@Override
	public int compareTo(DateTime dt) throws PageException {
		return Operator.compare((Date)castToDateTime(), (Date)dt);
	}

	@Override
	public int compareTo(double d) throws PageException {
		return Operator.compare(castToDoubleValue(), d);
	}

	@Override
	public int compareTo(String str) throws PageException {
		return Operator.compare(castToString(), str);
	}

	@Override
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
		DumpTable table= (DumpTable) super.toDumpData(pageContext, maxlevel,dp);
		table.setTitle("SV Array");
		return table;
	}

	@Override
	public synchronized Object clone() {
		return duplicate(true);
	}

	@Override
	public synchronized Collection duplicate(boolean deepCopy) {
		SVArray sva = new SVArray();
		duplicate(sva,deepCopy);
		sva.position=position;
		return sva;
	}
}