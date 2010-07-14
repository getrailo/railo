package railo.runtime.functions.system;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Struct;

public final class GetSystemInfo implements Function {
    
    public static Struct call(PageContext pc) throws PageException {
        throw new ExpressionException("the function getSystemInfo is no longer supported");
    	
    }
}