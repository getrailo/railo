// FUTURE move to loader
package railo.runtime.type;

import railo.runtime.PageContext;

/**
 * scope that is used for multiple requests, attention scope can be used from muliple threads ad same state, make no internal state!
 */
public interface SharedScope extends Scope {

	/**
	 * Initalize Scope only for this request, scope was already used
	 * @param pc Page Context
	 */
	public void touchBeforeRequest(PageContext pc); 
	
	/**
	 * release scope only for current request, scope will be used again
	 */
	public void touchAfterRequest(PageContext pc);
}
