package railo.runtime.orm.hibernate.tuplizer.proxy;

import java.util.Iterator;
import java.util.Set;

import railo.runtime.Component;
import railo.runtime.PageContext;
import railo.runtime.component.Member;
import railo.runtime.component.Property;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.orm.hibernate.CommonUtil;
import railo.runtime.type.Struct;

/*
 * this implementation "simulates" all ComponentPro methods from core 
 */

public abstract class ComponentProReflectionProxy extends ComponentProxy {

	private static final long serialVersionUID = -7646935560408716588L;

	

	public Property[] getProperties(boolean onlyPeristent, boolean includeBaseProperties, boolean overrideProperties, boolean inheritedMappedSuperClassOnly) {
		return getProperties(getComponent(), onlyPeristent, includeBaseProperties, overrideProperties, inheritedMappedSuperClassOnly);
	}
	

	public static Property[] getProperties(Component c, boolean onlyPeristent, boolean includeBaseProperties, boolean overrideProperties, boolean inheritedMappedSuperClassOnly) {
		try{
			java.lang.reflect.Method m = c.getClass().getMethod("getProperties", new Class[]{boolean.class, boolean.class, boolean.class , boolean.class});
			return (Property[])m.invoke(c, new Object[]{onlyPeristent, includeBaseProperties, overrideProperties, inheritedMappedSuperClassOnly});
		}
		catch(Throwable t){
			throw new RuntimeException(t);
		}
	}
	
	public boolean isPersistent() {
		return isPersistent(getComponent());
	}
	
	public static boolean isPersistent(Component c) {
		try{
			java.lang.reflect.Method m = c.getClass().getMethod("isPersistent", new Class[]{});
			return CommonUtil.toBooleanValue(m.invoke(c, new Object[]{}));
		}
		catch(Throwable t){
			throw new RuntimeException(t);
		}
	}

	public boolean isAccessors() {
		Component c = getComponent();
		try{
			java.lang.reflect.Method m = c.getClass().getMethod("isAccessors", new Class[]{});
			return CommonUtil.toBooleanValue(m.invoke(c, new Object[]{}));
		}
		catch(Throwable t){
			throw new RuntimeException(t);
		}
	}

	public Object getMetaStructItem(Key name) {
		return getMetaStructItem(getComponent(), name);
	}

	public static Object getMetaStructItem(Component c, Key name) {
		try{
			java.lang.reflect.Method m = c.getClass().getMethod("getMetaStructItem", new Class[]{Key.class});
			return m.invoke(c, new Object[]{name});
		}
		catch(Throwable t){
			throw new RuntimeException(t);
		}
	}

	public Set<Key> keySet(int access) {
		Component c = getComponent();
		try{
			java.lang.reflect.Method m = c.getClass().getMethod("keySet", new Class[]{int.class});
			return (Set<Key>)m.invoke(c, new Object[]{access});
		}
		catch(Throwable t){
			throw new RuntimeException(t);
		}
	}

	public Object call(PageContext pc, int access, Key name, Object[] args) {
		Component c = getComponent();
		try{
			java.lang.reflect.Method m = c.getClass().getMethod("call", new Class[]{PageContext.class, int.class, Key.class, Object[].class});
			return m.invoke(c, new Object[]{pc, access, name, args});
		}
		catch(Throwable t){
			throw new RuntimeException(t);
		}
	}

	public Object callWithNamedValues(PageContext pc, int access, Key name, Struct args) {
		Component c = getComponent();
		try{
			java.lang.reflect.Method m = c.getClass().getMethod("callWithNamedValues", new Class[]{PageContext.class, int.class, Key.class, Struct.class});
			return m.invoke(c, new Object[]{pc, access, name, args});
		}
		catch(Throwable t){
			throw new RuntimeException(t);
		}
	}

	public int size(int access) {
		Component c = getComponent();
		try{
			java.lang.reflect.Method m = c.getClass().getMethod("size", new Class[]{int.class});
			return CommonUtil.toIntValue(m.invoke(c, new Object[]{access}));
		}
		catch(Throwable t){
			throw new RuntimeException(t);
		}
	}

	public Key[] keys(int access) {
		Component c = getComponent();
		try{
			java.lang.reflect.Method m = c.getClass().getMethod("keys", new Class[]{int.class});
			return (Key[]) m.invoke(c, new Object[]{access});
		}
		catch(Throwable t){
			throw new RuntimeException(t);
		}
	}

	public Iterator<Entry<Key, Object>> entryIterator(int access) {
		Component c = getComponent();
		try{
			java.lang.reflect.Method m = c.getClass().getMethod("entryIterator", new Class[]{int.class});
			return (Iterator<Entry<Key, Object>>)m.invoke(c, new Object[]{access});
		}
		catch(Throwable t){
			throw new RuntimeException(t);
		}
	}

	public Iterator<Object> valueIterator(int access) {
		Component c = getComponent();
		try{
			java.lang.reflect.Method m = c.getClass().getMethod("valueIterator", new Class[]{int.class});
			return (Iterator<Object>)m.invoke(c, new Object[]{access});
		}
		catch(Throwable t){
			throw new RuntimeException(t);
		}
	}
	

	public Object get(int access, Key key) {
		Component c = getComponent();
		try{
			java.lang.reflect.Method m = c.getClass().getMethod("get", new Class[]{int.class,Key.class});
			return m.invoke(c, new Object[]{access,key});
		}
		catch(Throwable t){
			throw new RuntimeException(t);
		}
	}

	public Object get(int access, Key key, Object defaultValue) {
		Component c = getComponent();
		try{
			java.lang.reflect.Method m = c.getClass().getMethod("get", new Class[]{int.class,Key.class,Object.class});
			return m.invoke(c, new Object[]{access,key,defaultValue});
		}
		catch(Throwable t){
			throw new RuntimeException(t);
		}
	}

	public Iterator<Key> keyIterator(int access) {
		Component c = getComponent();
		try{
			java.lang.reflect.Method m = c.getClass().getMethod("keyIterator", new Class[]{int.class});
			return (Iterator<Key>) m.invoke(c, new Object[]{access});
		}
		catch(Throwable t){
			throw new RuntimeException(t);
		}
	}
	
	public Iterator<String> keysAsStringIterator(int access) {
		Component c = getComponent();
		try{
			java.lang.reflect.Method m = c.getClass().getMethod("keysAsStringIterator", new Class[]{int.class});
			return (Iterator<String>) m.invoke(c, new Object[]{access});
		}
		catch(Throwable t){
			throw new RuntimeException(t);
		}
	}

	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp, int access) {
		Component c = getComponent();
		try{
			java.lang.reflect.Method m = c.getClass().getMethod("toDumpData", new Class[]{PageContext.class, int.class, DumpProperties.class, int.class});
			return (DumpData) m.invoke(c, new Object[]{pageContext, maxlevel, dp, access});
		}
		catch(Throwable t){
			throw new RuntimeException(t);
		}
	}

	public boolean contains(int access, Key name) {
		Component c = getComponent();
		try{
			java.lang.reflect.Method m = c.getClass().getMethod("contains", new Class[]{int.class, Key.class});
			return CommonUtil.toBooleanValue(m.invoke(c, new Object[]{access, name}));
		}
		catch(Throwable t){
			throw new RuntimeException(t);
		}
	}

	public Member getMember(int access, Key key, boolean dataMember, boolean superAccess) {
		Component c = getComponent();
		try{
			java.lang.reflect.Method m = c.getClass().getMethod("getMember", new Class[]{int.class, Key.class, boolean.class, boolean.class});
			return (Member) m.invoke(c, new Object[]{access, key, dataMember, superAccess});
		}
		catch(Throwable t){
			throw new RuntimeException(t);
		}
	}
	
	public void setEntity(boolean entity) {
		setEntity(getComponent(), entity);
	}
	
	public static void setEntity(Component c, boolean entity) {
		try{
			java.lang.reflect.Method m = c.getClass().getMethod("setEntity", new Class[]{boolean.class});
			m.invoke(c, new Object[]{entity});
			return;
		}
		catch(Throwable t){
			throw new RuntimeException(t);
		}
	}

	public boolean isEntity() {
		Component c = getComponent();
		try{
			java.lang.reflect.Method m = c.getClass().getMethod("isEntity", new Class[]{});
			return CommonUtil.toBooleanValue(m.invoke(c, new Object[]{}));
		}
		catch(Throwable t){
			throw new RuntimeException(t);
		}
	}

	public Component getBaseComponent() {
		Component c = getComponent();
		try{
			java.lang.reflect.Method m = c.getClass().getMethod("getBaseComponent", new Class[]{});
			return (Component)m.invoke(c, new Object[]{});
		}
		catch(Throwable t){
			throw new RuntimeException(t);
		}
	}
}
