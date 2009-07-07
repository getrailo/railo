/**
 * Implements the Cold Fusion Function array
 */
package railo.runtime.functions.struct;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.FunctionValue;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;

public class Struct_ implements Function {
	public static Struct call(PageContext pc , Object[] objArr) throws PageException {
		return _call(objArr, "invalid argument for function struct, only named arguments are allowed like struct(name:\"value\",name2:\"value2\")");
	}
	protected static Struct _call(Object[] objArr,String expMessage) throws PageException {
		Struct sct=new StructImpl();
		for(int i=0;i<objArr.length;i++) {
			if(objArr[i] instanceof FunctionValue) {
				FunctionValue fv=((FunctionValue)objArr[i]);
				sct.set(fv.getName(),fv.getValue());
			}
			else {
				throw new ExpressionException(expMessage);
			}
		}
		return sct;
	}
}