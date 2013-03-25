package railo.runtime.functions.arrays;


import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;

public final class ArrayFirst extends BIF {

	private static final long serialVersionUID = 6190330742719202792L;

	public static Object call(PageContext pc , Array array) throws PageException {
        if(array.size()==0) throw new ExpressionException("Cannot return first element of array; array is empty");
        return array.getE(1);
    }
	
	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		return call(pc,Caster.toArray(args[0]));
	}
    
}