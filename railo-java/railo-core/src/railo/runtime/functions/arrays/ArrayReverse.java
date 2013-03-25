/**
 * Implements the CFML Function ArrayReverse
 */
package railo.runtime.functions.arrays;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;


public final class ArrayReverse extends BIF {

	private static final long serialVersionUID = 5418304787535992180L;

	public static Array call(PageContext pc , Array array) throws ExpressionException {
		Array rev=new ArrayImpl(array.getDimension());
		int len=array.size();
		for(int i=0;i<len;i++) {
			try {
				rev.setE(len-i,array.getE(i+1));
			} catch (PageException e) {
			}
		}
		return rev;
	}	
	
	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		return call(pc,Caster.toArray(args[0]));
	}
}