// FUTURE move to loader
package railo.runtime.type;

import railo.runtime.PageContext;

public interface RequestScope extends Scope {
	/**
	 * return if the scope is Initialiesd
	 * @return scope is init
	 */
	public boolean isInitalized(); 
	
	/**
	 * Initalize Scope
	 * @param pc Page Context
	 */
	public void initialize(PageContext pc); 
	
	/**
	 * release scope for reuse
	 */
	public void release(PageContext pc);
	
}
