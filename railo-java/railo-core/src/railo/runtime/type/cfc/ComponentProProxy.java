package railo.runtime.type.cfc;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import railo.commons.lang.types.RefBoolean;
import railo.runtime.ComponentPro;
import railo.runtime.ComponentScope;
import railo.runtime.Page;
import railo.runtime.PageContext;
import railo.runtime.PageSource;
import railo.runtime.component.Property;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.exp.PageException;
import railo.runtime.type.Struct;
import railo.runtime.type.dt.DateTime;

public abstract class ComponentProProxy implements ComponentPro {
	
	public abstract ComponentPro getComponentPro(); 
	

	/**
	 * @see railo.runtime.Component#getJavaAccessClass(railo.commons.lang.types.RefBoolean)
	 */
	public Class getJavaAccessClass(RefBoolean isNew) throws PageException {
		return getComponentPro().getJavaAccessClass(isNew);
	}

	/**
	 * @see railo.runtime.Component#getDisplayName()
	 */
	public String getDisplayName() {
		return getComponentPro().getDisplayName();
	}

	/**
	 * @see railo.runtime.Component#getExtends()
	 */
	public String getExtends() {
		return getComponentPro().getExtends();
	}

	/**
	 * @see railo.runtime.Component#getHint()
	 */
	public String getHint() {
		return getComponentPro().getHint();
	}

	/**
	 * @see railo.runtime.Component#getName()
	 */
	public String getName() {
		return getComponentPro().getName();
	}

	/**
	 * @see railo.runtime.Component#getCallName()
	 */
	public String getCallName() {
		return getComponentPro().getCallName();
	}

	/**
	 * @see railo.runtime.Component#getAbsName()
	 */
	public String getAbsName() {
		return getComponentPro().getAbsName();
	}

	/**
	 * @see railo.runtime.Component#getOutput()
	 */
	public boolean getOutput() {
		return getComponentPro().getOutput();
	}

	/**
	 * @see railo.runtime.Component#instanceOf(java.lang.String)
	 */
	public boolean instanceOf(String type) {
		return getComponentPro().instanceOf(type);
	}

	/**
	 * @see railo.runtime.Component#isValidAccess(int)
	 */
	public boolean isValidAccess(int access) {
		return getComponentPro().isValidAccess(access);
	}

	/**
	 * @see railo.runtime.Component#getMetaData(railo.runtime.PageContext)
	 */
	public Struct getMetaData(PageContext pc) throws PageException {
		return getComponentPro().getMetaData(pc);
	}

	/**
	 * @see railo.runtime.Component#call(railo.runtime.PageContext, java.lang.String, java.lang.Object[])
	 */
	public Object call(PageContext pc, String key, Object[] args)
			throws PageException {
		return getComponentPro().call(pc, key, args);
	}

	/**
	 * @see railo.runtime.Component#callWithNamedValues(railo.runtime.PageContext, java.lang.String, railo.runtime.type.Struct)
	 */
	public Object callWithNamedValues(PageContext pc, String key, Struct args)
			throws PageException {
		return getComponentPro().callWithNamedValues(pc, key, args);
	}

	/**
	 * @see railo.runtime.Component#getPage()
	 */
	public Page getPage() {
		return getComponentPro().getPage();
	}

	/**
	 * @see railo.runtime.type.Collection#size()
	 */
	public int size() {
		return getComponentPro().size();
	}

	/**
	 * @see railo.runtime.type.Collection#keys()
	 */
	public Key[] keys() {
		return getComponentPro().keys();
	}

	/**
	 * @see railo.runtime.type.Collection#keysAsString()
	 */
	public String[] keysAsString() {
		return getComponentPro().keysAsString();
	}

	/**
	 * @see railo.runtime.type.Collection#remove(railo.runtime.type.Collection.Key)
	 */
	public Object remove(Key key) throws PageException {
		return getComponentPro().remove(key);
	}

	/**
	 * @see railo.runtime.type.Collection#removeEL(railo.runtime.type.Collection.Key)
	 */
	public Object removeEL(Key key) {
		return getComponentPro().removeEL(key);
	}

	/**
	 * @see railo.runtime.type.Collection#clear()
	 */
	public void clear() {
		getComponentPro().clear();
	}

	/**
	 * @see railo.runtime.type.Collection#get(java.lang.String)
	 */
	public Object get(String key) throws PageException {
		return getComponentPro().get(key);
	}

	/**
	 * @see railo.runtime.type.Collection#get(railo.runtime.type.Collection.Key)
	 */
	public Object get(Key key) throws PageException {
		return getComponentPro().get(key);
	}

	/**
	 * @see railo.runtime.type.Collection#get(java.lang.String, java.lang.Object)
	 */
	public Object get(String key, Object defaultValue) {
		return getComponentPro().get(key, defaultValue);
	}

	/**
	 * @see railo.runtime.type.Collection#get(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object get(Key key, Object defaultValue) {
		return getComponentPro().get(key, defaultValue);
	}

	/**
	 * @see railo.runtime.type.Collection#set(java.lang.String, java.lang.Object)
	 */
	public Object set(String key, Object value) throws PageException {
		return getComponentPro().set(key, value);
	}

	/**
	 * @see railo.runtime.type.Collection#set(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object set(Key key, Object value) throws PageException {
		return getComponentPro().set(key, value);
	}

	/**
	 * @see railo.runtime.type.Collection#setEL(java.lang.String, java.lang.Object)
	 */
	public Object setEL(String key, Object value) {
		return getComponentPro().setEL(key, value);
	}

	/**
	 * @see railo.runtime.type.Collection#setEL(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object setEL(Key key, Object value) {
		return getComponentPro().setEL(key, value);
	}


	/**
	 * @see railo.runtime.type.Collection#containsKey(java.lang.String)
	 */
	public boolean containsKey(String key) {
		return getComponentPro().containsKey(key);
	}

	/**
	 * @see railo.runtime.type.Collection#containsKey(railo.runtime.type.Collection.Key)
	 */
	public boolean containsKey(Key key) {
		return getComponentPro().containsKey(key);
	}

	/**
	 * @see railo.runtime.dump.Dumpable#toDumpData(railo.runtime.PageContext, int, railo.runtime.dump.DumpProperties)
	 */
	public DumpData toDumpData(PageContext pageContext, int maxlevel,
			DumpProperties properties) {
		return getComponentPro().toDumpData(pageContext, maxlevel, properties);
	}

	/**
	 * @see railo.runtime.type.Iteratorable#keyIterator()
	 */
	public Iterator keyIterator() {
		return getComponentPro().keyIterator();
	}

	/**
	 * @see railo.runtime.type.Iteratorable#valueIterator()
	 */
	public Iterator valueIterator() {
		return getComponentPro().valueIterator();
	}

	/**
	 * @see railo.runtime.type.Iteratorable#iterator()
	 */
	public Iterator iterator() {
		return getComponentPro().iterator();
	}

	/**
	 * @see railo.runtime.op.Castable#castToString()
	 */
	public String castToString() throws PageException {
		return getComponentPro().castToString();
	}

	/**
	 * @see railo.runtime.op.Castable#castToString(java.lang.String)
	 */
	public String castToString(String defaultValue) {
		return getComponentPro().castToString(defaultValue);
	}

	/**
	 * @see railo.runtime.op.Castable#castToBooleanValue()
	 */
	public boolean castToBooleanValue() throws PageException {
		return getComponentPro().castToBooleanValue();
	}

	/**
	 * @see railo.runtime.op.Castable#castToBoolean(java.lang.Boolean)
	 */
	public Boolean castToBoolean(Boolean defaultValue) {
		return getComponentPro().castToBoolean(defaultValue);
	}

	/**
	 * @see railo.runtime.op.Castable#castToDoubleValue()
	 */
	public double castToDoubleValue() throws PageException {
		return getComponentPro().castToDoubleValue();
	}

	/**
	 * @see railo.runtime.op.Castable#castToDoubleValue(double)
	 */
	public double castToDoubleValue(double defaultValue) {
		return getComponentPro().castToDoubleValue(defaultValue);
	}

	/**
	 * @see railo.runtime.op.Castable#castToDateTime()
	 */
	public DateTime castToDateTime() throws PageException {
		return getComponentPro().castToDateTime();
	}

	/**
	 * @see railo.runtime.op.Castable#castToDateTime(railo.runtime.type.dt.DateTime)
	 */
	public DateTime castToDateTime(DateTime defaultValue) {
		return getComponentPro().castToDateTime(defaultValue);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(java.lang.String)
	 */
	public int compareTo(String str) throws PageException {
		return getComponentPro().compareTo(str);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(boolean)
	 */
	public int compareTo(boolean b) throws PageException {
		return getComponentPro().compareTo(b);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(double)
	 */
	public int compareTo(double d) throws PageException {
		return getComponentPro().compareTo(d);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(railo.runtime.type.dt.DateTime)
	 */
	public int compareTo(DateTime dt) throws PageException {
		return getComponentPro().compareTo(dt);
	}

	/**
	 * @see java.util.Map#containsKey(java.lang.Object)
	 */
	public boolean containsKey(Object key) {
		return getComponentPro().containsKey(key);
	}

	/**
	 * @see java.util.Map#containsValue(java.lang.Object)
	 */
	public boolean containsValue(Object value) {
		return getComponentPro().containsValue(value);
	}

	/**
	 * @see java.util.Map#entrySet()
	 */
	public Set entrySet() {
		return getComponentPro().entrySet();
	}

	/**
	 * @see java.util.Map#get(java.lang.Object)
	 */
	public Object get(Object key) {
		return getComponentPro().get(key);
	}

	/**
	 * @see java.util.Map#isEmpty()
	 */
	public boolean isEmpty() {
		return getComponentPro().isEmpty();
	}

	/**
	 * @see java.util.Map#keySet()
	 */
	public Set keySet() {
		return getComponentPro().keySet();
	}

	/**
	 * @see java.util.Map#put(java.lang.Object, java.lang.Object)
	 */
	public Object put(Object key, Object value) {
		return getComponentPro().put(key, value);
	}

	/**
	 * @see java.util.Map#putAll(java.util.Map)
	 */
	public void putAll(Map m) {
		getComponentPro().putAll(m);
	}

	/**
	 * @see java.util.Map#remove(java.lang.Object)
	 */
	public Object remove(Object key) {
		return getComponentPro().remove(key);
	}

	/**
	 * @see java.util.Map#values()
	 */
	public java.util.Collection values() {
		return getComponentPro().values();
	}

	/**
	 * @see railo.runtime.type.Objects#get(railo.runtime.PageContext, java.lang.String, java.lang.Object)
	 */
	public Object get(PageContext pc, String key, Object defaultValue) {
		return getComponentPro().get(pc, key, defaultValue);
	}

	/**
	 * @see railo.runtime.type.Objects#get(railo.runtime.PageContext, railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object get(PageContext pc, Key key, Object defaultValue) {
		return getComponentPro().get(pc, key, defaultValue);
	}

	/**
	 * @see railo.runtime.type.Objects#get(railo.runtime.PageContext, java.lang.String)
	 */
	public Object get(PageContext pc, String key) throws PageException {
		return getComponentPro().get(pc, key);
	}

	/**
	 * @see railo.runtime.type.Objects#get(railo.runtime.PageContext, railo.runtime.type.Collection.Key)
	 */
	public Object get(PageContext pc, Key key) throws PageException {
		return getComponentPro().get(pc, key);
	}

	/**
	 * @see railo.runtime.type.Objects#set(railo.runtime.PageContext, java.lang.String, java.lang.Object)
	 */
	public Object set(PageContext pc, String propertyName, Object value)
			throws PageException {
		return getComponentPro().set(pc, propertyName, value);
	}

	/**
	 * @see railo.runtime.type.Objects#set(railo.runtime.PageContext, railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object set(PageContext pc, Key propertyName, Object value)
			throws PageException {
		return getComponentPro().set(pc, propertyName, value);
	}

	/**
	 * @see railo.runtime.type.Objects#setEL(railo.runtime.PageContext, java.lang.String, java.lang.Object)
	 */
	public Object setEL(PageContext pc, String propertyName, Object value) {
		return getComponentPro().setEL(pc, propertyName, value);
	}

	/**
	 * @see railo.runtime.type.Objects#setEL(railo.runtime.PageContext, railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object setEL(PageContext pc, Key propertyName, Object value) {
		return getComponentPro().setEL(pc, propertyName, value);
	}

	/**
	 * @see railo.runtime.type.Objects#call(railo.runtime.PageContext, railo.runtime.type.Collection.Key, java.lang.Object[])
	 */
	public Object call(PageContext pc, Key methodName, Object[] arguments)
			throws PageException {
		return getComponentPro().call(pc, methodName, arguments);
	}

	/**
	 * @see railo.runtime.type.Objects#callWithNamedValues(railo.runtime.PageContext, railo.runtime.type.Collection.Key, railo.runtime.type.Struct)
	 */
	public Object callWithNamedValues(PageContext pc, Key methodName,
			Struct args) throws PageException {
		return getComponentPro().callWithNamedValues(pc, methodName, args);
	}

	/**
	 * @see railo.runtime.type.Objects#isInitalized()
	 */
	public boolean isInitalized() {
		return getComponentPro().isInitalized();
	}

	/**
	 * @see railo.runtime.ComponentPro#getProperties(boolean)
	 */
	public Property[] getProperties(boolean onlyPeristent) {
		return getComponentPro().getProperties(onlyPeristent);
	}
	
	/**
	 * @see railo.runtime.ComponentPro#getProperties(boolean,boolean)
	 */
	public Property[] getProperties(boolean onlyPeristent, boolean includeSuper) {
		return getComponentPro().getProperties(onlyPeristent,includeSuper);
	}

	/**
	 * @see railo.runtime.ComponentPro#setProperty(railo.runtime.component.Property)
	 */
	public void setProperty(Property property) throws PageException {
		getComponentPro().setProperty(property);
	}

	/**
	 * @see railo.runtime.ComponentPro#getComponentScope()
	 */
	public ComponentScope getComponentScope() {
		return getComponentPro().getComponentScope();
	}

	/**
	 * @see railo.runtime.ComponentPro#contains(railo.runtime.PageContext, railo.runtime.type.Collection.Key)
	 */
	public boolean contains(PageContext pc, Key key) {
		return getComponentPro().contains(pc, key);
	}

	/**
	 * @see railo.runtime.ComponentPro#getPageSource()
	 */
	public PageSource getPageSource() {
		return getComponentPro().getPageSource();
	}

	/**
	 * @see railo.runtime.ComponentPro#getBaseAbsName()
	 */
	public String getBaseAbsName() {
		return getComponentPro().getBaseAbsName();
	}

	/**
	 * @see railo.runtime.ComponentPro#isBasePeristent()
	 */
	public boolean isBasePeristent() {
		return getComponentPro().isBasePeristent();
	}

	/**
	 * @see railo.runtime.ComponentPro#equalTo(java.lang.String)
	 */
	public boolean equalTo(String type) {
		return getComponentPro().equalTo(type);
	}
	
	/**
	 * @see java.lang.Object#clone()
	 */
	public Object clone(){
		return duplicate(true);
	}
}
