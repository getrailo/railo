package railo.runtime.type;

import java.util.Date;
import java.util.Iterator;

import railo.runtime.PageContext;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.op.Duplicator;
import railo.runtime.op.Operator;
import railo.runtime.op.ThreadLocalDuplication;
import railo.runtime.op.date.DateCaster;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.it.EntryIterator;
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
    
    @Override
    public Collection.Key getKey() {
        return key;
    }

    @Override
    public String getKeyAsString() {
        return key.getString();
    }

    @Override
    public Object get(PageContext pc) throws PageException {
        return get(key);
    }

    @Override
    public Object get(PageContext pc, Object defaultValue) {
        return get(key,defaultValue);
    }

    @Override
    public Object set(PageContext pc, Object value) throws PageException {
        return set(key,value);
    }

    @Override
    public Object setEL(PageContext pc, Object value) {
        return setEL(key,value);
    }

    @Override
    public Object remove(PageContext pc) throws PageException {
        return remove(key);
    }
    
    @Override
    public Object removeEL(PageContext pc) {
        return removeEL(key);
    }

    @Override
    public Object touch(PageContext pc) throws PageException {
        Object o=get(key,null);
        if(o!=null) return o;
        return set(key,new StructImpl());
    }
    
    @Override
    public Object touchEL(PageContext pc) {
        Object o=get(key,null);
        if(o!=null) return o;
        return setEL(key,new StructImpl());
    }

    public Object getParent() {
        return parent;
    }

    @Override
    public void clear() {
        parent.clear();
    }

    @Override
	public Iterator<Collection.Key> keyIterator() {
        return parent.keyIterator();
    }
    
	@Override
	public Iterator<String> keysAsStringIterator() {
    	return parent.keysAsStringIterator();
    }
	
	@Override
	public Iterator<Entry<Key, Object>> entryIterator() {
		return new EntryIterator(this, keys());
	}
	
	@Override
	public Iterator<Object> valueIterator() {
		return parent.valueIterator();
	}

    @Override
    public Collection.Key[] keys() {
        return parent.keys();
    }

    @Override
    public int size() {
        return parent.size();
    }

    @Override
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
	    return parent.toDumpData(pageContext,maxlevel,dp);
    }

    @Override
    public boolean castToBooleanValue() throws PageException {
        return Caster.toBooleanValue(get(key));
    }
    
    @Override
    public Boolean castToBoolean(Boolean defaultValue) {
    	Object value = get(key,defaultValue); 
    	if(value==null)return defaultValue;
        return Caster.toBoolean(value,defaultValue);
    }

    @Override
    public DateTime castToDateTime() throws PageException {
        return Caster.toDate(get(key),null);
    }
    
    @Override
    public DateTime castToDateTime(DateTime defaultValue) {
        Object value = get(key,defaultValue);
        if(value==null)return defaultValue;
    	return DateCaster.toDateAdvanced(value, true, null, defaultValue); 
    }

    @Override
    public double castToDoubleValue() throws PageException {
        return Caster.toDoubleValue(get(key));
    }
    
    @Override
    public double castToDoubleValue(double defaultValue) {
    	Object value=get(key,null);
    	if(value==null)return defaultValue;
        return Caster.toDoubleValue(value,defaultValue);
    }

    @Override
    public String castToString() throws PageException {
        return Caster.toString(get(key));
    }
    
	@Override
	public String castToString(String defaultValue) {
		Object value = get(key,null);
		if(value==null) return defaultValue;
		
		return Caster.toString(value,defaultValue);
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
    public Collection duplicate(boolean deepCopy) {
        SVStruct svs = new SVStruct(key);
        ThreadLocalDuplication.set(this, svs);
        try{
	        Collection.Key[] keys = keys();
	        for(int i=0;i<keys.length;i++) {
	            if(deepCopy)svs.setEL(keys[i],Duplicator.duplicate(get(keys[i],null),deepCopy));
	            else svs.setEL(keys[i],get(keys[i],null));
	        }
	        return svs;
        }
        finally{
        	// ThreadLocalDuplication.remove(this); removed "remove" to catch sisters and brothers
        }
    }

	
	
    

	@Override
	public boolean containsKey(Collection.Key key) {
		return parent.containsKey(key);
	}
	

	@Override
	public Object get(Collection.Key key) throws PageException {
		return parent.get(key);
	}

	@Override
	public Object get(Collection.Key key, Object defaultValue) {
		return parent.get(key, defaultValue);
	}


	@Override
	public Object remove(Collection.Key key) throws PageException {
		return parent.remove(key);
	}

	@Override
	public Object removeEL(Collection.Key key) {
		return parent.removeEL(key);
	}

	@Override
	public Object set(Collection.Key key, Object value) throws PageException {
		return parent.set(key, value);
	}

	@Override
	public Object setEL(Collection.Key key, Object value) {
		return parent.setEL(key, value);
	}

	@Override
	public boolean containsValue(Object value) {
		return parent.containsValue(value);
	}

	@Override
	public java.util.Collection values() {
		return parent.values();
	}

}