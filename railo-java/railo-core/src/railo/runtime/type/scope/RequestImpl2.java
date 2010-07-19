package railo.runtime.type.scope;

import railo.runtime.type.Struct;

public class RequestImpl2 extends ScopeSupport implements Request {

	public RequestImpl2() {
		super("request",SCOPE_REQUEST,Struct.TYPE_REGULAR);
		
	}

}
