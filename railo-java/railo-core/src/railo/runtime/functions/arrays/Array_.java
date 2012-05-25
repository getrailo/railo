/**
 * Implements the ColdFusion Function array
 */
package railo.runtime.functions.arrays;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.FunctionValue;

/**
 * implementation of the Function array
 */
public class Array_ implements Function {
	
	private static final long serialVersionUID = 4974431571073577001L;

	/**
	 * @param pc
	 * @param objArr
	 * @return
	 * @throws ExpressionException
	 */
	public static Array call(PageContext pc , Object[] objArr) {
		for(int i=0;i<objArr.length;i++) {
			if(objArr[i] instanceof FunctionValue)objArr[i]=((FunctionValue)objArr[i]).getValue();
		}
		return new ArrayImpl(objArr);
	}
}