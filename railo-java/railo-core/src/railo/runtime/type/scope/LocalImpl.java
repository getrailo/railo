package railo.runtime.type.scope;


public final class LocalImpl extends ScopeSupport implements Scope,Local {

	private boolean bind;
	
	public LocalImpl() {
		super(false, "local", Scope.SCOPE_LOCAL);
	}

	@Override
	public boolean isBind() {
		return bind;
	}

	@Override
	public void setBind(boolean bind) {
		this.bind=bind;
	}

}
