package railo.runtime.type.scope;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import railo.runtime.PageContext;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.exp.PageException;
import railo.runtime.type.Collection;
import railo.runtime.type.dt.DateTime;

public final class VariablesAsSession implements Session {
	
	private Session session;

	public VariablesAsSession(Session session){
		this.session=session;
	}

	public long getLastAccess() {
		// TODO Auto-generated method stub
		return 0;
	}

	public long getTimeSpan() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean isExpired() {
		// TODO Auto-generated method stub
		return false;
	}

	public void touch() {
		// TODO Auto-generated method stub
		
	}

	public int getType() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getTypeAsString() {
		// TODO Auto-generated method stub
		return null;
	}

	public void initialize(PageContext pc) {
		// TODO Auto-generated method stub
		
	}

	public boolean isInitalized() {
		// TODO Auto-generated method stub
		return false;
	}

	public void release() {
		// TODO Auto-generated method stub
		
	}

	public void clear() {
		// TODO Auto-generated method stub
		
	}

	public boolean containsKey(String key) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean containsKey(Key key) {
		// TODO Auto-generated method stub
		return false;
	}

	public Collection duplicate(boolean deepCopy) {
		// TODO Auto-generated method stub
		return null;
	}
	

	public Object get(String key) throws PageException {
		// TODO Auto-generated method stub
		return null;
	}

	public Object get(Key key) throws PageException {
		// TODO Auto-generated method stub
		return null;
	}

	public Object get(String key, Object defaultValue) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object get(Key key, Object defaultValue) {
		// TODO Auto-generated method stub
		return null;
	}

	public Key[] keys() {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] keysAsString() {
		// TODO Auto-generated method stub
		return null;
	}

	public Object remove(Key key) throws PageException {
		// TODO Auto-generated method stub
		return null;
	}

	public Object removeEL(Key key) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see railo.runtime.type.Collection#set(java.lang.String, java.lang.Object)
	 */
	public Object set(String key, Object value) throws PageException {
		return session.set(key, value);
	}

	/**
	 * @see railo.runtime.type.Collection#set(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object set(Key key, Object value) throws PageException {
		return session.set(key, value);
	}

	/**
	 * @see railo.runtime.type.Collection#setEL(java.lang.String, java.lang.Object)
	 */
	public Object setEL(String key, Object value) {
		return session.setEL(key, value);
	}

	/**
	 * @see railo.runtime.type.Collection#setEL(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object setEL(Key key, Object value) {
		return session.setEL(key, value);
	}

	/**
	 * @see railo.runtime.type.Collection#size()
	 */
	public int size() {
		return session.size();
	}

	/**
	 * @see railo.runtime.dump.Dumpable#toDumpData(railo.runtime.PageContext, int, railo.runtime.dump.DumpProperties)
	 */
	public DumpData toDumpData(PageContext pageContext, int maxlevel,DumpProperties properties) {
		return session.toDumpData(pageContext, maxlevel, properties);
	}

	public Iterator iterator() {
		return session.iterator();
	}

	/**
	 * @see railo.runtime.type.Iteratorable#keyIterator()
	 */
	public Iterator keyIterator() {
		return session.keyIterator();
	}

	/**
	 * @see railo.runtime.type.Iteratorable#valueIterator()
	 */
	public Iterator valueIterator() {
		return session.valueIterator();
	}

	/**
	 * @see railo.runtime.op.Castable#castToBoolean(java.lang.Boolean)
	 */
	public Boolean castToBoolean(Boolean defaultValue) {
		return session.castToBoolean(defaultValue);
	}

	/**
	 * @see railo.runtime.op.Castable#castToBooleanValue()
	 */
	public boolean castToBooleanValue() throws PageException {
		return session.castToBooleanValue();
	}

	/**
	 * @see railo.runtime.op.Castable#castToDateTime()
	 */
	public DateTime castToDateTime() throws PageException {
		return session.castToDateTime();
	}

	/**
	 * @see railo.runtime.op.Castable#castToDateTime(railo.runtime.type.dt.DateTime)
	 */
	public DateTime castToDateTime(DateTime defaultValue) {
		return session.castToDateTime(defaultValue);
	}

	/**
	 * @see railo.runtime.op.Castable#castToDoubleValue()
	 */
	public double castToDoubleValue() throws PageException {
		return session.castToDoubleValue();
	}

	/**
	 * @see railo.runtime.op.Castable#castToDoubleValue(double)
	 */
	public double castToDoubleValue(double defaultValue) {
		return session.castToDoubleValue(defaultValue);
	}

	/**
	 * @see railo.runtime.op.Castable#castToString()
	 */
	public String castToString() throws PageException {
		return session.castToString();
	}

	/**
	 * @see railo.runtime.op.Castable#castToString(java.lang.String)
	 */
	public String castToString(String defaultValue) {
		return session.castToString(defaultValue);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(java.lang.String)
	 */
	public int compareTo(String str) throws PageException {
		return compareTo(str);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(boolean)
	 */
	public int compareTo(boolean b) throws PageException {
		return session.compareTo(b);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(double)
	 */
	public int compareTo(double d) throws PageException {
		return session.compareTo(d);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(railo.runtime.type.dt.DateTime)
	 */
	public int compareTo(DateTime dt) throws PageException {
		return session.compareTo(dt);
	}

	/**
	 * @see java.util.Map#containsKey(java.lang.Object)
	 */
	public boolean containsKey(Object key) {
		return session.containsKey(key);
	}

	/**
	 * @see java.util.Map#containsValue(java.lang.Object)
	 */
	public boolean containsValue(Object value) {
		return session.containsValue(value);
	}

	/**
	 * @see java.util.Map#entrySet()
	 */
	public Set entrySet() {
		return session.entrySet();
	}

	/**
	 * @see java.util.Map#get(java.lang.Object)
	 */
	public Object get(Object key) {
		return session.get(key);
	}

	/**
	 * @see java.util.Map#isEmpty()
	 */
	public boolean isEmpty() {
		return session.isEmpty();
	}

	/**
	 * @see java.util.Map#keySet()
	 */
	public Set keySet() {
		return session.keySet();
	}

	/**
	 * @see java.util.Map#put(java.lang.Object, java.lang.Object)
	 */
	public Object put(Object key, Object value) {
		return session.put(key, value);
	}

	/**
	 * @see java.util.Map#putAll(java.util.Map)
	 */
	public void putAll(Map m) {
		session.putAll(m);
	}

	/**
	 * @see java.util.Map#remove(java.lang.Object)
	 */
	public Object remove(Object key) {
		return session.remove(key);
	}

	/**
	 * @see java.util.Map#values()
	 */
	public java.util.Collection values() {
		return session.values();
	}

	/**
	 * @see java.lang.Object#clone()
	 */
	public Object clone() {
		return duplicate(false);
	}

	@Override
	public long sizeOf() {
		return session.sizeOf();
	}

	@Override
	public void touchBeforeRequest(PageContext pc) {
		session.touchBeforeRequest(pc);
	}

	@Override
	public void touchAfterRequest(PageContext pc) {
		session.touchAfterRequest(pc);
	}

	@Override
	public int _getId() {
		return session._getId();
	}

}
