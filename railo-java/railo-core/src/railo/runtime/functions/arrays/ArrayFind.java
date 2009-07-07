package railo.runtime.functions.arrays;


import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Operator;
import railo.runtime.type.Array;

public final class ArrayFind implements Function {

    public static double call(PageContext pc , Array array, Object value) throws PageException {
        return find(array,value,true);
    }
    public static int find(Array array, Object value, boolean caseSensitive) throws PageException {
        int len=array.size();
        Object o;
        for(int i=1;i<=len;i++) {
            o=array.get(i,null);
            if(o!=null && Operator.equals(o,value,caseSensitive)) return i;
        }
        return 0;
    }
}