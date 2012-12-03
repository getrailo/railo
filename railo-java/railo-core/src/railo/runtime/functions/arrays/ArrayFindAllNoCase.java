package railo.runtime.functions.arrays;


import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Array;

public final class ArrayFindAllNoCase implements Function {
	
	private static final long serialVersionUID = -1922900405563697067L;

	public static Array call(PageContext pc , Array array, Object value) throws PageException {
        return ArrayFindAll.find(array,value,false);
    }
}