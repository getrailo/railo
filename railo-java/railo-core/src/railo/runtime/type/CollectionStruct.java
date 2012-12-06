package railo.runtime.type;

import java.util.Iterator;

import railo.runtime.PageContext;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.exp.PageException;
import railo.runtime.op.Duplicator;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.util.StructSupport;

public final class CollectionStruct extends StructSupport implements ObjectWrap,Struct {

	private final Collection coll;

	public CollectionStruct(Collection coll) {
		this.coll=coll;
	}

	@Override
	public void clear() {
		coll.clear();
	}

	@Override
	public boolean containsKey(Key key) {
		return coll.containsKey(key);
	}

	@Override
	public Collection duplicate(boolean deepCopy) {
		return (Collection) Duplicator.duplicate(coll,deepCopy);
	}
	

	@Override
	public Object get(Key key) throws PageException {
		return coll.get(key);
	}

	@Override
	public Object get(Key key, Object defaultValue) {
		return coll.get(key, defaultValue);
	}

	@Override
	public Key[] keys() {
		return coll.keys();
	}

	@Override
	public Object remove(Key key) throws PageException {
		return coll.remove(key);
	}

	@Override
	public Object removeEL(Key key) {
		return coll.removeEL(key);
	}

	@Override
	public Object set(Key key, Object value) throws PageException {
		return coll.set(key, value);
	}

	@Override
	public Object setEL(Key key, Object value) {
		return coll.setEL(key, value);
	}

	@Override
	public int size() {
		return coll.size();
	}

	@Override
	public Iterator<Collection.Key> keyIterator() {
		return coll.keyIterator();
	}
    
	@Override
	public Iterator<String> keysAsStringIterator() {
    	return coll.keysAsStringIterator();
    }
	
	@Override
	public Iterator<Entry<Key, Object>> entryIterator() {
		return coll.entryIterator();
	}
	
	@Override
	public Iterator<Object> valueIterator() {
		return coll.valueIterator();
	}
	

	@Override
	public DumpData toDumpData(PageContext pageContext, int maxlevel,DumpProperties properties) {
		return coll.toDumpData(pageContext, maxlevel, properties);
	}

	@Override
    public boolean castToBooleanValue() throws PageException {
    	return coll.castToBooleanValue();
    }

    @Override
    public double castToDoubleValue() throws PageException {
    	return coll.castToDoubleValue();
    }


    @Override
    public DateTime castToDateTime() throws PageException {
    	return coll.castToDateTime();
    }

    @Override
    public String castToString() throws PageException {
		return coll.castToString();
    }


	@Override
	public int compareTo(boolean b) throws PageException {
		return coll.compareTo(b);
	}

	@Override
	public int compareTo(DateTime dt) throws PageException {
		return coll.compareTo(dt);
	}

	@Override
	public int compareTo(double d) throws PageException {
		return coll.compareTo(d);
	}

	@Override
	public int compareTo(String str) throws PageException {
		return coll.compareTo(str);
	}

	@Override
	public Object getEmbededObject(Object defaultValue) {
		return coll;
	}

	@Override
	public Object getEmbededObject() throws PageException {
		return coll;
	}

	/**
	 * @return
	 */
	public Collection getCollection() {
		return coll;
	}
}
