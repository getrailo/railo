/**
 * 
 */
package railo.runtime;

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
import railo.runtime.op.Duplicator;
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
public class SuperComponent extends MemberSupport implements ComponentPro, Member,Sizeable {
	
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

	@Override
	public Object getValue() {
		return this;
	}
	@Override
	public Object call(PageContext pc, String name, Object[] args) throws PageException {
		return comp._call(pc, getAccess(), KeyImpl.init(name), null, args,true);
	}

	@Override
	public Object call(PageContext pc, Key name, Object[] args) throws PageException {
		return comp._call(pc, getAccess(), name, null, args,true);
	}

	@Override
	public Object callWithNamedValues(PageContext pc, String name, Struct args) throws PageException {
		return comp._call(pc, getAccess(), KeyImpl.init(name), args,null,true);
	}

	@Override
	public Object callWithNamedValues(PageContext pc, Key methodName, Struct args) throws PageException {
		return comp._call(pc, getAccess(), methodName, args,null,true);
	}
	
	@Override
	public boolean castToBooleanValue() throws PageException {
		return comp.castToBooleanValue(true);
	}
    
    @Override
    public Boolean castToBoolean(Boolean defaultValue) {
        return comp.castToBoolean(true,defaultValue);
    }

	@Override
	public DateTime castToDateTime() throws PageException {
		return comp.castToDateTime(true);
	}
    
    @Override
    public DateTime castToDateTime(DateTime defaultValue) {
        return comp.castToDateTime(true,defaultValue);
    }

	@Override
	public double castToDoubleValue() throws PageException {
		return comp.castToDoubleValue(true);
	}
    
    @Override
    public double castToDoubleValue(double defaultValue) {
        return comp.castToDoubleValue(true,defaultValue);
    }

	@Override
	public String castToString() throws PageException {
		return comp.castToString(true);
	}

	@Override
	public String castToString(String defaultValue) {
		return comp.castToString(true,defaultValue);
	}

	@Override
	public void clear() {
		comp.clear();
	}

	@Override
	public Object clone() {
		return duplicate(true);
	}

	@Override
	public int compareTo(boolean b) throws PageException {
		return comp.compareTo(b);
	}

	@Override
	public int compareTo(DateTime dt) throws PageException {
		return comp.compareTo(dt);
	}

	@Override
	public int compareTo(double d) throws PageException {
		return comp.compareTo(d);
	}

	@Override
	public int compareTo(String str) throws PageException {
		return comp.compareTo(str);
	}

	@Override
	public boolean containsKey(String name) {
		return comp.contains(getAccess(),(name));
	}

	@Override
	public boolean containsKey(Key key) {
		return comp.contains(getAccess(),key.getLowerString());
	}

	@Override
	public synchronized Collection duplicate(boolean deepCopy) {
		return new SuperComponent((ComponentImpl) Duplicator.duplicate(comp,deepCopy));
	}

	@Override
	public Object get(PageContext pc, Key key) throws PageException {
		return get(key);
	}

	@Override
	public Object get(PageContext pc, Key key, Object defaultValue) {
		return get(key, defaultValue);
	}

	@Override
	public Object get(String name) throws PageException {
		return get(KeyImpl.init(name));
	}

	@Override
	public Object get(String name, Object defaultValue) {
		return get(KeyImpl.init(name), defaultValue);
	}

	@Override
	public Object get(Key key) throws PageException {
		Member member=comp.getMember(getAccess(),key,true,true);
        if(member!=null) return member.getValue();
        return comp.get(getAccess(), key);
	}

	@Override
	public Object get(Key key, Object defaultValue) {
		Member member=comp.getMember(getAccess(),key,true,true);
        if(member!=null) return member.getValue();
		return comp.get(getAccess(), key, defaultValue);
	}

	@Override
	public String getAbsName() {
		return comp.getAbsName();
	}
    
    @Override
    public String getBaseAbsName() {
        return comp.getBaseAbsName();
    }
    
    public boolean isBasePeristent() {
		return comp.isPersistent();
	}

	@Override
	public String getCallName() {
		return comp.getCallName();
	}

	@Override
	public String getDisplayName() {
		return comp.getDisplayName();
	}

	@Override
	public String getExtends() {
		return comp.getExtends();
	}

	@Override
	public String getHint() {
		return comp.getHint();
	}

	@Override
	public Class getJavaAccessClass(RefBoolean isNew) throws PageException {
		return comp.getJavaAccessClass(isNew);
	}

	@Override
	public synchronized Struct getMetaData(PageContext pc) throws PageException {
		return comp.getMetaData(pc);
	}

	@Override
	public String getName() {
		return comp.getName();
	}

	@Override
	public boolean getOutput() {
		return comp.getOutput();
	}

	@Override
	public boolean instanceOf(String type) {
		return comp.top.instanceOf(type);
	}

	public boolean isInitalized() {
		return comp.top.isInitalized();
	}

	@Override
	public boolean isValidAccess(int access) {
		return comp.isValidAccess(access);
	}

	@Override
	public Iterator<Collection.Key> keyIterator() {
		return comp.keyIterator(getAccess());
	}

	@Override
	public Iterator<String> keysAsStringIterator() {
		return comp.keysAsStringIterator(getAccess());
	}
	
	@Override
	public Iterator<Entry<Key, Object>> entryIterator() {
		return comp.entryIterator(getAccess());
	}

	@Override
	public Key[] keys() {
		return comp.keys(getAccess());
	}

	@Override
	public Object remove(Key key) throws PageException {
		return comp.remove(key);
	}


	@Override
	public Object removeEL(Key key) {
		return comp.removeEL(key);
	}

	@Override
	public Object set(PageContext pc, Key key, Object value) throws PageException {
		return comp.set(pc, key, value);
	}

	@Override
	public Object set(String name, Object value) throws PageException {
		return comp.set(name, value);
	}

	@Override
	public Object set(Key key, Object value) throws PageException {
		return comp.set(key, value);
	}

	@Override
	public Object setEL(PageContext pc, Key name, Object value) {
		return comp.setEL(pc, name, value);
	}

	@Override
	public Object setEL(String name, Object value) {
		return comp.setEL(name, value);
	}

	@Override
	public Object setEL(Key key, Object value) {
		return comp.setEL(key, value);
	}

	@Override
	public int size() {
		return comp.size(getAccess());
	}

	@Override
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
		return comp.top.toDumpData(pageContext, maxlevel,dp);
	}
	
	@Override
	public PageSource getPageSource() {
		return comp.getPageSource();
	}


	@Override
	public boolean containsKey(Object key) {
		return containsKey(KeyImpl.toKey(key,null));
	}


	@Override
	public Set entrySet() {
		return StructUtil.entrySet(this);
	}


	@Override
	public Object get(Object key) {
		return get(KeyImpl.toKey(key,null), null);
	}


	@Override
	public boolean isEmpty() {
		return size()==0;
	}

	@Override
	public Set keySet() {
		return StructUtil.keySet(this);
	}


	@Override
	public Object put(Object key, Object value) {
		return setEL(KeyImpl.toKey(key,null), value);
	}

	@Override
	public void putAll(Map map) {
		StructUtil.putAll(this, map);
	}

	@Override
	public Object remove(Object key) {
		return removeEL(KeyImpl.toKey(key,null));
	}

	@Override
	public java.util.Collection values() {
		return StructUtil.values(this);
	}

	@Override
	public boolean containsValue(Object value) {
		return values().contains(value);
	}


	public Iterator<Object> valueIterator() {
		return comp.valueIterator();
	}

	@Override
	public Property[] getProperties(boolean onlyPeristent) {
		return comp.getProperties(onlyPeristent);
	}

	@Override
	public Property[] getProperties(boolean onlyPeristent, boolean includeBaseProperties, boolean overrideProperties, boolean inheritedMappedSuperClassOnly) {
		return comp.getProperties(onlyPeristent,includeBaseProperties, overrideProperties, inheritedMappedSuperClassOnly);
	}


	@Override
	public ComponentScope getComponentScope() {
		return comp.getComponentScope();
	}

	@Override
	public boolean contains(PageContext pc, Key key) {
		return comp.contains(getAccess(),key);
	}

	/*private Member getMember(int access, Key key, boolean dataMember,boolean superAccess) {
		return comp.getMember(access, key, dataMember, superAccess);
	}*/

	@Override
	public void setProperty(Property property) throws PageException {
		comp.setProperty(property);
	}


	@Override
	public long sizeOf() {
		return StructUtil.sizeOf(this);
	}


	public boolean equalTo(String type) {
		return comp.top.equalTo(type);
	}

	@Override
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
	
	@Override
	public java.util.Iterator<String> getIterator() {
    	return keysAsStringIterator();
    }
	
}
