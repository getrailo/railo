package railo.runtime.functions.arrays;


import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;

public final class ArrayLast extends BIF {

	private static final long serialVersionUID = -2378677202092684813L;

	public static Object call(PageContext pc , Array array) throws PageException {
        if(array.size()==0) throw new ExpressionException("Cannot return last element of array; array is empty");
        return array.getE(array.size());
    }
	
	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		return call(pc,Caster.toArray(args[0]));
	}
    
}