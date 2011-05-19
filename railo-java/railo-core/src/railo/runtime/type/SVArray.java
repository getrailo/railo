package railo.runtime.type;

import java.util.Date;
import java.util.Map;

import railo.runtime.PageContext;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.dump.DumpTable;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.op.Operator;
import railo.runtime.op.ThreadLocalDuplication;
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

    /**
     * @see railo.runtime.type.ref.Reference#getKey()
     */
    public Collection.Key getKey() {
        return KeyImpl.init(Caster.toString(position));
    }

    /**
     *
     * @see railo.runtime.type.ref.Reference#getKeyAsString()
     */
    public String getKeyAsString() {
        return Caster.toString(position);
    }

    /**
     * @see railo.runtime.type.ref.Reference#get(railo.runtime.PageContext)
     */
    public Object get(PageContext pc) throws PageException {
        return getE(position);
    }

    /**
     *
     * @see railo.runtime.type.ref.Reference#get(railo.runtime.PageContext, java.lang.Object)
     */
    public Object get(PageContext pc, Object defaultValue) {
        return get(position,defaultValue);
    }

    /**
     * @see railo.runtime.type.ref.Reference#touch(railo.runtime.PageContext)
     */
    public Object touch(PageContext pc) throws PageException {
        Object o=get(position,null);
        if(o!=null) return o;
        return setE(position,new StructImpl());
    }
    
    /**
     * @see railo.runtime.type.ref.Reference#touchEL(railo.runtime.PageContext)
     */
    public Object touchEL(PageContext pc) {
        Object o=get(position,null);
        if(o!=null) return o;
        return setEL(position,new StructImpl());
    }

    /**
     * @see railo.runtime.type.ref.Reference#set(railo.runtime.PageContext, java.lang.Object)
     */
    public Object set(PageContext pc, Object value) throws PageException {
        return setE(position,value);
    }
    
    /**
     * @see railo.runtime.type.ref.Reference#setEL(railo.runtime.PageContext, java.lang.Object)
     */
    public Object setEL(PageContext pc, Object value) {
        return setEL(position,value);
    }

    /**
     * @see railo.runtime.type.ref.Reference#remove(railo.runtime.PageContext)
     */
    public Object remove(PageContext pc) throws PageException {
        return removeE(position);
    }
    
    /**
     * @see railo.runtime.type.ref.Reference#removeEL(railo.runtime.PageContext)
     */
    public Object removeEL(PageContext pc) {
        return removeEL(position);
    }

    /**
     * @see railo.runtime.type.ref.Reference#getParent()
     */
    public Object getParent() {
        return this;
    }

    /**
     * @see railo.runtime.op.Castable#castToString()
     */
    public String castToString() throws PageException {
        return Caster.toString(getE(position));
    }
    
	/**
	 * @see railo.runtime.type.util.StructSupport#castToString(java.lang.String)
	 */
	public String castToString(String defaultValue) {
		Object value = get(position,null);
		if(value==null) return defaultValue;
		return Caster.toString(value,defaultValue);
	}

    /**
     * @see railo.runtime.op.Castable#castToBooleanValue()
     */
    public boolean castToBooleanValue() throws PageException {
        return Caster.toBooleanValue(getE(position));
    }
    
    /**
     * @see railo.runtime.op.Castable#castToBoolean(java.lang.Boolean)
     */
    public Boolean castToBoolean(Boolean defaultValue) {
    	Object value = get(position,defaultValue); 
    	if(value==null)return defaultValue;
        return Caster.toBoolean(value,defaultValue);
    }

    /**
     * @see railo.runtime.op.Castable#castToDoubleValue()
     */
    public double castToDoubleValue() throws PageException {
        return Caster.toDoubleValue(getE(position));
    }
    
    /**
     * @see railo.runtime.op.Castable#castToDoubleValue(double)
     */
    public double castToDoubleValue(double defaultValue) {
    	Object value = get(position,null);
    	if(value==null)return defaultValue;
        return Caster.toDoubleValue(value,defaultValue);
    }

    /**
     * @see railo.runtime.op.Castable#castToDateTime()
     */
    public DateTime castToDateTime() throws PageException {
        return Caster.toDate(getE(position),null);
    }
    
    /**
     * @see railo.runtime.op.Castable#castToDateTime(railo.runtime.type.dt.DateTime)
     */
    public DateTime castToDateTime(DateTime defaultValue) {
        Object value = get(position,defaultValue);
        if(value==null)return defaultValue;
    	return DateCaster.toDateAdvanced(value, true, null, defaultValue); 
    }


	/**
	 * @see railo.runtime.op.Castable#compare(boolean)
	 */
	public int compareTo(boolean b) throws PageException {
		return Operator.compare(castToBooleanValue(), b);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(railo.runtime.type.dt.DateTime)
	 */
	public int compareTo(DateTime dt) throws PageException {
		return Operator.compare((Date)castToDateTime(), (Date)dt);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(double)
	 */
	public int compareTo(double d) throws PageException {
		return Operator.compare(castToDoubleValue(), d);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(java.lang.String)
	 */
	public int compareTo(String str) throws PageException {
		return Operator.compare(castToString(), str);
	}

	/**
	 *
	 * @see railo.runtime.type.ArrayImpl#toDumpData(railo.runtime.PageContext, int)
	 */
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
		DumpTable table= (DumpTable) super.toDumpData(pageContext, maxlevel,dp);
		table.setTitle("SV Array");
		return table;
	}

	/**
	 *
	 * @see railo.runtime.type.ArrayImpl#clone()
	 */
	public synchronized Object clone() {
		return duplicate(true,ThreadLocalDuplication.getMap());
	}

	/**
	 *
	 * @see railo.runtime.type.ArrayImpl#duplicate(boolean)
	 */
	public synchronized Collection duplicate(boolean deepCopy,Map<Object, Object> done) {
		SVArray sva = new SVArray();
		duplicate(sva,deepCopy,done);
		sva.position=position;
		return sva;
	}
}