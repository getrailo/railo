package railo.runtime.type.cfc;

import java.util.Iterator;
import java.util.Set;

import railo.runtime.Component;
import railo.runtime.PageContext;
import railo.runtime.component.Member;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.exp.PageException;
import railo.runtime.type.Collection;
import railo.runtime.type.Struct;

public interface ComponentAccess extends Component {
	
	@Override
	public boolean isPersistent();
	@Override
	public Object getMetaStructItem(Collection.Key name);
	
	@Override
	Set<Key> keySet(int access);
	@Override
	Object call(PageContext pc, int access, Collection.Key name, Object[] args) throws PageException;
	@Override
	Object callWithNamedValues(PageContext pc, int access, Collection.Key name, Struct args) throws PageException;
	@Override
	int size(int access);
	@Override
	Collection.Key[] keys(int access);

	@Override
	Iterator<Collection.Key> keyIterator(int access);
	@Override
	Iterator<String> keysAsStringIterator(int access);
	

	@Override
	Iterator<Entry<Key, Object>> entryIterator(int access);
	@Override
	Iterator<Object> valueIterator(int access);
	
	@Override
	Object get(int access, Collection.Key key) throws PageException;
	@Override
	Object get(int access, Collection.Key key, Object defaultValue);
	@Override
	DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp, int access);
	@Override
	boolean contains(int access,Key name);
	@Override
	Member getMember(int access,Collection.Key key, boolean dataMember,boolean superAccess);
	public ComponentAccess _base();// TODO do better impl
	//public boolean isRest();
	@Override
	public void setEntity(boolean entity);
	@Override
	public boolean isEntity();
	
	
	
}
