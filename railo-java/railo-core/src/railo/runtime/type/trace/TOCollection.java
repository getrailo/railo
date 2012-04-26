package railo.runtime.type.trace;

import java.util.Iterator;
import java.util.Map.Entry;

import railo.runtime.PageContext;
import railo.runtime.debug.Debugger;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.exp.PageException;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.it.EntryIterator;

abstract class TOCollection extends TOObjects implements Collection {

	private static final long serialVersionUID = -6006915508424163880L;

	private Collection coll;

	protected TOCollection(Debugger debugger,Collection coll, int type,String category,String text) {
		super(debugger,coll, type, category, text);
		this.coll=coll;
	}
	

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public Object clone() {
		return duplicate(true);
	}


	/**
	 * @see railo.runtime.type.Iteratorable#keyIterator()
	 */
	public Iterator<Collection.Key> keyIterator() {
		log();
		return coll.keyIterator();
	}
	
	@Override
	public Iterator<Entry<Key, Object>> entryIterator() {
		log();
		return coll.entryIterator();
	}

	/**
	 * @see railo.runtime.type.Iteratorable#valueIterator()
	 */
	public Iterator valueIterator() {
		log();
		return coll.valueIterator();
	}

	/**
	 * @see railo.runtime.type.Iteratorable#iterator()
	 */
	public Iterator iterator() {
		log();
		return coll.iterator();
	}

	public String castToString() throws PageException {
		log();
		return coll.castToString();
	}

	/**
	 * @see railo.runtime.op.Castable#castToString(java.lang.String)
	 */
	public String castToString(String defaultValue) {
		log();
		return coll.castToString(defaultValue);
	}

	/**
	 * @see railo.runtime.op.Castable#castToBooleanValue()
	 */
	public boolean castToBooleanValue() throws PageException {
		log();
		return coll.castToBooleanValue();
	}

	/**
	 * @see railo.runtime.op.Castable#castToBoolean(java.lang.Boolean)
	 */
	public Boolean castToBoolean(Boolean defaultValue) {
		log();
		return coll.castToBoolean(defaultValue);
	}

	/**
	 * @see railo.runtime.op.Castable#castToDoubleValue()
	 */
	public double castToDoubleValue() throws PageException {
		log();
		return coll.castToDoubleValue();
	}

	/**
	 * @see railo.runtime.op.Castable#castToDoubleValue(double)
	 */
	public double castToDoubleValue(double defaultValue) {
		log();
		return coll.castToDoubleValue(defaultValue);
	}

	/**
	 * @see railo.runtime.op.Castable#castToDateTime()
	 */
	public DateTime castToDateTime() throws PageException {
		log();
		return new TODateTime(debugger,coll.castToDateTime(),type,category,text);
	}

	/**
	 * @see railo.runtime.op.Castable#castToDateTime(railo.runtime.type.dt.DateTime)
	 */
	public DateTime castToDateTime(DateTime defaultValue) {
		log();
		return  new TODateTime(debugger,coll.castToDateTime(defaultValue),type,category,text);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(java.lang.String)
	 */
	public int compareTo(String str) throws PageException {
		log();
		return coll.compareTo(str);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(boolean)
	 */
	public int compareTo(boolean b) throws PageException {
		log();
		return coll.compareTo(b);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(double)
	 */
	public int compareTo(double d) throws PageException {
		log();
		return coll.compareTo(d);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(railo.runtime.type.dt.DateTime)
	 */
	public int compareTo(DateTime dt) throws PageException {
		log();
		return coll.compareTo(dt);
	}

	/**
	 * @see railo.runtime.type.Collection#size()
	 */
	public int size() {
		log();
		return coll.size();
	}

	/**
	 * @see railo.runtime.type.Collection#keys()
	 */
	public Key[] keys() {
		log();
		return coll.keys();
	}

	/**
	 * @see railo.runtime.type.Collection#keysAsString()
	 */
	public String[] keysAsString() {
		log();
		return coll.keysAsString();
	}

	/**
	 * @see railo.runtime.type.Collection#remove(railo.runtime.type.Collection.Key)
	 */
	public Object remove(Key key) throws PageException {
		log(key.getString());
		return coll.remove(key);
		//return TraceObjectSupport.toTraceObject(debugger,coll.remove(key),type,category,text);
	}

	/**
	 * @see railo.runtime.type.Collection#removeEL(railo.runtime.type.Collection.Key)
	 */
	public Object removeEL(Key key) {
		log(key.getString());
		return coll.removeEL(key);
		//return TraceObjectSupport.toTraceObject(debugger,coll.removeEL(key),type,category,text);
	}

	/**
	 * @see railo.runtime.type.Collection#clear()
	 */
	public void clear() {
		log();
		coll.clear();
	}

	/**
	 * @see railo.runtime.type.Collection#get(java.lang.String)
	 */
	public Object get(String key) throws PageException {
		log(key);
		return coll.get(KeyImpl.init(key));
		//return TraceObjectSupport.toTraceObject(debugger,coll.get(key),type,category,text);
	}

	/**
	 * @see railo.runtime.type.Collection#get(railo.runtime.type.Collection.Key)
	 */
	public Object get(Key key) throws PageException {
		log(key.getString());
		return coll.get(key);
		//return TraceObjectSupport.toTraceObject(debugger,coll.get(key),type,category,text);
	}

	/**
	 * @see railo.runtime.type.Collection#get(java.lang.String, java.lang.Object)
	 */
	public Object get(String key, Object defaultValue) {
		log(key);
		return coll.get(key, defaultValue);
		//return TraceObjectSupport.toTraceObject(debugger,coll.get(key, defaultValue),type,category,text);
	}

	/**
	 * @see railo.runtime.type.Collection#get(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object get(Key key, Object defaultValue) {
		log(key.getString());
		return coll.get(key,defaultValue);
		//return TraceObjectSupport.toTraceObject(debugger,coll.get(key,defaultValue),type,category,text);
	}

	/**
	 * @see railo.runtime.type.Collection#set(java.lang.String, java.lang.Object)
	 */
	public Object set(String key, Object value) throws PageException {
		log(key,value);
		return coll.set(key, value);
		//return TraceObjectSupport.toTraceObject(debugger,coll.set(key, value),type,category,text);
	}

	/**
	 * @see railo.runtime.type.Collection#set(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object set(Key key, Object value) throws PageException {
		log(key.getString(),value);
		return coll.set(key, value);
		//return TraceObjectSupport.toTraceObject(debugger,coll.set(key, value),type,category,text);
	}

	/**
	 * @see railo.runtime.type.Collection#setEL(java.lang.String, java.lang.Object)
	 */
	public Object setEL(String key, Object value) {
		log(key,value);
		return coll.setEL(key, value);
		//return TraceObjectSupport.toTraceObject(debugger,coll.setEL(key, value),type,category,text);
	}

	/**
	 * @see railo.runtime.type.Collection#setEL(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object setEL(Key key, Object value) {
		log(key.getString(),value);
		return coll.setEL(key, value);
		//return TraceObjectSupport.toTraceObject(debugger,coll.setEL(key, value),type,category,text);
	}

	/**
	 * @see railo.runtime.type.Collection#containsKey(java.lang.String)
	 */
	public boolean containsKey(String key) {
		log(key);
		return coll.containsKey(key);
	}

	/**
	 * @see railo.runtime.type.Collection#containsKey(railo.runtime.type.Collection.Key)
	 */
	public boolean containsKey(Key key) {
		log(key.getString());
		return coll.containsKey(key);
	}


	public DumpData toDumpData(PageContext pageContext, int maxlevel,DumpProperties properties) {
		log();
		return coll.toDumpData(pageContext, maxlevel, properties);
	}
}
