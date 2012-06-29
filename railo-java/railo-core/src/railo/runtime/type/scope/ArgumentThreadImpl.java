package railo.runtime.type.scope;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import railo.commons.lang.CFTypes;
import railo.runtime.PageContext;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.exp.CasterException;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.exp.PageRuntimeException;
import railo.runtime.op.Caster;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Sizeable;
import railo.runtime.type.Struct;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.util.MemberUtil;
import railo.runtime.type.util.StructUtil;

public final class ArgumentThreadImpl implements Argument,Sizeable {

	private final Struct sct;

	public ArgumentThreadImpl(Struct sct){
		this.sct=sct;
	}
	
	/**
	 * @see railo.runtime.type.scope.ArgumentPro#getFunctionArgument(java.lang.String, java.lang.Object)
	 */
	public Object getFunctionArgument(String key, Object defaultValue) {
		return sct.get(key,defaultValue);
	}

	/**
	 * @see railo.runtime.type.scope.ArgumentPro#getFunctionArgument(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object getFunctionArgument(Key key, Object defaultValue) {
		return sct.get(key,defaultValue);
	}
	


	/**
	 * @see railo.runtime.type.scope.ArgumentPro#containsFunctionArgumentKey(railo.runtime.type.Collection.Key)
	 */
	public boolean containsFunctionArgumentKey(Key key) {
		return sct.containsKey(key);
	}

	public Object setArgument(Object obj) throws PageException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see railo.runtime.type.scope.ArgumentPro#setFunctionArgumentNames(java.util.Set)
	 */
	public void setFunctionArgumentNames(Set functionArgumentNames) {
		
	}

	public boolean insert(int index, String key, Object value) throws PageException {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see railo.runtime.type.scope.Argument#isBind()
	 */
	public boolean isBind() {
		return true;
	}

	/**
	 * @see railo.runtime.type.scope.Argument#setBind(boolean)
	 */
	public void setBind(boolean bind) {
		
	}

	/**
	 * @see railo.runtime.type.scope.Scope#getType()
	 */
	public int getType() {
		return SCOPE_ARGUMENTS;
	}

	/**
	 * @see railo.runtime.type.scope.Scope#getTypeAsString()
	 */
	public String getTypeAsString() {
		return "arguments";
	}

	/**
	 * @see railo.runtime.type.scope.Scope#initialize(railo.runtime.PageContext)
	 */
	public void initialize(PageContext pc) {
	}

	/**
	 * @see railo.runtime.type.scope.Scope#isInitalized()
	 */
	public boolean isInitalized() {
		return true;
	}

	/**
	 * @see railo.runtime.type.scope.Scope#release()
	 */
	public void release() {}
	public void release(PageContext pc) {}

	/**
	 * @see railo.runtime.type.Collection#clear()
	 */
	public void clear() {
		sct.clear();
	}

	/**
	 * @see railo.runtime.type.Collection#containsKey(java.lang.String)
	 */
	public boolean containsKey(String key) {
		return sct.containsKey(key);
	}

	/**
	 * @see railo.runtime.type.Collection#containsKey(railo.runtime.type.Collection.Key)
	 */
	public boolean containsKey(Key key) {
		return sct.containsKey(key);
	}

	/**
	 * @see railo.runtime.type.Collection#duplicate(boolean)
	 */
	public Collection duplicate(boolean deepCopy) {
		return new ArgumentThreadImpl((Struct)sct.duplicate(deepCopy));
	}

	/**
	 * @see railo.runtime.type.Collection#get(java.lang.String)
	 */
	public Object get(String key) throws PageException {
		return get(KeyImpl.init(key));
	}

	/**
	 * @see railo.runtime.type.Collection#get(railo.runtime.type.Collection.Key)
	 */
	public Object get(Key key) throws PageException {
		return sct.get(key);
	}

	/**
	 * @see railo.runtime.type.Collection#get(java.lang.String, java.lang.Object)
	 */
	public Object get(String key, Object defaultValue) {
		return sct.get(key, defaultValue);
	}

	/**
	 * @see railo.runtime.type.Collection#get(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object get(Key key, Object defaultValue) {
		return sct.get(key, defaultValue);
	}

	/**
	 * @see railo.runtime.type.Collection#keys()
	 */
	public Key[] keys() {
		return sct.keys();
	}

	/**
	 * @see railo.runtime.type.Collection#remove(railo.runtime.type.Collection.Key)
	 */
	public Object remove(Key key) throws PageException {
		return sct.remove(key);
	}

	/**
	 * @see railo.runtime.type.Collection#removeEL(railo.runtime.type.Collection.Key)
	 */
	public Object removeEL(Key key) {
		return sct.removeEL(key);
	}

	/**
	 * @see railo.runtime.type.Collection#set(java.lang.String, java.lang.Object)
	 */
	public Object set(String key, Object value) throws PageException {
		return sct.set(key, value);
	}

	/**
	 * @see railo.runtime.type.Collection#set(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object set(Key key, Object value) throws PageException {
		return sct.set(key, value);
	}

	/**
	 * @see railo.runtime.type.Collection#setEL(java.lang.String, java.lang.Object)
	 */
	public Object setEL(String key, Object value) {
		return sct.setEL(key, value);
	}

	/**
	 * @see railo.runtime.type.Collection#setEL(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object setEL(Key key, Object value) {
		return sct.setEL(key, value);
	}

	/**
	 * @see railo.runtime.type.Collection#size()
	 */
	public int size() {
		return sct.size();
	}

	/**
	 * @see railo.runtime.dump.Dumpable#toDumpData(railo.runtime.PageContext, int, railo.runtime.dump.DumpProperties)
	 */
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties properties) {
		return sct.toDumpData(pageContext, maxlevel, properties);
	}

	/**
	 * @see railo.runtime.type.Iteratorable#iterator()
	 */
	public Iterator iterator() {
		return sct.iterator();
	}

	/**
	 * @see railo.runtime.type.Iteratorable#keyIterator()
	 */
	public Iterator<Collection.Key> keyIterator() {
		return sct.keyIterator();
	}
    
    @Override
	public Iterator<String> keysAsStringIterator() {
    	return sct.keysAsStringIterator();
    }
	
	@Override
	public Iterator<Entry<Key, Object>> entryIterator() {
		return sct.entryIterator();
	}

	/**
	 * @see railo.runtime.type.Iteratorable#valueIterator()
	 */
	public Iterator valueIterator() {
		return sct.valueIterator();
	}

	public Boolean castToBoolean(Boolean defaultValue) {
		return sct.castToBoolean(defaultValue);
	}

	/**
	 * @see railo.runtime.op.Castable#castToBooleanValue()
	 */
	public boolean castToBooleanValue() throws PageException {
		return sct.castToBooleanValue();
	}

	/**
	 * @see railo.runtime.op.Castable#castToDateTime()
	 */
	public DateTime castToDateTime() throws PageException {
		return sct.castToDateTime();
	}

	/**
	 * @see railo.runtime.op.Castable#castToDateTime(railo.runtime.type.dt.DateTime)
	 */
	public DateTime castToDateTime(DateTime defaultValue) {
		return sct.castToDateTime(defaultValue);
	}

	/**
	 * @see railo.runtime.op.Castable#castToDoubleValue()
	 */
	public double castToDoubleValue() throws PageException {
		return sct.castToDoubleValue();
	}

	/**
	 * @see railo.runtime.op.Castable#castToDoubleValue(double)
	 */
	public double castToDoubleValue(double defaultValue) {
		return sct.castToDoubleValue(defaultValue);
	}

	/**
	 * @see railo.runtime.op.Castable#castToString()
	 */
	public String castToString() throws PageException {
		return sct.castToString();
	}

	/**
	 * @see railo.runtime.op.Castable#castToString(java.lang.String)
	 */
	public String castToString(String defaultValue) {
		return sct.castToString(defaultValue);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(java.lang.String)
	 */
	public int compareTo(String str) throws PageException {
		return sct.compareTo(str);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(boolean)
	 */
	public int compareTo(boolean b) throws PageException {
		return sct.compareTo(b);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(double)
	 */
	public int compareTo(double d) throws PageException {
		return sct.compareTo(d);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(railo.runtime.type.dt.DateTime)
	 */
	public int compareTo(DateTime dt) throws PageException {
		return sct.compareTo(dt);
	}

	/**
	 * @see java.util.Map#containsKey(java.lang.Object)
	 */
	public boolean containsKey(Object key) {
		return sct.containsKey(key);
	}

	/**
	 * @see java.util.Map#containsValue(java.lang.Object)
	 */
	public boolean containsValue(Object value) {
		return sct.containsValue(value);
	}

	/**
	 * @see java.util.Map#entrySet()
	 */
	public Set entrySet() {
		return sct.entrySet();
	}

	/**
	 * @see java.util.Map#get(java.lang.Object)
	 */
	public Object get(Object key) {
		return sct.get(key);
	}

	/**
	 * @see java.util.Map#isEmpty()
	 */
	public boolean isEmpty() {
		return sct.isEmpty();
	}

	/**
	 * @see java.util.Map#keySet()
	 */
	public Set keySet() {
		return sct.keySet();
	}

	/**
	 * @see java.util.Map#put(java.lang.Object, java.lang.Object)
	 */
	public Object put(Object key, Object value) {
		return sct.put(key, value);
	}

	/**
	 * @see java.util.Map#putAll(java.util.Map)
	 */
	public void putAll(Map m) {
		sct.putAll(m);
	}

	/**
	 * @see java.util.Map#remove(java.lang.Object)
	 */
	public Object remove(Object key) {
		return sct.remove(key);
	}

	
	/**
	 * @see java.util.Map#values()
	 */
	public java.util.Collection values() {
		return sct.values();
	}

	/**
	 * @see railo.runtime.type.Array#append(java.lang.Object)
	 */
	public Object append(Object o) throws PageException {
		throw new CasterException(sct,"Array");
	}

	/**
	 * @see railo.runtime.type.Array#appendEL(java.lang.Object)
	 */
	public Object appendEL(Object o) {
		throw new PageRuntimeException(new CasterException(sct,"Array"));
	}

	/**
	 * @see railo.runtime.type.Array#containsKey(int)
	 */
	public boolean containsKey(int key) {
		return sct.containsKey(KeyImpl.init(Caster.toString(key)));
	}

	/**
	 * @see railo.runtime.type.Array#get(int, java.lang.Object)
	 */
	public Object get(int key, Object defaultValue) {
		return sct.get(KeyImpl.init(Caster.toString(key)),defaultValue);
	}

	/**
	 * @see railo.runtime.type.Array#getDimension()
	 */
	public int getDimension() {
		throw new PageRuntimeException(new CasterException(sct,"Array"));
	}

	/**
	 * @see railo.runtime.type.Array#getE(int)
	 */
	public Object getE(int key) throws PageException {
		return sct.get(KeyImpl.init(Caster.toString(key)));
	}

	/**
	 * @see railo.runtime.type.Array#insert(int, java.lang.Object)
	 */
	public boolean insert(int key, Object value) throws PageException {
		throw new CasterException(sct,"Array");
	}

	/**
	 * @see railo.runtime.type.Array#intKeys()
	 */
	public int[] intKeys() {
		throw new PageRuntimeException(new CasterException(sct,"Array"));
	}

	/**
	 * @see railo.runtime.type.Array#prepend(java.lang.Object)
	 */
	public Object prepend(Object o) throws PageException {
		throw new CasterException(sct,"Array");
	}

	/**
	 * @see railo.runtime.type.Array#removeE(int)
	 */
	public Object removeE(int key) throws PageException {
		return sct.remove(KeyImpl.init(Caster.toString(key)));
	}

	/**
	 * @see railo.runtime.type.Array#removeEL(int)
	 */
	public Object removeEL(int key) {
		return sct.removeEL(KeyImpl.init(Caster.toString(key)));
	}

	/**
	 * @see railo.runtime.type.Array#resize(int)
	 */
	public void resize(int to) throws PageException {
		throw new CasterException(sct,"Array");
	}

	/**
	 * @param key
	 * @param value
	 * @return
	 * @throws PageException
	 */
	public Object setE(int key, Object value) throws PageException {
		return sct.set(Caster.toString(key), value);
	}

	/**
	 * @see railo.runtime.type.Array#setEL(int, java.lang.Object)
	 */
	public Object setEL(int key, Object value) {
		return sct.setEL(Caster.toString(key), value);
	}

	/**
	 * @see railo.runtime.type.Array#sort(java.lang.String, java.lang.String)
	 */
	public void sort(String sortType, String sortOrder) throws PageException {
		throw new CasterException(sct,"Array");
	}

	public void sort(Comparator com) throws ExpressionException {
		throw new CasterException(sct,"Array");
	}

	/**
	 * @see railo.runtime.type.Array#toArray()
	 */
	public Object[] toArray() {
		try {
			return Caster.toArray(sct).toArray();
		} catch (PageException pe) {
			throw new PageRuntimeException(pe);
		}
	}

	/**
	 * @see railo.runtime.type.Array#toList()
	 */
	public List toList() {
		try {
			return Caster.toArray(sct).toList();
		} catch (PageException pe) {
			throw new PageRuntimeException(pe);
		}
	}
	
	/**
	 * @see java.lang.Object#clone()
	 */
	public Object clone(){
		return duplicate(true);
	}

	/**
	 * @see railo.runtime.type.Sizeable#sizeOf()
	 */
	public long sizeOf() {
		return StructUtil.sizeOf(this);
	}

	@Override
	public Object get(PageContext pc, Key key, Object defaultValue) {
		return get(key, defaultValue);
	}

	@Override
	public Object get(PageContext pc, Key key) throws PageException {
		return get(key);
	}

	@Override
	public Object set(PageContext pc, Key propertyName, Object value) throws PageException {
		return set(propertyName, value);
	}

	@Override
	public Object setEL(PageContext pc, Key propertyName, Object value) {
		return setEL(propertyName, value);
	}

	@Override
	public Object call(PageContext pc, Key methodName, Object[] args) throws PageException {
		return MemberUtil.call(pc, this, methodName, args, CFTypes.TYPE_ARRAY, "array");
	}

	@Override
	public Object callWithNamedValues(PageContext pc, Key methodName, Struct args) throws PageException {
		return MemberUtil.callWithNamedValues(pc,this,methodName,args, CFTypes.TYPE_ARRAY, "array");
	}

}
