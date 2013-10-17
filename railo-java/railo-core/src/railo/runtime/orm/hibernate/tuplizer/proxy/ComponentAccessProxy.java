package railo.runtime.orm.hibernate.tuplizer.proxy;

import java.util.Iterator;
import java.util.Set;

import railo.runtime.Component;
import railo.runtime.PageContext;
import railo.runtime.component.Member;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.exp.PageException;
import railo.runtime.op.Duplicator;
import railo.runtime.type.Collection;
import railo.runtime.type.Struct;
import railo.runtime.type.cfc.ComponentAccess;

public abstract class ComponentAccessProxy extends ComponentProxy implements ComponentAccess {

	public abstract ComponentAccess getComponentAccess();
	

	public Component getComponent() {
		return getComponentAccess();
	}
	
	@Override
	public String getWSDLFile() {
		return getComponentAccess().getWSDLFile();
	}

	@Override
	public Collection duplicate(boolean deepCopy) {
		return (Collection) Duplicator.duplicate(getComponentAccess(),deepCopy);
	}

	@Override
	public boolean isPersistent() {
		return getComponentAccess().isPersistent();
	}

	@Override
	public Object getMetaStructItem(Key name) {
		return getComponentAccess().getMetaStructItem(name);
	}

	@Override
	public Set<Key> keySet(int access) {
		return getComponentAccess().keySet(access);
	}

	@Override
	public Object call(PageContext pc, int access, Key name, Object[] args)
			throws PageException {
		return getComponentAccess().call(pc, access, name, args);
	}

	@Override
	public Object callWithNamedValues(PageContext pc, int access, Key name,
			Struct args) throws PageException {
		return getComponentAccess().callWithNamedValues(pc, access, name, args);
	}

	public int size(int access) {
		return getComponentAccess().size(access);
	}

	@Override
	public Key[] keys(int access) {
		return getComponentAccess().keys(access);
	}

	@Override
	public Iterator<Entry<Key, Object>> entryIterator(int access) {
		return getComponentAccess().entryIterator(access);
	}

	@Override
	public Iterator<Object> valueIterator(int access) {
		return getComponentAccess().valueIterator(access);
	}
	

	@Override
	public Object get(int access, Key key) throws PageException {
		return getComponentAccess().get(access, key);
	}

	@Override
	public Object get(int access, Key key, Object defaultValue) {
		return getComponentAccess().get(access, key, defaultValue);
	}

	@Override
	public Iterator<Collection.Key> keyIterator(int access) {
		return getComponentAccess().keyIterator(access);
	}
	
	public Iterator<String> keysAsStringIterator(int access) {
		return getComponentAccess().keysAsStringIterator(access);
	}

	@Override
	public DumpData toDumpData(PageContext pageContext, int maxlevel,
			DumpProperties dp, int access) {
		return getComponentAccess().toDumpData(pageContext, maxlevel, dp, access);
	}

	@Override
	public boolean contains(int access, Key name) {
		return getComponentAccess().contains(access, name);
	}

	@Override
	public Member getMember(int access, Key key, boolean dataMember,
			boolean superAccess) {
		return getComponentAccess().getMember(access, key, dataMember, superAccess);
	}
	
	@Override
	public ComponentAccess _base() {
		return getComponentAccess()._base();
	}
	
	@Override
	public void setEntity(boolean entity) {
		getComponentAccess().setEntity(entity);
	}

	@Override
	public boolean isEntity() {
		return getComponentAccess().isEntity();
	}



}
