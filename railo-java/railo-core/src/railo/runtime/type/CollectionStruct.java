package railo.runtime.type;

import java.util.Iterator;
import java.util.Map;

import railo.runtime.PageContext;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.exp.PageException;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.util.StructSupport;

public class CollectionStruct extends StructSupport implements ObjectWrap,Struct {

	private Collection coll;

	public CollectionStruct(Collection coll) {
		this.coll=coll;
	}

	/**
	 * @see java.util.Map#clear()
	 */
	public void clear() {
		coll.clear();
	}

	/**
	 * @see railo.runtime.type.Collection#containsKey(railo.runtime.type.Collection.Key)
	 */
	public boolean containsKey(Key key) {
		return coll.containsKey(key);
	}

	/**
	 * @see railo.runtime.type.Collection#duplicate(boolean)
	 */
	public Collection duplicate(boolean deepCopy,Map<Object, Object> done) {
		return coll.duplicate(deepCopy,done);
	}
	

	/**
	 * @see railo.runtime.type.Collection#get(railo.runtime.type.Collection.Key)
	 */
	public Object get(Key key) throws PageException {
		return coll.get(key);
	}

	/**
	 * @see railo.runtime.type.Collection#get(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object get(Key key, Object defaultValue) {
		return coll.get(key, defaultValue);
	}

	/**
	 * @see railo.runtime.type.Collection#keys()
	 */
	public Key[] keys() {
		return coll.keys();
	}

	/**
	 * @see railo.runtime.type.Collection#keysAsString()
	 */
	public String[] keysAsString() {
		return coll.keysAsString();
	}

	/**
	 * @see railo.runtime.type.Collection#remove(railo.runtime.type.Collection.Key)
	 */
	public Object remove(Key key) throws PageException {
		return coll.remove(key);
	}

	/**
	 * @see railo.runtime.type.Collection#removeEL(railo.runtime.type.Collection.Key)
	 */
	public Object removeEL(Key key) {
		return coll.removeEL(key);
	}

	/**
	 * @see railo.runtime.type.Collection#set(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object set(Key key, Object value) throws PageException {
		return coll.set(key, value);
	}

	/**
	 * @see railo.runtime.type.Collection#setEL(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object setEL(Key key, Object value) {
		return coll.setEL(key, value);
	}

	/**
	 * @see java.util.Map#size()
	 */
	public int size() {
		return coll.size();
	}

	/**
	 * @see railo.runtime.type.Iteratorable#keyIterator()
	 */
	public Iterator keyIterator() {
		return coll.keyIterator();
	}
	

	/**
	 * @see railo.runtime.dump.Dumpable#toDumpData(railo.runtime.PageContext, int, railo.runtime.dump.DumpProperties)
	 */
	public DumpData toDumpData(PageContext pageContext, int maxlevel,DumpProperties properties) {
		return coll.toDumpData(pageContext, maxlevel, properties);
	}

	/**
     * @see railo.runtime.op.Castable#castToBooleanValue()
     */
    public boolean castToBooleanValue() throws PageException {
    	return coll.castToBooleanValue();
    }

    /**
     * @see railo.runtime.op.Castable#castToDoubleValue()
     */
    public double castToDoubleValue() throws PageException {
    	return coll.castToDoubleValue();
    }


    /**
     * @see railo.runtime.op.Castable#castToDateTime()
     */
    public DateTime castToDateTime() throws PageException {
    	return coll.castToDateTime();
    }

    /**
     * @see railo.runtime.op.Castable#castToString()
     */
    public String castToString() throws PageException {
		return coll.castToString();
    }


	/**
	 * @see railo.runtime.op.Castable#compare(boolean)
	 */
	public int compareTo(boolean b) throws PageException {
		return coll.compareTo(b);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(railo.runtime.type.dt.DateTime)
	 */
	public int compareTo(DateTime dt) throws PageException {
		return coll.compareTo(dt);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(double)
	 */
	public int compareTo(double d) throws PageException {
		return coll.compareTo(d);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(java.lang.String)
	 */
	public int compareTo(String str) throws PageException {
		return coll.compareTo(str);
	}

	/**
	 * @see railo.runtime.type.ObjectWrap#getEmbededObject(java.lang.Object)
	 */
	public Object getEmbededObject(Object defaultValue) {
		return coll;
	}

	/**
	 * @see railo.runtime.type.ObjectWrap#getEmbededObject()
	 */
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
