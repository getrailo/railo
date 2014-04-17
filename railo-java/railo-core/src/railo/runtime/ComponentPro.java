package railo.runtime;

import java.util.Iterator;
import java.util.Set;

import railo.runtime.component.Member;
import railo.runtime.component.Property;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.exp.PageException;
import railo.runtime.type.Collection;
import railo.runtime.type.Struct;

public interface ComponentPro extends Component {
	public Property[] getProperties(boolean onlyPeristent, boolean includeBaseProperties, boolean preferBaseProperties, boolean inheritedMappedSuperClassOnly);
	
	public boolean isPersistent();
	public boolean isAccessors();
	public void setEntity(boolean entity);
	public boolean isEntity();
	public Component getBaseComponent();
	public Object getMetaStructItem(Collection.Key name);
    
	// access
    Set<Key> keySet(int access);
    Object call(PageContext pc, int access, Collection.Key name, Object[] args) throws PageException;
	Object callWithNamedValues(PageContext pc, int access, Collection.Key name, Struct args) throws PageException;
	int size(int access);
	Collection.Key[] keys(int access);

	Iterator<Collection.Key> keyIterator(int access);
	Iterator<String> keysAsStringIterator(int access);
	

	Iterator<Entry<Key, Object>> entryIterator(int access);
	Iterator<Object> valueIterator(int access);
	
	Object get(int access, Collection.Key key) throws PageException;
	Object get(int access, Collection.Key key, Object defaultValue);
	DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp, int access);
	boolean contains(int access,Key name);
	Member getMember(int access,Collection.Key key, boolean dataMember,boolean superAccess);
}
