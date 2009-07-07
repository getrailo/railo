package railo.runtime.type.scope;

import railo.runtime.type.Struct;

public class RequestImpl extends ScopeSupport implements Request {

	
	public RequestImpl() {
		super("request",SCOPE_REQUEST,Struct.TYPE_REGULAR);
	}
	
}
