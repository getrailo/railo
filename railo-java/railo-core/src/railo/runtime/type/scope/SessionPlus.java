package railo.runtime.type.scope;

import railo.runtime.type.RequestScope;

public interface SessionPlus extends Session,RequestScope {

	public int _getId();// TODO to better implementation

}
