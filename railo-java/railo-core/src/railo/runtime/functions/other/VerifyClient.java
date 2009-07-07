package railo.runtime.functions.other;


import railo.runtime.PageContext;
import railo.runtime.exp.FunctionNotSupported;
import railo.runtime.ext.function.Function;

public final class VerifyClient implements Function {
    
    public static String call(PageContext pc) throws FunctionNotSupported {
    	throw new FunctionNotSupported("VerifyClient");
    }
    

}