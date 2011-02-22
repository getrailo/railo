package railo.runtime;//.orm.hibernate.tuplizer.proxy;


import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;

import railo.commons.lang.types.RefBoolean;
import railo.runtime.component.Member;
import railo.runtime.component.Property;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.exp.PageException;
import railo.runtime.orm.hibernate.tuplizer.proxy.CFCLazyInitializer;
import railo.runtime.type.Struct;
import railo.runtime.type.UDF;
import railo.runtime.type.UDFImpl;
import railo.runtime.type.UDFProperties;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.scope.Variables;

/**
 * Proxy for "dynamic-map" entity representations.
 *
 * @author Gavin King
 */
// MUST implements ComponentPro instead of ComponentImpl
public class CFCProxy extends ComponentImpl implements HibernateProxy, Serializable {

	private CFCLazyInitializer li;

	public CFCProxy(CFCLazyInitializer li) {
		this.li = li;
	}

	public Object writeReplace() {
		return this;
	}

	public LazyInitializer getHibernateLazyInitializer() {
		return li;
	}

	/**
	 * @see railo.runtime.ComponentImpl#_getName()
	 */
	@Override
	public String _getName() {
		return li.getCFC()._getName();
	}

	/**
	 * @see railo.runtime.ComponentImpl#addConstructorUDF(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	@Override
	public void addConstructorUDF(Key key, UDF value) {
		 li.getCFC().addConstructorUDF(key, value);
	}

	/**
	 * @see railo.runtime.ComponentImpl#afterCall(railo.runtime.PageContext, railo.runtime.type.scope.Variables)
	 */
	@Override
	public void afterCall(PageContext pc, Variables parent) {
		li.getCFC().afterCall(pc, parent);
	}

	/**
	 * @see railo.runtime.ComponentImpl#afterConstructor(railo.runtime.PageContext, railo.runtime.type.scope.Variables)
	 */
	@Override
	public void afterConstructor(PageContext pc, Variables parent) {
		li.getCFC().afterConstructor(pc, parent);
	}

	/**
	 * @see railo.runtime.ComponentImpl#beforeCall(railo.runtime.PageContext)
	 */
	@Override
	public Variables beforeCall(PageContext pc) {
		return li.getCFC().beforeCall(pc);
	}

	/**
	 * @see railo.runtime.ComponentImpl#call(railo.runtime.PageContext, java.lang.String, java.lang.Object[])
	 */
	@Override
	public Object call(PageContext pc, String name, Object[] args)
			throws PageException {
		return li.getCFC().call(pc, name, args);
	}

	/**
	 * @see railo.runtime.ComponentImpl#call(railo.runtime.PageContext, railo.runtime.type.Collection.Key, java.lang.Object[])
	 */
	@Override
	public Object call(PageContext pc, Key name, Object[] args)
			throws PageException {
		return li.getCFC().call(pc, name, args);
	}

	/**
	 * @see railo.runtime.ComponentImpl#call(railo.runtime.PageContext, int, java.lang.String, java.lang.Object[])
	 */
	@Override
	protected Object call(PageContext pc, int access, String name, Object[] args)
			throws PageException {
		return li.getCFC().call(pc, access, name, args);
	}

	/**
	 * @see railo.runtime.ComponentImpl#call(railo.runtime.PageContext, int, railo.runtime.type.Collection.Key, java.lang.Object[])
	 */
	@Override
	protected Object call(PageContext pc, int access, Key name, Object[] args)
			throws PageException {
		return li.getCFC().call(pc, access, name, args);
	}

	/**
	 * @see railo.runtime.ComponentImpl#callWithNamedValues(railo.runtime.PageContext, java.lang.String, railo.runtime.type.Struct)
	 */
	@Override
	public Object callWithNamedValues(PageContext pc, String name, Struct args)
			throws PageException {
		return li.getCFC().callWithNamedValues(pc, name, args);
	}

	/**
	 * @see railo.runtime.ComponentImpl#callWithNamedValues(railo.runtime.PageContext, railo.runtime.type.Collection.Key, railo.runtime.type.Struct)
	 */
	@Override
	public Object callWithNamedValues(PageContext pc, Key methodName,
			Struct args) throws PageException {
		return li.getCFC().callWithNamedValues(pc, methodName, args);
	}

	/**
	 * @see railo.runtime.ComponentImpl#callWithNamedValues(railo.runtime.PageContext, int, java.lang.String, railo.runtime.type.Struct)
	 */
	@Override
	protected Object callWithNamedValues(PageContext pc, int access,
			String name, Struct args) throws PageException {
		return li.getCFC().callWithNamedValues(pc, access, name, args);
	}

	/**
	 * @see railo.runtime.ComponentImpl#callWithNamedValues(railo.runtime.PageContext, int, railo.runtime.type.Collection.Key, railo.runtime.type.Struct)
	 */
	@Override
	protected Object callWithNamedValues(PageContext pc, int access, Key name,
			Struct args) throws PageException {
		return li.getCFC().callWithNamedValues(pc, access, name, args);
	}

	/**
	 * @see railo.runtime.ComponentImpl#castToBoolean(java.lang.Boolean)
	 */
	@Override
	public Boolean castToBoolean(Boolean defaultValue) {
		return li.getCFC().castToBoolean(defaultValue);
	}

	/**
	 * @see railo.runtime.ComponentImpl#castToBooleanValue()
	 */
	@Override
	public boolean castToBooleanValue() throws PageException {
		return li.getCFC().castToBooleanValue();
	}

	/**
	 * @see railo.runtime.ComponentImpl#castToDateTime()
	 */
	@Override
	public DateTime castToDateTime() throws PageException {
		return li.getCFC().castToDateTime();
	}

	/**
	 * @see railo.runtime.ComponentImpl#castToDateTime(railo.runtime.type.dt.DateTime)
	 */
	@Override
	public DateTime castToDateTime(DateTime defaultValue) {
		return li.getCFC().castToDateTime(defaultValue);
	}

	/**
	 * @see railo.runtime.ComponentImpl#castToDoubleValue()
	 */
	@Override
	public double castToDoubleValue() throws PageException {
		return li.getCFC().castToDoubleValue();
	}

	/**
	 * @see railo.runtime.ComponentImpl#castToDoubleValue(double)
	 */
	@Override
	public double castToDoubleValue(double defaultValue) {
		return li.getCFC().castToDoubleValue(defaultValue);
	}

	/**
	 * @see railo.runtime.ComponentImpl#castToString()
	 */
	@Override
	public String castToString() throws PageException {
		return li.getCFC().castToString();
	}

	/**
	 * @see railo.runtime.ComponentImpl#castToString(java.lang.String)
	 */
	@Override
	public String castToString(String defaultValue) {
		return li.getCFC().castToString(defaultValue);
	}

	/**
	 * @see railo.runtime.ComponentImpl#checkInterface(railo.runtime.PageContext, railo.runtime.ComponentPage)
	 */
	@Override
	public void checkInterface(PageContext pc, ComponentPage componentPage)
			throws PageException {
		li.getCFC().checkInterface(pc, componentPage);
	}

	/**
	 * @see railo.runtime.ComponentImpl#clear()
	 */
	@Override
	public void clear() {
		li.getCFC().clear();
	}

	/**
	 * @see railo.runtime.ComponentImpl#compareTo(boolean)
	 */
	@Override
	public int compareTo(boolean b) throws PageException {
		return li.getCFC().compareTo(b);
	}

	/**
	 * @see railo.runtime.ComponentImpl#compareTo(railo.runtime.type.dt.DateTime)
	 */
	@Override
	public int compareTo(DateTime dt) throws PageException {
		return li.getCFC().compareTo(dt);
	}

	/**
	 * @see railo.runtime.ComponentImpl#compareTo(double)
	 */
	@Override
	public int compareTo(double d) throws PageException {
		return li.getCFC().compareTo(d);
	}

	/**
	 * @see railo.runtime.ComponentImpl#compareTo(java.lang.String)
	 */
	@Override
	public int compareTo(String str) throws PageException {
		return li.getCFC().compareTo(str);
	}

	/**
	 * @see railo.runtime.ComponentImpl#contains(railo.runtime.PageContext, java.lang.String)
	 */
	@Override
	public boolean contains(PageContext pc, String name) {
		return li.getCFC().contains(pc, name);
	}

	/**
	 * @see railo.runtime.ComponentImpl#contains(railo.runtime.PageContext, railo.runtime.type.Collection.Key)
	 */
	@Override
	public boolean contains(PageContext pc, Key key) {
		return li.getCFC().contains(pc, key);
	}

	/**
	 * @see railo.runtime.ComponentImpl#contains(int, java.lang.String)
	 */
	@Override
	public boolean contains(int access, String name) {
		return li.getCFC().contains(access, name);
	}

	/**
	 * @see railo.runtime.ComponentImpl#containsKey(railo.runtime.type.Collection.Key)
	 */
	@Override
	public boolean containsKey(Key key) {
		return li.getCFC().containsKey(key);
	}

	/**
	 * @see railo.runtime.ComponentImpl#duplicate(boolean)
	 */
	@Override
	public synchronized railo.runtime.type.Collection duplicate(boolean deepCopy) {
		return li.getCFC().duplicate(deepCopy);
	}

	/**
	 * @see railo.runtime.ComponentImpl#get(railo.runtime.PageContext, java.lang.String)
	 */
	@Override
	public Object get(PageContext pc, String name) throws PageException {
		return li.getCFC().get(pc, name);
	}

	/**
	 * @see railo.runtime.ComponentImpl#get(railo.runtime.PageContext, railo.runtime.type.Collection.Key)
	 */
	@Override
	public Object get(PageContext pc, Key key) throws PageException {
		return li.getCFC().get(pc, key);
	}

	/**
	 * @see railo.runtime.ComponentImpl#get(int, java.lang.String)
	 */
	@Override
	public Object get(int access, String name) throws PageException {
		return li.getCFC().get(access, name);
	}

	/**
	 * @see railo.runtime.ComponentImpl#get(int, railo.runtime.type.Collection.Key)
	 */
	@Override
	public Object get(int access, Key key) throws PageException {
		return li.getCFC().get(access, key);
	}

	/**
	 * @see railo.runtime.ComponentImpl#get(railo.runtime.PageContext, java.lang.String, java.lang.Object)
	 */
	@Override
	public Object get(PageContext pc, String name, Object defaultValue) {
		return li.getCFC().get(pc, name, defaultValue);
	}

	/**
	 * @see railo.runtime.ComponentImpl#get(railo.runtime.PageContext, railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	@Override
	public Object get(PageContext pc, Key key, Object defaultValue) {
		return li.getCFC().get(pc, key, defaultValue);
	}

	/**
	 * @see railo.runtime.ComponentImpl#get(int, java.lang.String, java.lang.Object)
	 */
	@Override
	protected Object get(int access, String name, Object defaultValue) {
		return li.getCFC().get(access, name, defaultValue);
	}

	/**
	 * @see railo.runtime.ComponentImpl#get(int, railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	@Override
	protected Object get(int access, Key key, Object defaultValue) {
		return li.getCFC().get(access, key, defaultValue);
	}

	/**
	 * @see railo.runtime.ComponentImpl#get(railo.runtime.type.Collection.Key)
	 */
	@Override
	public Object get(Key key) throws PageException {
		return li.getCFC().get(key);
	}

	/**
	 * @see railo.runtime.ComponentImpl#get(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	@Override
	public Object get(Key key, Object defaultValue) {
		return li.getCFC().get(key, defaultValue);
	}

	/**
	 * @see railo.runtime.ComponentImpl#getAbsName()
	 */
	@Override
	public String getAbsName() {
		return li.getCFC().getAbsName();
	}

	/**
	 * @see railo.runtime.ComponentImpl#getCallName()
	 */
	@Override
	public String getCallName() {
		return li.getCFC().getCallName();
	}

	/**
	 * @see railo.runtime.ComponentImpl#getCallPath()
	 */
	@Override
	protected String getCallPath() {
		return li.getCFC().getCallPath();
	}

	/**
	 * @see railo.runtime.ComponentImpl#getComponentScope()
	 */
	@Override
	public ComponentScope getComponentScope() {
		return li.getCFC().getComponentScope();
	}


	/**
	 * @see railo.runtime.ComponentImpl#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return li.getCFC().getDisplayName();
	}

	/**
	 * @see railo.runtime.ComponentImpl#getExtends()
	 */
	@Override
	public String getExtends() {
		return li.getCFC().getExtends();
	}

	/**
	 * @see railo.runtime.ComponentImpl#getHint()
	 */
	@Override
	public String getHint() {
		return li.getCFC().getHint();
	}

	/**
	 * @see railo.runtime.ComponentImpl#getJavaAccessClass(railo.commons.lang.types.RefBoolean)
	 */
	@Override
	public Class getJavaAccessClass(RefBoolean isNew) throws PageException {
		return li.getCFC().getJavaAccessClass(isNew);
	}

	/**
	 * @see railo.runtime.ComponentImpl#getJavaAccessClass(railo.commons.lang.types.RefBoolean, boolean, boolean, boolean)
	 */
	@Override
	public Class getJavaAccessClass(RefBoolean isNew, boolean writeLog,
			boolean takeTop, boolean create) throws PageException {
		return li.getCFC().getJavaAccessClass(isNew, writeLog, takeTop, create);
	}

	/**
	 * @see railo.runtime.ComponentImpl#getMember(int, railo.runtime.type.Collection.Key, boolean, boolean)
	 */
	@Override
	public Member getMember(int access, Key key, boolean dataMember,
			boolean superAccess) {
		return li.getCFC().getMember(access, key, dataMember, superAccess);
	}

	/**
	 * @see railo.runtime.ComponentImpl#getMember(railo.runtime.PageContext, railo.runtime.type.Collection.Key, boolean, boolean)
	 */
	@Override
	protected Member getMember(PageContext pc, Key key, boolean dataMember,
			boolean superAccess) {
		return li.getCFC().getMember(pc, key, dataMember, superAccess);
	}

	/**
	 * @see railo.runtime.ComponentImpl#getMembers(int)
	 */
	@Override
	protected List getMembers(int access) {
		return li.getCFC().getMembers(access);
	}

	/**
	 * @see railo.runtime.ComponentImpl#getMetaData(railo.runtime.PageContext)
	 */
	@Override
	public synchronized Struct getMetaData(PageContext pc) throws PageException {
		return li.getCFC().getMetaData(pc);
	}

	/**
	 * @see railo.runtime.ComponentImpl#getName()
	 */
	@Override
	public String getName() {
		return li.getCFC().getName();
	}

	/**
	 * @see railo.runtime.ComponentImpl#getOutput()
	 */
	@Override
	public boolean getOutput() {
		return li.getCFC().getOutput();
	}

	/**
	 * @see railo.runtime.ComponentImpl#getPage()
	 */
	@Override
	public Page getPage() {
		return li.getCFC().getPage();
	}

	/**
	 * @see railo.runtime.ComponentImpl#getProperties()
	 */
	@Override
	public Property[] getProperties(boolean onlyPeristent) {
		return li.getCFC().getProperties(onlyPeristent);
	}

	/**
	 * @see railo.runtime.ComponentImpl#getWSDLFile()
	 */
	@Override
	public String getWSDLFile() {
		return li.getCFC().getWSDLFile();
	}

	/**
	 * @see railo.runtime.ComponentImpl#init(railo.runtime.PageContext, railo.runtime.ComponentPage)
	 */
	@Override
	public void init(PageContext pageContext, ComponentPage componentPage)
			throws PageException {
		li.getCFC().init(pageContext, componentPage);
	}

	/**
	 * @see railo.runtime.ComponentImpl#instanceOf(java.lang.String)
	 */
	@Override
	public boolean instanceOf(String type) {
		return li.getCFC().instanceOf(type);
	}

	/**
	 * @see railo.runtime.ComponentImpl#isInitalized()
	 */
	@Override
	public boolean isInitalized() {
		return li.getCFC().isInitalized();
	}

	/**
	 * @see railo.runtime.ComponentImpl#isValidAccess(int)
	 */
	@Override
	public boolean isValidAccess(int access) {
		return li.getCFC().isValidAccess(access);
	}

	/**
	 * @see railo.runtime.ComponentImpl#iterator(int)
	 */
	@Override
	protected Iterator iterator(int access) {
		return li.getCFC().iterator(access);
	}

	/**
	 * @see railo.runtime.ComponentImpl#keyIterator()
	 */
	@Override
	public Iterator keyIterator() {
		return li.getCFC().keyIterator();
	}

	/**
	 * @see railo.runtime.ComponentImpl#keySet(int)
	 */
	@Override
	protected Set keySet(int access) {
		return li.getCFC().keySet(access);
	}

	/**
	 * @see railo.runtime.ComponentImpl#keys(int)
	 */
	@Override
	public Key[] keys(int access) {
		return li.getCFC().keys(access);
	}

	/**
	 * @see railo.runtime.ComponentImpl#keys()
	 */
	@Override
	public Key[] keys() {
		return li.getCFC().keys();
	}

	/**
	 * @see railo.runtime.ComponentImpl#keysAsString(int)
	 */
	@Override
	public String[] keysAsString(int access) {
		return li.getCFC().keysAsString(access);
	}

	/**
	 * @see railo.runtime.ComponentImpl#keysAsString()
	 */
	@Override
	public String[] keysAsString() {
		return li.getCFC().keysAsString();
	}

	/**
	 * @see railo.runtime.ComponentImpl#onMissingMethod(railo.runtime.PageContext, int, railo.runtime.component.Member, java.lang.String, java.lang.Object[], railo.runtime.type.Struct, boolean)
	 */
	@Override
	public Object onMissingMethod(PageContext pc, int access, Member member,
			String name, Object[] _args, Struct args, boolean superAccess)
			throws PageException {
		// TODO Auto-generated method stub
		return li.getCFC().onMissingMethod(pc, access, member, name, _args, args, superAccess);
	}

	/**
	 * @see railo.runtime.ComponentImpl#readExternal(java.io.ObjectInput)
	 */
	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		li.getCFC().readExternal(in);
	}

	/**
	 * @see railo.runtime.ComponentImpl#registerUDF(java.lang.String, railo.runtime.type.UDF)
	 */
	@Override
	public void registerUDF(String key, UDF udf) {
		li.getCFC().registerUDF(key, udf);
	}

	/**
	 * @see railo.runtime.ComponentImpl#registerUDF(java.lang.String, railo.runtime.type.UDFProperties)
	 */
	@Override
	public void registerUDF(String key, UDFProperties prop) {
		li.getCFC().registerUDF(key, prop);
	}

	/**
	 * @see railo.runtime.ComponentImpl#registerUDF(railo.runtime.type.Collection.Key, railo.runtime.type.UDF)
	 */
	@Override
	public void registerUDF(Key key, UDF udf) {
		li.getCFC().registerUDF(key, udf);
	}

	/**
	 * @see railo.runtime.ComponentImpl#registerUDF(railo.runtime.type.Collection.Key, railo.runtime.type.UDFProperties)
	 */
	@Override
	public void registerUDF(Key key, UDFProperties prop) {
		li.getCFC().registerUDF(key, prop);
	}

	/**
	 * @see railo.runtime.ComponentImpl#registerUDF(railo.runtime.type.Collection.Key, railo.runtime.type.UDFImpl, boolean, boolean)
	 */
	@Override
	public void registerUDF(Key key, UDFImpl udf, boolean useShadow,
			boolean injected) {
		li.getCFC().registerUDF(key, udf, useShadow, injected);
	}

	/**
	 * @see railo.runtime.ComponentImpl#remove(railo.runtime.type.Collection.Key)
	 */
	@Override
	public Object remove(Key key) throws PageException {
		return li.getCFC().remove(key);
	}

	/**
	 * @see railo.runtime.ComponentImpl#removeEL(railo.runtime.type.Collection.Key)
	 */
	@Override
	public Object removeEL(Key key) {
		return li.getCFC().removeEL(key);
	}

	/**
	 * @see railo.runtime.ComponentImpl#set(railo.runtime.PageContext, java.lang.String, java.lang.Object)
	 */
	@Override
	public Object set(PageContext pc, String name, Object value)
			throws PageException {
		return li.getCFC().set(pc, name, value);
	}

	/**
	 * @see railo.runtime.ComponentImpl#set(railo.runtime.PageContext, railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	@Override
	public Object set(PageContext pc, Key key, Object value)
			throws PageException {
		return li.getCFC().set(pc, key, value);
	}

	/**
	 * @see railo.runtime.ComponentImpl#set(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	@Override
	public Object set(Key key, Object value) throws PageException {
		return li.getCFC().set(key, value);
	}

	/**
	 * @see railo.runtime.ComponentImpl#setEL(railo.runtime.PageContext, java.lang.String, java.lang.Object)
	 */
	@Override
	public Object setEL(PageContext pc, String name, Object value) {
		return li.getCFC().setEL(pc, name, value);
	}

	/**
	 * @see railo.runtime.ComponentImpl#setEL(railo.runtime.PageContext, railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	@Override
	public Object setEL(PageContext pc, Key name, Object value) {
		return li.getCFC().setEL(pc, name, value);
	}

	/**
	 * @see railo.runtime.ComponentImpl#setEL(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	@Override
	public Object setEL(Key key, Object value) {
		return li.getCFC().setEL(key, value);
	}

	/**
	 * @see railo.runtime.ComponentImpl#setInitalized(boolean)
	 */
	@Override
	public void setInitalized(boolean isInit) {
		li.getCFC().setInitalized(isInit);
	}

	/**
	 * @see railo.runtime.ComponentImpl#setProperty(railo.runtime.component.Property)
	 */
	@Override
	public void setProperty(Property property) {
		li.getCFC().setProperty(property);
	}

	/**
	 * @see railo.runtime.ComponentImpl#size(int)
	 */
	@Override
	protected int size(int access) {
		return li.getCFC().size(access);
	}

	/**
	 * @see railo.runtime.ComponentImpl#size()
	 */
	@Override
	public int size() {
		return li.getCFC().size();
	}

	/**
	 * @see railo.runtime.ComponentImpl#sizeOf()
	 */
	@Override
	public long sizeOf() {
		return li.getCFC().sizeOf();
	}

	/**
	 * @see railo.runtime.ComponentImpl#toDumpData(railo.runtime.PageContext, int, railo.runtime.dump.DumpProperties)
	 */
	@Override
	public DumpData toDumpData(PageContext pageContext, int maxlevel,
			DumpProperties dp) {
		return li.getCFC().toDumpData(pageContext, maxlevel, dp);
	}

	/**
	 * @see railo.runtime.ComponentImpl#toDumpData(railo.runtime.PageContext, int, railo.runtime.dump.DumpProperties, int)
	 */
	@Override
	public DumpData toDumpData(PageContext pageContext, int maxlevel,DumpProperties dp, int access) {
		return li.getCFC().toDumpData(pageContext, maxlevel, dp, access);
	}

	/**
	 * @see railo.runtime.ComponentImpl#udfKeySet(int)
	 */
	@Override
	protected Set udfKeySet(int access) {
		return li.getCFC().udfKeySet(access);
	}

	/**
	 * @see railo.runtime.ComponentImpl#writeExternal(java.io.ObjectOutput)
	 */
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		li.getCFC().writeExternal(out);
	}

	/**
	 * @see railo.runtime.type.util.StructSupport#containsValue(java.lang.Object)
	 */
	@Override
	public boolean containsValue(Object value) {
		return li.getCFC().containsValue(value);
	}

	/**
	 * @see railo.runtime.type.util.StructSupport#entrySet()
	 */
	@Override
	public Set entrySet() {
		return li.getCFC().entrySet();
	}

	/**
	 * @see railo.runtime.type.util.StructSupport#keySet()
	 */
	@Override
	public Set keySet() {
		return li.getCFC().keySet();
	}

	/**
	 * @see railo.runtime.type.util.StructSupport#toString()
	 */
	@Override
	public String toString() {
		return li.getCFC().toString();
	}

	/**
	 * @see railo.runtime.type.util.StructSupport#valueIterator()
	 */
	@Override
	public Iterator valueIterator() {
		return li.getCFC().valueIterator();
	}

	/**
	 * @see railo.runtime.type.util.StructSupport#values()
	 */
	@Override
	public Collection values() {
		return li.getCFC().values();
	}

	

}