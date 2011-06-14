package railo.runtime.type.cfc;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import railo.runtime.Component;
import railo.runtime.PageContext;
import railo.runtime.component.Member;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.exp.PageException;
import railo.runtime.type.Collection;
import railo.runtime.type.Struct;

public abstract class ComponentAccessProxy extends ComponentProxy implements ComponentAccess {

	public abstract ComponentAccess getComponentAccess();
	

	public Component getComponent() {
		return getComponentAccess();
	}
	
	/**
	 * @see railo.runtime.ComponentPro#getWSDLFile()
	 */
	public String getWSDLFile() {
		return getComponentAccess().getWSDLFile();
	}

	/**
	 * @see railo.runtime.type.Collection#duplicate(boolean)
	 */
	public Collection duplicate(boolean deepCopy,Map<Object, Object> done) {
		return getComponentAccess().duplicate(deepCopy,done);
	}

	/**
	 * @see railo.runtime.type.cfc.ComponentAccess#isPersistent()
	 */
	public boolean isPersistent() {
		return getComponentAccess().isPersistent();
	}

	/**
	 * @see railo.runtime.type.cfc.ComponentAccess#getMetaStructItem(railo.runtime.type.Collection.Key)
	 */
	public Object getMetaStructItem(Key name) {
		return getComponentAccess().getMetaStructItem(name);
	}

	/**
	 * @see railo.runtime.type.cfc.ComponentAccess#keySet(int)
	 */
	public Set<Key> keySet(int access) {
		return getComponentAccess().keySet(access);
	}

	/**
	 * @see railo.runtime.type.cfc.ComponentAccess#call(railo.runtime.PageContext, int, railo.runtime.type.Collection.Key, java.lang.Object[])
	 */
	public Object call(PageContext pc, int access, Key name, Object[] args)
			throws PageException {
		return getComponentAccess().call(pc, access, name, args);
	}

	/**
	 * @see railo.runtime.type.cfc.ComponentAccess#callWithNamedValues(railo.runtime.PageContext, int, railo.runtime.type.Collection.Key, railo.runtime.type.Struct)
	 */
	public Object callWithNamedValues(PageContext pc, int access, Key name,
			Struct args) throws PageException {
		return getComponentAccess().callWithNamedValues(pc, access, name, args);
	}

	public int size(int access) {
		return getComponentAccess().size(access);
	}

	/**
	 * @see railo.runtime.type.cfc.ComponentAccess#keysAsString(int)
	 */
	public String[] keysAsString(int access) {
		return getComponentAccess().keysAsString(access);
	}

	/**
	 * @see railo.runtime.type.cfc.ComponentAccess#keys(int)
	 */
	public Key[] keys(int access) {
		return getComponentAccess().keys(access);
	}

	/**
	 * @see railo.runtime.type.cfc.ComponentAccess#get(int, railo.runtime.type.Collection.Key)
	 */
	public Object get(int access, Key key) throws PageException {
		return getComponentAccess().get(access, key);
	}

	/**
	 * @see railo.runtime.type.cfc.ComponentAccess#get(int, railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object get(int access, Key key, Object defaultValue) {
		return getComponentAccess().get(access, key, defaultValue);
	}

	/**
	 * @see railo.runtime.type.cfc.ComponentAccess#iterator(int)
	 */
	public Iterator iterator(int access) {
		return getComponentAccess().iterator(access);
	}

	/**
	 * @see railo.runtime.type.cfc.ComponentAccess#toDumpData(railo.runtime.PageContext, int, railo.runtime.dump.DumpProperties, int)
	 */
	public DumpData toDumpData(PageContext pageContext, int maxlevel,
			DumpProperties dp, int access) {
		return getComponentAccess().toDumpData(pageContext, maxlevel, dp, access);
	}

	/**
	 * @see railo.runtime.type.cfc.ComponentAccess#contains(int, railo.runtime.type.Collection.Key)
	 */
	public boolean contains(int access, Key name) {
		return getComponentAccess().contains(access, name);
	}

	/**
	 * @see railo.runtime.type.cfc.ComponentAccess#getMember(int, railo.runtime.type.Collection.Key, boolean, boolean)
	 */
	public Member getMember(int access, Key key, boolean dataMember,
			boolean superAccess) {
		return getComponentAccess().getMember(access, key, dataMember, superAccess);
	}
	
	/**
	 * @see railo.runtime.type.cfc.ComponentAccess#_base()
	 */
	public ComponentAccess _base() {
		return getComponentAccess()._base();
	}

	/**
	 * @see railo.runtime.type.Sizeable#sizeOf()
	 */
	public long sizeOf() {
		return getComponentAccess().sizeOf();
	}
}
