package railo.runtime.type;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import railo.runtime.PageContext;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.op.Duplicator;
import railo.runtime.op.Operator;
import railo.runtime.op.date.DateCaster;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.ref.Reference;
import railo.runtime.type.util.StructSupport;

public final class SVStruct extends StructSupport implements Reference,Struct {

    private Collection.Key key;
    private StructImpl parent=new StructImpl();

    /**
     * constructor of the class
     * @param key
     */
    public SVStruct(Collection.Key key) {
        this.key=key;
    }
    
    /**
     *
     * @see railo.runtime.type.ref.Reference#getKey()
     */
    public Collection.Key getKey() {
        return key;
    }

    /**
     *
     * @see railo.runtime.type.ref.Reference#getKeyAsString()
     */
    public String getKeyAsString() {
        return key.getString();
    }

    /**
     * @see railo.runtime.type.ref.Reference#get(railo.runtime.PageContext)
     */
    public Object get(PageContext pc) throws PageException {
        return get(key);
    }

    /**
     *
     * @see railo.runtime.type.ref.Reference#get(railo.runtime.PageContext, java.lang.Object)
     */
    public Object get(PageContext pc, Object defaultValue) {
        return get(key,defaultValue);
    }

    /**
     * @see railo.runtime.type.ref.Reference#set(railo.runtime.PageContext, java.lang.Object)
     */
    public Object set(PageContext pc, Object value) throws PageException {
        return set(key,value);
    }

    /**
     * @see railo.runtime.type.ref.Reference#setEL(railo.runtime.PageContext, java.lang.Object)
     */
    public Object setEL(PageContext pc, Object value) {
        return setEL(key,value);
    }

    /**
     * @see railo.runtime.type.ref.Reference#remove(railo.runtime.PageContext)
     */
    public Object remove(PageContext pc) throws PageException {
        return remove(key);
    }
    
    /**
     * @see railo.runtime.type.ref.Reference#removeEL(railo.runtime.PageContext)
     */
    public Object removeEL(PageContext pc) {
        return removeEL(key);
    }

    /**
     * @see railo.runtime.type.ref.Reference#touch(railo.runtime.PageContext)
     */
    public Object touch(PageContext pc) throws PageException {
        Object o=get(key,null);
        if(o!=null) return o;
        return set(key,new StructImpl());
    }
    
    /**
     * @see railo.runtime.type.ref.Reference#touchEL(railo.runtime.PageContext)
     */
    public Object touchEL(PageContext pc) {
        Object o=get(key,null);
        if(o!=null) return o;
        return setEL(key,new StructImpl());
    }

    public Object getParent() {
        return parent;
    }

    /**
     * @see railo.runtime.type.Collection#clear()
     */
    public void clear() {
        parent.clear();
    }


    /**
     * @see railo.runtime.type.Collection#keyIterator()
     */
    public Iterator keyIterator() {
        return parent.keyIterator();
    }

    /**
     *
     * @see railo.runtime.type.Collection#keys()
     */
    public Collection.Key[] keys() {
        return parent.keys();
    }

    /**
     *
     * @see railo.runtime.type.Collection#keysAsString()
     */
    public String[] keysAsString() {
        return parent.keysAsString();
    }

    /**
     * @see railo.runtime.type.Collection#remove(java.lang.String)
     */
    public Object remove(String key) throws PageException {
        return parent.remove(key);
    }


    /**
     * @see railo.runtime.type.Collection#size()
     */
    public int size() {
        return parent.size();
    }

    /**
	 * @see railo.runtime.dump.Dumpable#toDumpData(railo.runtime.PageContext, int)
	 */
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
	    return parent.toDumpData(pageContext,maxlevel,dp);
    }

    /**
     * @see railo.runtime.op.Castable#castToBooleanValue()
     */
    public boolean castToBooleanValue() throws PageException {
        return Caster.toBooleanValue(get(key));
    }
    
    /**
     * @see railo.runtime.op.Castable#castToBoolean(java.lang.Boolean)
     */
    public Boolean castToBoolean(Boolean defaultValue) {
    	Object value = get(key,defaultValue); 
    	if(value==null)return defaultValue;
        return Caster.toBoolean(value,defaultValue);
    }

    /**
     * @see railo.runtime.op.Castable#castToDateTime()
     */
    public DateTime castToDateTime() throws PageException {
        return Caster.toDate(get(key),null);
    }
    
    /**
     * @see railo.runtime.op.Castable#castToDateTime(railo.runtime.type.dt.DateTime)
     */
    public DateTime castToDateTime(DateTime defaultValue) {
        Object value = get(key,defaultValue);
        if(value==null)return defaultValue;
    	return DateCaster.toDateAdvanced(value, true, null, defaultValue); 
    }

    /**
     * @see railo.runtime.op.Castable#castToDoubleValue()
     */
    public double castToDoubleValue() throws PageException {
        return Caster.toDoubleValue(get(key));
    }
    
    /**
     * @see railo.runtime.op.Castable#castToDoubleValue(double)
     */
    public double castToDoubleValue(double defaultValue) {
    	Object value=get(key,null);
    	if(value==null)return defaultValue;
        return Caster.toDoubleValue(value,defaultValue);
    }

    /**
     * @see railo.runtime.op.Castable#castToString()
     */
    public String castToString() throws PageException {
        return Caster.toString(get(key));
    }
    
	/**
	 * @see railo.runtime.type.util.StructSupport#castToString(java.lang.String)
	 */
	public String castToString(String defaultValue) {
		Object value = get(key,null);
		if(value==null) return defaultValue;
		
		return Caster.toString(value,defaultValue);
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
     * @see railo.runtime.type.Collection#duplicate(boolean)
     */
    public Collection duplicate(boolean deepCopy, Map<Object, Object> done) {
        SVStruct svs = new SVStruct(key);
        done.put(this, svs);
        try{
	        Collection.Key[] keys = keys();
	        for(int i=0;i<keys.length;i++) {
	            if(deepCopy)svs.setEL(keys[i],Duplicator.duplicate(get(keys[i],null),deepCopy));
	            else svs.setEL(keys[i],get(keys[i],null));
	        }
	        return svs;
        }
        finally{
        	done.remove(this);
        }
    }

	
	
    

	/**
	 *
	 * @see railo.runtime.type.Collection#containsKey(railo.runtime.type.Collection.Key)
	 */
	public boolean containsKey(Collection.Key key) {
		return parent.containsKey(key);
	}
	

	/**
	 *
	 * @see railo.runtime.type.Collection#get(railo.runtime.type.Collection.Key)
	 */
	public Object get(Collection.Key key) throws PageException {
		return parent.get(key);
	}

	/**
	 *
	 * @see railo.runtime.type.Collection#get(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object get(Collection.Key key, Object defaultValue) {
		return parent.get(key, defaultValue);
	}


	/**
	 * @see railo.runtime.type.Collection#remove(railo.runtime.type.Collection.Key)
	 */
	public Object remove(Collection.Key key) throws PageException {
		return parent.remove(key);
	}

	/**
	 *
	 * @see railo.runtime.type.Collection#removeEL(railo.runtime.type.Collection.Key)
	 */
	public Object removeEL(Collection.Key key) {
		return parent.removeEL(key);
	}

	/**
	 *
	 * @see railo.runtime.type.Collection#set(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object set(Collection.Key key, Object value) throws PageException {
		return parent.set(key, value);
	}

	/**
	 *
	 * @see railo.runtime.type.Collection#setEL(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object setEL(Collection.Key key, Object value) {
		return parent.setEL(key, value);
	}

	/**
	 * @see java.util.Map#containsValue(java.lang.Object)
	 */
	public boolean containsValue(Object value) {
		return parent.containsValue(value);
	}

	/**
	 * @see java.util.Map#values()
	 */
	public java.util.Collection values() {
		return parent.values();
	}

}