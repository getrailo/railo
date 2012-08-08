package railo.runtime.functions.international;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.ext.function.Function;

/**
 * Implements the CFML Function getEncoding
 */
public final class GetEncoding implements Function {
	public static String call(PageContext pc , String scope) throws FunctionException {
        scope=scope.trim().toLowerCase();
        
        if(scope.equals("url"))return (pc.urlScope()).getEncoding();
        if(scope.equals("form"))return (pc.formScope()).getEncoding();
        throw new FunctionException(pc,"getEncoding",1,"scope","scope must have the one of the following values [url,form] not ["+scope+"]");
        
	}
}