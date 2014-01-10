package railo.runtime.orm.hibernate.tuplizer.proxy;

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
	

	@Override
	public Class getJavaAccessClass(RefBoolean isNew) throws PageException {
		return getComponent().getJavaAccessClass(isNew);
	}

	@Override
	public String getDisplayName() {
		return getComponent().getDisplayName();
	}

	@Override
	public String getExtends() {
		return getComponent().getExtends();
	}

	@Override
	public String getHint() {
		return getComponent().getHint();
	}

	@Override
	public String getName() {
		return getComponent().getName();
	}

	@Override
	public String getCallName() {
		return getComponent().getCallName();
	}

	@Override
	public String getAbsName() {
		return getComponent().getAbsName();
	}

	@Override
	public boolean getOutput() {
		return getComponent().getOutput();
	}

	@Override
	public boolean instanceOf(String type) {
		return getComponent().instanceOf(type);
	}

	@Override
	public boolean isValidAccess(int access) {
		return getComponent().isValidAccess(access);
	}

	@Override
	public Struct getMetaData(PageContext pc) throws PageException {
		return getComponent().getMetaData(pc);
	}

	@Override
	public Object call(PageContext pc, String key, Object[] args)
			throws PageException {
		return getComponent().call(pc, key, args);
	}

	@Override
	public Object callWithNamedValues(PageContext pc, String key, Struct args)
			throws PageException {
		return getComponent().callWithNamedValues(pc, key, args);
	}

	@Override
	public int size() {
		return getComponent().size();
	}

	@Override
	public Key[] keys() {
		return getComponent().keys();
	}

	@Override
	public Object remove(Key key) throws PageException {
		return getComponent().remove(key);
	}

	@Override
	public Object removeEL(Key key) {
		return getComponent().removeEL(key);
	}

	@Override
	public void clear() {
		getComponent().clear();
	}

	@Override
	public Object get(String key) throws PageException {
		return get(KeyImpl.init(key));
	}

	@Override
	public Object get(Key key) throws PageException {
		return getComponent().get(key);
	}

	@Override
	public Object get(String key, Object defaultValue) {
		return getComponent().get(key, defaultValue);
	}

	@Override
	public Object get(Key key, Object defaultValue) {
		return getComponent().get(key, defaultValue);
	}

	@Override
	public Object set(String key, Object value) throws PageException {
		return getComponent().set(key, value);
	}

	@Override
	public Object set(Key key, Object value) throws PageException {
		return getComponent().set(key, value);
	}

	@Override
	public Object setEL(String key, Object value) {
		return getComponent().setEL(key, value);
	}

	@Override
	public Object setEL(Key key, Object value) {
		return getComponent().setEL(key, value);
	}


	@Override
	public boolean containsKey(String key) {
		return getComponent().containsKey(key);
	}

	@Override
	public boolean containsKey(Key key) {
		return getComponent().containsKey(key);
	}

	@Override
	public DumpData toDumpData(PageContext pageContext, int maxlevel,
			DumpProperties properties) {
		return getComponent().toDumpData(pageContext, maxlevel, properties);
	}

	@Override
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

	@Override
	public Iterator<Object> valueIterator() {
		return getComponent().valueIterator();
	}

	@Override
	public String castToString() throws PageException {
		return getComponent().castToString();
	}

	@Override
	public String castToString(String defaultValue) {
		return getComponent().castToString(defaultValue);
	}

	@Override
	public boolean castToBooleanValue() throws PageException {
		return getComponent().castToBooleanValue();
	}

	@Override
	public Boolean castToBoolean(Boolean defaultValue) {
		return getComponent().castToBoolean(defaultValue);
	}

	@Override
	public double castToDoubleValue() throws PageException {
		return getComponent().castToDoubleValue();
	}

	@Override
	public double castToDoubleValue(double defaultValue) {
		return getComponent().castToDoubleValue(defaultValue);
	}

	@Override
	public DateTime castToDateTime() throws PageException {
		return getComponent().castToDateTime();
	}

	@Override
	public DateTime castToDateTime(DateTime defaultValue) {
		return getComponent().castToDateTime(defaultValue);
	}

	@Override
	public int compareTo(String str) throws PageException {
		return getComponent().compareTo(str);
	}

	@Override
	public int compareTo(boolean b) throws PageException {
		return getComponent().compareTo(b);
	}

	@Override
	public int compareTo(double d) throws PageException {
		return getComponent().compareTo(d);
	}

	@Override
	public int compareTo(DateTime dt) throws PageException {
		return getComponent().compareTo(dt);
	}

	@Override
	public boolean containsKey(Object key) {
		return getComponent().containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return getComponent().containsValue(value);
	}

	@Override
	public Set entrySet() {
		return getComponent().entrySet();
	}

	@Override
	public Object get(Object key) {
		return getComponent().get(key);
	}

	@Override
	public boolean isEmpty() {
		return getComponent().isEmpty();
	}

	@Override
	public Set keySet() {
		return getComponent().keySet();
	}

	@Override
	public Object put(Object key, Object value) {
		return getComponent().put(key, value);
	}

	@Override
	public void putAll(Map m) {
		getComponent().putAll(m);
	}

	@Override
	public Object remove(Object key) {
		return getComponent().remove(key);
	}

	@Override
	public java.util.Collection values() {
		return getComponent().values();
	}

	@Override
	public Object get(PageContext pc, Key key, Object defaultValue) {
		return getComponent().get(pc, key, defaultValue);
	}

	@Override
	public Object get(PageContext pc, Key key) throws PageException {
		return getComponent().get(pc, key);
	}

	@Override
	public Object set(PageContext pc, Key propertyName, Object value)
			throws PageException {
		return getComponent().set(pc, propertyName, value);
	}

	@Override
	public Object setEL(PageContext pc, Key propertyName, Object value) {
		return getComponent().setEL(pc, propertyName, value);
	}

	@Override
	public Object call(PageContext pc, Key methodName, Object[] arguments)
			throws PageException {
		return getComponent().call(pc, methodName, arguments);
	}

	@Override
	public Object callWithNamedValues(PageContext pc, Key methodName,
			Struct args) throws PageException {
		return getComponent().callWithNamedValues(pc, methodName, args);
	}
	
	@Override
	public Property[] getProperties(boolean onlyPeristent) {
		return getComponent().getProperties(onlyPeristent);
	}

	@Override
	public void setProperty(Property property) throws PageException {
		getComponent().setProperty(property);
	}

	@Override
	public ComponentScope getComponentScope() {
		return getComponent().getComponentScope();
	}

	@Override
	public boolean contains(PageContext pc, Key key) {
		return getComponent().contains(pc, key);
	}

	@Override
	public PageSource getPageSource() {
		return getComponent().getPageSource();
	}

	@Override
	public String getBaseAbsName() {
		return getComponent().getBaseAbsName();
	}

	@Override
	public boolean isBasePeristent() {
		return getComponent().isBasePeristent();
	}

	@Override
	public boolean equalTo(String type) {
		return getComponent().equalTo(type);
	}
	
	@Override
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
	

	@Override
	public Property[] getProperties(boolean onlyPeristent, boolean includeBaseProperties, boolean overrideProperties, boolean inheritedMappedSuperClassOnly) {
		return ((ComponentPro)getComponent()).getProperties(onlyPeristent, includeBaseProperties, overrideProperties, inheritedMappedSuperClassOnly);
	}
	
	@Override
	public java.util.Iterator<String> getIterator() {
    	return keysAsStringIterator();
    }

	@Override
	public long sizeOf() {
		return getComponent().sizeOf();
	}
}
