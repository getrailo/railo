package railo.runtime.type.cfc;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import railo.commons.lang.types.RefBoolean;
import railo.runtime.Component;
import railo.runtime.ComponentPro;
import railo.runtime.ComponentScope;
import railo.runtime.PageContext;
import railo.runtime.PageSource;
import railo.runtime.component.Property;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.exp.PageException;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.UDF;
import railo.runtime.type.UDFProperties;
import railo.runtime.type.dt.DateTime;

public abstract class ComponentProxy implements ComponentPro {
	
	public abstract Component getComponent(); 
	

	/**
	 * @see railo.runtime.Component#getJavaAccessClass(railo.commons.lang.types.RefBoolean)
	 */
	public Class getJavaAccessClass(RefBoolean isNew) throws PageException {
		return getComponent().getJavaAccessClass(isNew);
	}

	/**
	 * @see railo.runtime.Component#getDisplayName()
	 */
	public String getDisplayName() {
		return getComponent().getDisplayName();
	}

	/**
	 * @see railo.runtime.Component#getExtends()
	 */
	public String getExtends() {
		return getComponent().getExtends();
	}

	/**
	 * @see railo.runtime.Component#getHint()
	 */
	public String getHint() {
		return getComponent().getHint();
	}

	/**
	 * @see railo.runtime.Component#getName()
	 */
	public String getName() {
		return getComponent().getName();
	}

	/**
	 * @see railo.runtime.Component#getCallName()
	 */
	public String getCallName() {
		return getComponent().getCallName();
	}

	/**
	 * @see railo.runtime.Component#getAbsName()
	 */
	public String getAbsName() {
		return getComponent().getAbsName();
	}

	/**
	 * @see railo.runtime.Component#getOutput()
	 */
	public boolean getOutput() {
		return getComponent().getOutput();
	}

	/**
	 * @see railo.runtime.Component#instanceOf(java.lang.String)
	 */
	public boolean instanceOf(String type) {
		return getComponent().instanceOf(type);
	}

	/**
	 * @see railo.runtime.Component#isValidAccess(int)
	 */
	public boolean isValidAccess(int access) {
		return getComponent().isValidAccess(access);
	}

	/**
	 * @see railo.runtime.Component#getMetaData(railo.runtime.PageContext)
	 */
	public Struct getMetaData(PageContext pc) throws PageException {
		return getComponent().getMetaData(pc);
	}

	/**
	 * @see railo.runtime.Component#call(railo.runtime.PageContext, java.lang.String, java.lang.Object[])
	 */
	public Object call(PageContext pc, String key, Object[] args)
			throws PageException {
		return getComponent().call(pc, key, args);
	}

	/**
	 * @see railo.runtime.Component#callWithNamedValues(railo.runtime.PageContext, java.lang.String, railo.runtime.type.Struct)
	 */
	public Object callWithNamedValues(PageContext pc, String key, Struct args)
			throws PageException {
		return getComponent().callWithNamedValues(pc, key, args);
	}

	/**
	 * @see railo.runtime.type.Collection#size()
	 */
	public int size() {
		return getComponent().size();
	}

	/**
	 * @see railo.runtime.type.Collection#keys()
	 */
	public Key[] keys() {
		return getComponent().keys();
	}

	/**
	 * @see railo.runtime.type.Collection#remove(railo.runtime.type.Collection.Key)
	 */
	public Object remove(Key key) throws PageException {
		return getComponent().remove(key);
	}

	/**
	 * @see railo.runtime.type.Collection#removeEL(railo.runtime.type.Collection.Key)
	 */
	public Object removeEL(Key key) {
		return getComponent().removeEL(key);
	}

	/**
	 * @see railo.runtime.type.Collection#clear()
	 */
	public void clear() {
		getComponent().clear();
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
		return getComponent().get(key);
	}

	/**
	 * @see railo.runtime.type.Collection#get(java.lang.String, java.lang.Object)
	 */
	public Object get(String key, Object defaultValue) {
		return getComponent().get(key, defaultValue);
	}

	/**
	 * @see railo.runtime.type.Collection#get(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object get(Key key, Object defaultValue) {
		return getComponent().get(key, defaultValue);
	}

	/**
	 * @see railo.runtime.type.Collection#set(java.lang.String, java.lang.Object)
	 */
	public Object set(String key, Object value) throws PageException {
		return getComponent().set(key, value);
	}

	/**
	 * @see railo.runtime.type.Collection#set(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object set(Key key, Object value) throws PageException {
		return getComponent().set(key, value);
	}

	/**
	 * @see railo.runtime.type.Collection#setEL(java.lang.String, java.lang.Object)
	 */
	public Object setEL(String key, Object value) {
		return getComponent().setEL(key, value);
	}

	/**
	 * @see railo.runtime.type.Collection#setEL(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object setEL(Key key, Object value) {
		return getComponent().setEL(key, value);
	}


	/**
	 * @see railo.runtime.type.Collection#containsKey(java.lang.String)
	 */
	public boolean containsKey(String key) {
		return getComponent().containsKey(key);
	}

	/**
	 * @see railo.runtime.type.Collection#containsKey(railo.runtime.type.Collection.Key)
	 */
	public boolean containsKey(Key key) {
		return getComponent().containsKey(key);
	}

	/**
	 * @see railo.runtime.dump.Dumpable#toDumpData(railo.runtime.PageContext, int, railo.runtime.dump.DumpProperties)
	 */
	public DumpData toDumpData(PageContext pageContext, int maxlevel,
			DumpProperties properties) {
		return getComponent().toDumpData(pageContext, maxlevel, properties);
	}

	/**
	 * @see railo.runtime.type.Iteratorable#keyIterator()
	 */
	public Iterator<Collection.Key> keyIterator() {
		return getComponent().keyIterator();
	}
    
    @Override
	public Iterator<String> keysAsStringIterator() {
    	return getComponent().keysAsStringIterator();
    }

	@Override
	public Iterator<Entry<Key, Object>> entryIterator() {
		return getComponent().entryIterator();
	}

	/**
	 * @see railo.runtime.type.Iteratorable#valueIterator()
	 */
	public Iterator<Object> valueIterator() {
		return getComponent().valueIterator();
	}

	/**
	 * @see railo.runtime.op.Castable#castToString()
	 */
	public String castToString() throws PageException {
		return getComponent().castToString();
	}

	/**
	 * @see railo.runtime.op.Castable#castToString(java.lang.String)
	 */
	public String castToString(String defaultValue) {
		return getComponent().castToString(defaultValue);
	}

	/**
	 * @see railo.runtime.op.Castable#castToBooleanValue()
	 */
	public boolean castToBooleanValue() throws PageException {
		return getComponent().castToBooleanValue();
	}

	/**
	 * @see railo.runtime.op.Castable#castToBoolean(java.lang.Boolean)
	 */
	public Boolean castToBoolean(Boolean defaultValue) {
		return getComponent().castToBoolean(defaultValue);
	}

	/**
	 * @see railo.runtime.op.Castable#castToDoubleValue()
	 */
	public double castToDoubleValue() throws PageException {
		return getComponent().castToDoubleValue();
	}

	/**
	 * @see railo.runtime.op.Castable#castToDoubleValue(double)
	 */
	public double castToDoubleValue(double defaultValue) {
		return getComponent().castToDoubleValue(defaultValue);
	}

	/**
	 * @see railo.runtime.op.Castable#castToDateTime()
	 */
	public DateTime castToDateTime() throws PageException {
		return getComponent().castToDateTime();
	}

	/**
	 * @see railo.runtime.op.Castable#castToDateTime(railo.runtime.type.dt.DateTime)
	 */
	public DateTime castToDateTime(DateTime defaultValue) {
		return getComponent().castToDateTime(defaultValue);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(java.lang.String)
	 */
	public int compareTo(String str) throws PageException {
		return getComponent().compareTo(str);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(boolean)
	 */
	public int compareTo(boolean b) throws PageException {
		return getComponent().compareTo(b);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(double)
	 */
	public int compareTo(double d) throws PageException {
		return getComponent().compareTo(d);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(railo.runtime.type.dt.DateTime)
	 */
	public int compareTo(DateTime dt) throws PageException {
		return getComponent().compareTo(dt);
	}

	/**
	 * @see java.util.Map#containsKey(java.lang.Object)
	 */
	public boolean containsKey(Object key) {
		return getComponent().containsKey(key);
	}

	/**
	 * @see java.util.Map#containsValue(java.lang.Object)
	 */
	public boolean containsValue(Object value) {
		return getComponent().containsValue(value);
	}

	/**
	 * @see java.util.Map#entrySet()
	 */
	public Set entrySet() {
		return getComponent().entrySet();
	}

	/**
	 * @see java.util.Map#get(java.lang.Object)
	 */
	public Object get(Object key) {
		return getComponent().get(key);
	}

	/**
	 * @see java.util.Map#isEmpty()
	 */
	public boolean isEmpty() {
		return getComponent().isEmpty();
	}

	/**
	 * @see java.util.Map#keySet()
	 */
	public Set keySet() {
		return getComponent().keySet();
	}

	/**
	 * @see java.util.Map#put(java.lang.Object, java.lang.Object)
	 */
	public Object put(Object key, Object value) {
		return getComponent().put(key, value);
	}

	/**
	 * @see java.util.Map#putAll(java.util.Map)
	 */
	public void putAll(Map m) {
		getComponent().putAll(m);
	}

	/**
	 * @see java.util.Map#remove(java.lang.Object)
	 */
	public Object remove(Object key) {
		return getComponent().remove(key);
	}

	/**
	 * @see java.util.Map#values()
	 */
	public java.util.Collection values() {
		return getComponent().values();
	}

	/**
	 * @see railo.runtime.type.Objects#get(railo.runtime.PageContext, railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object get(PageContext pc, Key key, Object defaultValue) {
		return getComponent().get(pc, key, defaultValue);
	}

	/**
	 * @see railo.runtime.type.Objects#get(railo.runtime.PageContext, railo.runtime.type.Collection.Key)
	 */
	public Object get(PageContext pc, Key key) throws PageException {
		return getComponent().get(pc, key);
	}

	/**
	 * @see railo.runtime.type.Objects#set(railo.runtime.PageContext, railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object set(PageContext pc, Key propertyName, Object value)
			throws PageException {
		return getComponent().set(pc, propertyName, value);
	}

	/**
	 * @see railo.runtime.type.Objects#setEL(railo.runtime.PageContext, railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object setEL(PageContext pc, Key propertyName, Object value) {
		return getComponent().setEL(pc, propertyName, value);
	}

	/**
	 * @see railo.runtime.type.Objects#call(railo.runtime.PageContext, railo.runtime.type.Collection.Key, java.lang.Object[])
	 */
	public Object call(PageContext pc, Key methodName, Object[] arguments)
			throws PageException {
		return getComponent().call(pc, methodName, arguments);
	}

	/**
	 * @see railo.runtime.type.Objects#callWithNamedValues(railo.runtime.PageContext, railo.runtime.type.Collection.Key, railo.runtime.type.Struct)
	 */
	public Object callWithNamedValues(PageContext pc, Key methodName,
			Struct args) throws PageException {
		return getComponent().callWithNamedValues(pc, methodName, args);
	}
	
	@Override
	public Property[] getProperties(boolean onlyPeristent) {
		return getComponent().getProperties(onlyPeristent);
	}

	/**
	 * @see railo.runtime.Component#setProperty(railo.runtime.component.Property)
	 */
	public void setProperty(Property property) throws PageException {
		getComponent().setProperty(property);
	}

	/**
	 * @see railo.runtime.Component#getComponentScope()
	 */
	public ComponentScope getComponentScope() {
		return getComponent().getComponentScope();
	}

	/**
	 * @see railo.runtime.Component#contains(railo.runtime.PageContext, railo.runtime.type.Collection.Key)
	 */
	public boolean contains(PageContext pc, Key key) {
		return getComponent().contains(pc, key);
	}

	/**
	 * @see railo.runtime.Component#getPageSource()
	 */
	public PageSource getPageSource() {
		return getComponent().getPageSource();
	}

	/**
	 * @see railo.runtime.Component#getBaseAbsName()
	 */
	public String getBaseAbsName() {
		return getComponent().getBaseAbsName();
	}

	/**
	 * @see railo.runtime.Component#isBasePeristent()
	 */
	public boolean isBasePeristent() {
		return getComponent().isBasePeristent();
	}

	/**
	 * @see railo.runtime.Component#equalTo(java.lang.String)
	 */
	public boolean equalTo(String type) {
		return getComponent().equalTo(type);
	}
	
	/**
	 * @see java.lang.Object#clone()
	 */
	public Object clone(){
		return duplicate(true);
	}
	
	@Override
    public void registerUDF(String key, UDF udf){
    	getComponent().registerUDF(key, udf);
    }
    
	@Override
    public void registerUDF(Collection.Key key, UDF udf){
    	getComponent().registerUDF(key, udf);
    }
    
	@Override
    public void registerUDF(String key, UDFProperties props){
    	getComponent().registerUDF(key, props);
    }
    
	@Override
    public void registerUDF(Collection.Key key, UDFProperties props){
    	getComponent().registerUDF(key, props);
    }
}
