package railo.runtime.functions.arrays;


import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.op.Operator;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;

public final class ArrayFindAll implements Function {

	private static final long serialVersionUID = -1757019034608924098L;

	public static Array call(PageContext pc , Array array, Object value) throws PageException {
        return find(array,value,true);
    }
    public static Array find(Array array, Object value, boolean caseSensitive) throws PageException {
        Array rtn=new ArrayImpl();
    	int len=array.size();
        boolean valueIsSimple=Decision.isSimpleValue(value);
        Object o;
        for(int i=1;i<=len;i++) {
            o=array.get(i,null);
            if(o!=null && Operator.equals(o, value,caseSensitive,!valueIsSimple)) {
            	rtn.appendEL(Caster.toDouble(i));
            }
        }
        return rtn;
    }
}