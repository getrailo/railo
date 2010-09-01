package railo.runtime.type.scope;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import railo.runtime.PageContext;
import railo.runtime.net.http.HTTPServletRequestWrap;
import railo.runtime.type.Struct;

public class RequestImpl extends ScopeSupport implements Request {

	public RequestImpl() {
		super("request",SCOPE_REQUEST,Struct.TYPE_REGULAR);
		
	}

	public void initialize(PageContext pc) {
		
		// fill all data from servlet request scope
		HTTPServletRequestWrap req = (HTTPServletRequestWrap) pc.getHttpServletRequest();
		HttpServletRequest org = req.getOriginalRequest();
		Enumeration<String> names = org.getAttributeNames();
		String name;
		while(names.hasMoreElements()) {
			name= names.nextElement();
			setEL(name, org.getAttribute(name));
		}
		
		// now set the request scope as backend for the httpservletrequestwrap
		
		req.setAttributes(this);
		
		super.initialize(pc);
	}

}
