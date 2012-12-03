package railo.runtime.type.scope;

import railo.runtime.PageContext;

public interface UserScope extends SharedScope {
    
	public void resetEnv(PageContext pc); 

}
