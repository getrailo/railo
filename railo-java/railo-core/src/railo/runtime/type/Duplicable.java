package railo.runtime.type;
// FUTURE move to interface
public interface Duplicable {

	public Object duplicate(boolean deepCopy);
	// FUTURE public Collection duplicate(boolean deepCopy, Map<Object,Object> done);
}
