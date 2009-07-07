package railo.runtime.functions.arrays;


import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Array;

public final class ArrayFindNoCase implements Function {

    public static double call(PageContext pc , Array array, Object value) throws PageException {
        return ArrayFind.find(array,value,false);
    }
}