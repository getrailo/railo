package railo.runtime.functions.international;

import java.io.UnsupportedEncodingException;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;

/**
 * Implements the CFML Function setlocale
 */
public final class SetEncoding implements Function {
      
    public static String call(PageContext pc , String scope, String charset) throws PageException {
        scope=scope.trim().toLowerCase();
        try {
            if(scope.equals("url"))(pc.urlScope()).setEncoding(charset);
            else if(scope.equals("form"))(pc.formScope()).setEncoding(charset);
            else throw new FunctionException(pc,"setEncoding",1,"scope","scope must have the one of the following values [url,from] not ["+scope+"]");
            
        } catch (UnsupportedEncodingException e) {
            throw Caster.toPageException(e);
        }
        return "";  
    }
	
	
}