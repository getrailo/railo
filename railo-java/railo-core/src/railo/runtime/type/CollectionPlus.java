package railo.runtime.type;

import java.util.Map;

// FUTURE add to interface Collection
public interface CollectionPlus extends Collection {
	public Collection duplicate(boolean deepCopy, Map<Object,Object> done);
}
