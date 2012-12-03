package railo.runtime.functions.rest;

import javax.servlet.http.HttpServletRequest;

import railo.runtime.PageContext;
import railo.runtime.exp.ApplicationException;
import railo.runtime.rest.Result;
import railo.runtime.type.Struct;

public class RestSetResponse {
	public static String call(PageContext pc , Struct rsp) throws ApplicationException {
		HttpServletRequest req = pc.getHttpServletRequest();
		
		Result result = (Result) req.getAttribute("rest-result");
		if(result==null)throw new ApplicationException("not inside a REST Request");
		
		result.setCustomResponse(rsp);
		
		
		return null;
	}
}
