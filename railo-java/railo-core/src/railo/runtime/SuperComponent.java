/**
 * 
 */
package railo.runtime;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import railo.commons.lang.types.RefBoolean;
import railo.runtime.component.DataMember;
import railo.runtime.component.Member;
import railo.runtime.component.MemberSupport;
import railo.runtime.component.Property;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.exp.PageException;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Sizeable;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.UDF;
import railo.runtime.type.UDFProperties;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.util.StructUtil;

/**
 * 
 */
public class SuperComponent extends MemberSupport implements Component, Member,Sizeable {
	
	private ComponentImpl comp;

	private SuperComponent(ComponentImpl comp) {
		super(Component.ACCESS_PRIVATE);
		this.comp=comp;
	}
	

	public static Member superMember(ComponentImpl comp) {
		if(comp==null) return new DataMember(Component.ACCESS_PRIVATE,new StructImpl());
        return new SuperComponent(comp);
	}
	public static Collection superInstance(ComponentImpl comp) {
		if(comp==null) return new StructImpl();
        return new SuperComponent(comp);
	}

	/**
	 * @see railo.runtime.component.Member#getValue()
	 */
	public Object getValue() {
		return this;
	}
	/**
	 *
	 * @see railo.runtime.ComponentImpl#call(railo.runtime.PageContext, java.lang.String, java.lang.Object[])
	 */
	public Object call(PageContext pc, String name, Object[] args) throws PageException {
		return comp._call(pc, getAccess(), KeyImpl.init(name), null, args,true);
	}

	/**
	 *
	 * @see railo.runtime.ComponentImpl#call(railo.runtime.PageContext, railo.runtime.type.Collection.Key, java.lang.Object[])
	 */
	public Object call(PageContext pc, Key name, Object[] args) throws PageException {
		return comp._call(pc, getAccess(), name, null, args,true);
	}

	/**
	 *
	 * @see railo.runtime.ComponentImpl#callWithNamedValues(railo.runtime.PageContext, java.lang.String, railo.runtime.type.Struct)
	 */
	public Object callWithNamedValues(PageContext pc, String name, Struct args) throws PageException {
		return comp._call(pc, getAccess(), KeyImpl.init(name), args,null,true);
	}

	/**
	 *
	 * @see railo.runtime.ComponentImpl#callWithNamedValues(railo.runtime.PageContext, railo.runtime.type.Collection.Key, railo.runtime.type.Struct)
	 */
	public Object callWithNamedValues(PageContext pc, Key methodName, Struct args) throws PageException {
		return comp._call(pc, getAccess(), methodName, args,null,true);
	}
	
	/**
	 *
	 * @see railo.runtime.ComponentImpl#castToBooleanValue()
	 */
	public boolean castToBooleanValue() throws PageException {
		return comp.castToBooleanValue(true);
	}
    
    /**
     * @see railo.runtime.op.Castable#castToBoolean(java.lang.Boolean)
     */
    public Boolean castToBoolean(Boolean defaultValue) {
        return comp.castToBoolean(true,defaultValue);
    }

	/**
	 *
	 * @see railo.runtime.ComponentImpl#castToDateTime()
	 */
	public DateTime castToDateTime() throws PageException {
		return comp.castToDateTime(true);
	}
    
    /**
     * @see railo.runtime.op.Castable#castToDateTime(railo.runtime.type.dt.DateTime)
     */
    public DateTime castToDateTime(DateTime defaultValue) {
        return comp.castToDateTime(true,defaultValue);
    }

	/**
	 *
	 * @see railo.runtime.ComponentImpl#castToDoubleValue()
	 */
	public double castToDoubleValue() throws PageException {
		return comp.castToDoubleValue(true);
	}
    
    /**
     * @see railo.runtime.op.Castable#castToDoubleValue(double)
     */
    public double castToDoubleValue(double defaultValue) {
        return comp.castToDoubleValue(true,defaultValue);
    }

	/**
	 *
	 * @see railo.runtime.ComponentImpl#castToString()
	 */
	public String castToString() throws PageException {
		return comp.castToString(true);
	}

	/**
	 * @see railo.runtime.op.Castable#castToString(java.lang.String)
	 */
	public String castToString(String defaultValue) {
		return comp.castToString(true,defaultValue);
	}

	/**
	 *
	 * @see railo.runtime.ComponentImpl#clear()
	 */
	public void clear() {
		comp.clear();
	}

	/**
	 *
	 * @see railo.runtime.Component#clone()
	 */
	public Object clone() {
		return duplicate(true);
	}

	/**
	 *
	 * @see railo.runtime.ComponentImpl#compareTo(boolean)
	 */
	public int compareTo(boolean b) throws PageException {
		return comp.compareTo(b);
	}

	/**
	 *
	 * @see railo.runtime.ComponentImpl#compareTo(railo.runtime.type.dt.DateTime)
	 */
	public int compareTo(DateTime dt) throws PageException {
		return comp.compareTo(dt);
	}

	/**
	 *
	 * @see railo.runtime.ComponentImpl#compareTo(double)
	 */
	public int compareTo(double d) throws PageException {
		return comp.compareTo(d);
	}

	/**
	 *
	 * @see railo.runtime.ComponentImpl#compareTo(java.lang.String)
	 */
	public int compareTo(String str) throws PageException {
		return comp.compareTo(str);
	}

	/**
	 *
	 * @see railo.runtime.ComponentImpl#containsKey(java.lang.String)
	 */
	public boolean containsKey(String name) {
		return comp.contains(getAccess(),(name));
	}

	/**
	 *
	 * @see railo.runtime.ComponentImpl#containsKey(railo.runtime.type.Collection.Key)
	 */
	public boolean containsKey(Key key) {
		return comp.contains(getAccess(),key.getLowerString());
	}

	/**
	 *
	 * @see railo.runtime.ComponentImpl#duplicate(boolean)
	 */
	public synchronized Collection duplicate(boolean deepCopy) {
		return new SuperComponent((ComponentImpl) comp.duplicate(deepCopy));
	}

	/**
	 *
	 * @see railo.runtime.ComponentImpl#get(railo.runtime.PageContext, railo.runtime.type.Collection.Key)
	 */
	public Object get(PageContext pc, Key key) throws PageException {
		return get(key);
	}

	/**
	 *
	 * @see railo.runtime.ComponentImpl#get(railo.runtime.PageContext, railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object get(PageContext pc, Key key, Object defaultValue) {
		return get(key, defaultValue);
	}
	

	/**
	 *
	 * @see railo.runtime.ComponentImpl#get(railo.runtime.PageContext, java.lang.String)
	 */
	public Object get(PageContext pc, String name) throws PageException {
		return get(KeyImpl.init(name));
	}

	/**
	 *
	 * @see railo.runtime.ComponentImpl#get(railo.runtime.PageContext, java.lang.String, java.lang.Object)
	 */
	public Object get(PageContext pc, String name, Object defaultValue) {
		return get(KeyImpl.init(name),defaultValue);
	}

	/**
	 *
	 * @see railo.runtime.ComponentImpl#get(java.lang.String)
	 */
	public Object get(String name) throws PageException {
		return get(KeyImpl.init(name));
	}

	/**
	 *
	 * @see railo.runtime.ComponentImpl#get(java.lang.String, java.lang.Object)
	 */
	public Object get(String name, Object defaultValue) {
		return get(KeyImpl.init(name), defaultValue);
	}

	/**
	 *
	 * @see railo.runtime.ComponentImpl#get(railo.runtime.type.Collection.Key)
	 */
	public Object get(Key key) throws PageException {
		Member member=comp.getMember(getAccess(),key,true,true);
        if(member!=null) return member.getValue();
        return comp.get(getAccess(), key);
	}

	/**
	 *
	 * @see railo.runtime.ComponentImpl#get(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object get(Key key, Object defaultValue) {
		Member member=getMember(getAccess(),key,true,true);
        if(member!=null) return member.getValue();
		return comp.get(getAccess(), key, defaultValue);
	}

	/**
	 *
	 * @see railo.runtime.ComponentImpl#getAbsName()
	 */
	public String getAbsName() {
		return comp.getAbsName();
	}
    
    /**
     * @see railo.runtime.Component#getBaseAbsName()
     */
    public String getBaseAbsName() {
        return comp.getBaseAbsName();
    }
    
    public boolean isBasePeristent() {
		return comp.isPersistent();
	}

	/**
	 *
	 * @see railo.runtime.ComponentImpl#getCallName()
	 */
	public String getCallName() {
		return comp.getCallName();
	}

	/**
	 *
	 * @see railo.runtime.ComponentImpl#getDisplayName()
	 */
	public String getDisplayName() {
		return comp.getDisplayName();
	}

	/**
	 *
	 * @see railo.runtime.ComponentImpl#getExtends()
	 */
	public String getExtends() {
		return comp.getExtends();
	}

	/**
	 *
	 * @see railo.runtime.ComponentImpl#getHint()
	 */
	public String getHint() {
		return comp.getHint();
	}

	/**
	 *
	 * @see railo.runtime.ComponentImpl#getJavaAccessClass(railo.commons.lang.types.RefBoolean)
	 */
	public Class getJavaAccessClass(RefBoolean isNew) throws PageException {
		return comp.getJavaAccessClass(isNew);
	}

	/**
	 *
	 * @see railo.runtime.ComponentImpl#getMetaData(railo.runtime.PageContext)
	 */
	public synchronized Struct getMetaData(PageContext pc) throws PageException {
		return comp.getMetaData(pc);
	}

	/**
	 *
	 * @see railo.runtime.ComponentImpl#getName()
	 */
	public String getName() {
		return comp.getName();
	}

	/**
	 *
	 * @see railo.runtime.ComponentImpl#getOutput()
	 */
	public boolean getOutput() {
		return comp.getOutput();
	}

	/**
	 *
	 * @see railo.runtime.ComponentImpl#instanceOf(java.lang.String)
	 */
	public boolean instanceOf(String type) {
		return comp.top.instanceOf(type);
	}

	/**
	 *
	 * @see railo.runtime.ComponentImpl#isInitalized()
	 */
	public boolean isInitalized() {
		return comp.top.isInitalized();
	}

	/**
	 *
	 * @see railo.runtime.ComponentImpl#isValidAccess(int)
	 */
	public boolean isValidAccess(int access) {
		return comp.isValidAccess(access);
	}

	/**
	 *
	 * @see railo.runtime.ComponentImpl#iterator()
	 */
	public Iterator iterator() {
		return comp.iterator();
	}

	/**
	 *
	 * @see railo.runtime.ComponentImpl#keyIterator()
	 */
	public Iterator<Collection.Key> keyIterator() {
		return comp.keyIterator(getAccess());
	}
	

	public Iterator<String> keysAsStringIterator() {
		return comp.keysAsStringIterator(getAccess());
	}
	
	@Override
	public Iterator<Entry<Key, Object>> entryIterator() {
		return comp.entryIterator(getAccess());
	}

	/**
	 *
	 * @see railo.runtime.ComponentImpl#keys()
	 */
	public Key[] keys() {
		return comp.keys(getAccess());
	}

	/**
	 *
	 * @see railo.runtime.ComponentImpl#remove(railo.runtime.type.Collection.Key)
	 */
	public Object remove(Key key) throws PageException {
		return comp.remove(key);
	}


	/**
	 *
	 * @see railo.runtime.ComponentImpl#removeEL(railo.runtime.type.Collection.Key)
	 */
	public Object removeEL(Key key) {
		return comp.removeEL(key);
	}

	/**
	 *
	 * @see railo.runtime.ComponentImpl#set(railo.runtime.PageContext, java.lang.String, java.lang.Object)
	 */
	public Object set(PageContext pc, String name, Object value) throws PageException {
		return comp.set(pc, name, value);
	}

	/**
	 *
	 * @see railo.runtime.ComponentImpl#set(railo.runtime.PageContext, railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object set(PageContext pc, Key key, Object value) throws PageException {
		return comp.set(pc, key, value);
	}

	/**
	 *
	 * @see railo.runtime.ComponentImpl#set(java.lang.String, java.lang.Object)
	 */
	public Object set(String name, Object value) throws PageException {
		return comp.set(name, value);
	}

	/**
	 *
	 * @see railo.runtime.ComponentImpl#set(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object set(Key key, Object value) throws PageException {
		return comp.set(key, value);
	}

	/**
	 *
	 * @see railo.runtime.ComponentImpl#setEL(railo.runtime.PageContext, java.lang.String, java.lang.Object)
	 */
	public Object setEL(PageContext pc, String name, Object value) {
		return comp.setEL(pc, name, value);
	}

	/**
	 *
	 * @see railo.runtime.ComponentImpl#setEL(railo.runtime.PageContext, railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object setEL(PageContext pc, Key name, Object value) {
		return comp.setEL(pc, name, value);
	}

	/**
	 *
	 * @see railo.runtime.ComponentImpl#setEL(java.lang.String, java.lang.Object)
	 */
	public Object setEL(String name, Object value) {
		return comp.setEL(name, value);
	}

	/**
	 *
	 * @see railo.runtime.ComponentImpl#setEL(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object setEL(Key key, Object value) {
		return comp.setEL(key, value);
	}

	/**
	 *
	 * @see railo.runtime.ComponentImpl#size()
	 */
	public int size() {
		return comp.size(getAccess());
	}

	/**
	 *
	 * @see railo.runtime.ComponentImpl#toDumpData(railo.runtime.PageContext, int)
	 */
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
		return comp.top.toDumpData(pageContext, maxlevel, dp);
	}


	public Page getPage() {
		return comp.getPage();
	}
	
	/**
	 * @see railo.runtime.Component#getPageSource()
	 */
	public PageSource getPageSource() {
		return comp.getPageSource();
	}


	/**
	 * @see java.util.Map#containsKey(java.lang.Object)
	 */
	public boolean containsKey(Object key) {
		return containsKey(KeyImpl.toKey(key, null));
	}


	/**
	 * @see java.util.Map#entrySet()
	 */
	public Set entrySet() {
		return StructUtil.entrySet(this);
	}


	/**
	 * @see java.util.Map#get(java.lang.Object)
	 */
	public Object get(Object key) {
		return get(KeyImpl.toKey(key, null), null);
	}


	/**
	 * @see java.util.Map#isEmpty()
	 */
	public boolean isEmpty() {
		return size()==0;
	}

	/**
	 * @see java.util.Map#keySet()
	 */
	public Set keySet() {
		return StructUtil.keySet(this);
	}


	/**
	 * @see java.util.Map#put(K, V)
	 */
	public Object put(Object key, Object value) {
		return setEL(KeyImpl.toKey(key, null), value);
	}

	/**
	 * @see java.util.Map#putAll(java.util.Map)
	 */
	public void putAll(Map map) {
		StructUtil.putAll(this, map);
	}

	/**
	 * @see java.util.Map#remove(java.lang.Object)
	 */
	public Object remove(Object key) {
		return removeEL(KeyImpl.toKey(key, null));
	}

	/**
	 * @see java.util.Map#values()
	 */
	public java.util.Collection values() {
		return StructUtil.values(this);
	}

	/**
	 * @see java.util.Map#containsValue(java.lang.Object)
	 */
	public boolean containsValue(Object value) {
		return values().contains(value);
	}


	public Iterator valueIterator() {
		return comp.valueIterator();
	}

	/**
	 * @see railo.runtime.Component#getProperties()
	 */
	public Property[] getProperties(boolean onlyPeristent) {
		return comp.getProperties(onlyPeristent);
	}

	@Override
	public HashMap<String, Property> getAllPersistentProperties()
	{
		return comp.getAllPersistentProperties();
	}

	/**
	 * @see railo.runtime.Component#getComponentScope()
	 */
	public ComponentScope getComponentScope() {
		return comp.getComponentScope();
	}

	/**
	 * @see railo.runtime.Component#contains(railo.runtime.PageContext, railo.runtime.type.Collection.Key)
	 */
	public boolean contains(PageContext pc, Key key) {
		return comp.contains(getAccess(),key);
	}

	/**
	 * @see railo.runtime.Component#getMember(int, railo.runtime.type.Collection.Key, boolean, boolean)
	 */
	public Member getMember(int access, Key key, boolean dataMember,boolean superAccess) {
		return comp.getMember(access, key, dataMember, superAccess);
	}

	/**
	 * @see railo.runtime.Component#setProperty(railo.runtime.component.Property)
	 */
	public void setProperty(Property property) throws PageException {
		comp.setProperty(property);
	}


	/**
	 * @see railo.runtime.type.Sizeable#sizeOf()
	 */
	public long sizeOf() {
		return StructUtil.sizeOf(this);
	}


	public boolean equalTo(String type) {
		return comp.top.equalTo(type);
	}

	/**
	 * @see railo.runtime.Component#getWSDLFile()
	 */
	public String getWSDLFile() {
		return comp.getWSDLFile();
	}
	
	@Override
    public void registerUDF(String key, UDF udf){
    	comp.registerUDF(key, udf);
    }
    
	@Override
    public void registerUDF(Collection.Key key, UDF udf){
		comp.registerUDF(key, udf);
    }
    
	@Override
    public void registerUDF(String key, UDFProperties props){
		comp.registerUDF(key, props);
    }
    
	@Override
    public void registerUDF(Collection.Key key, UDFProperties props){
		comp.registerUDF(key, props);
    }
	
}
