package railo.runtime.type.scope;

import railo.runtime.type.Scope;

public class LocalImpl extends ScopeSupport implements Scope,Local {

	private boolean bind;
	
	public LocalImpl() {
		super(false, "local", Scope.SCOPE_LOCAL);
	}

	/**
	 * @see railo.runtime.type.scope.LocalPro#isBind()
	 */
	public boolean isBind() {
		return bind;
	}

	/**
	 * @see railo.runtime.type.scope.LocalPro#setBind(boolean)
	 */
	public void setBind(boolean bind) {
		this.bind=bind;
	}

}
