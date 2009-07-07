package railo.runtime.functions.arrays;


import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Array;

public final class ArrayFirst implements Function {

    public static Object call(PageContext pc , Array array) throws PageException {
        if(array.size()==0) throw new ExpressionException("can't return first element of array, array is empty");
        return array.getE(1);
    }
    
}