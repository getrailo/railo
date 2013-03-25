package railo.runtime.functions.arrays;


import railo.runtime.PageContext;
import railo.runtime.exp.CasterException;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.op.Operator;
import railo.runtime.type.Array;
import railo.runtime.type.Closure;
import railo.runtime.type.UDF;

public final class ArrayFind extends BIF {

	private static final long serialVersionUID = -3282048672805234115L;

	public static double call(PageContext pc , Array array, Object value) throws PageException {
		if(value instanceof UDF) 
        	return find(pc,array,(UDF)value);
    	
		
		return find(array,value,true);
    }
	
	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		return call(pc,Caster.toArray(args[0]),args[1]);
	}

    public static int find(PageContext pc ,Array array, UDF udf) throws PageException {
        int len=array.size();
        
        Object[] arr=new Object[1];
        Object res;
        Boolean b;
        for(int i=1;i<=len;i++) {
            arr[0]=array.get(i,null);
            if(arr[0]!=null) {
            	res=udf.call(pc, arr, false);
            	b=Caster.toBoolean(res,null);
            	if(b==null) throw new FunctionException(pc,"ArrayFind",2,"function","return value of the "+(udf instanceof Closure?"closure":"function ["+udf.getFunctionName()+"]")+" cannot be casted to a boolean value.",CasterException.createMessage(res, "boolean"));
            	if(b.booleanValue())return i;
            }
        }
        return 0;
    }
	
    public static int find(Array array, Object value, boolean caseSensitive) {
        int len=array.size();
        boolean valueIsSimple=Decision.isSimpleValue(value);
        Object o;
        for(int i=1;i<=len;i++) {
            o=array.get(i,null);
            if(o!=null && Operator.equalsEL(o, value,caseSensitive,!valueIsSimple)) return i;
        }
        return 0;
    }
}