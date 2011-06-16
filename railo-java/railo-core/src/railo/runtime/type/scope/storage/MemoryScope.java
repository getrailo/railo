package railo.runtime.type.scope.storage;

import railo.runtime.type.scope.Scope;

public interface MemoryScope extends Scope {
	/**
	 * is the scope expired?
	 */
	public boolean isExpired();
	
	/**
	 * set lastvistit to now
	 */
	public abstract void touch();
	
}
