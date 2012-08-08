/**
 * Implements the CFML Function array
 */
package railo.runtime.functions.struct;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.FunctionValue;
import railo.runtime.type.FunctionValueImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;

public class Struct_ implements Function {
	public static Struct call(PageContext pc , Object[] objArr) throws PageException {
		return _call(objArr, "invalid argument for function struct, only named arguments are allowed like struct(name:\"value\",name2:\"value2\")");
	}
	
	
	protected static Struct _call(Object[] objArr,String expMessage) throws PageException {
		Struct sct=new StructImpl();
		FunctionValueImpl fv;
		for(int i=0;i<objArr.length;i++) {
			if(objArr[i] instanceof FunctionValue) {
				fv=((FunctionValueImpl)objArr[i]);
				if(fv.getNames()==null) {
					sct.set(fv.getNameAsKey(),fv.getValue());
				}
				else {
					String[] arr = fv.getNames();
					Struct s=sct;
					for(int y=0;y<arr.length-1;y++) {
						s=touch(s,arr[y]);
					}
					s.set(arr[arr.length-1], fv.getValue());	
				}
			}
			else {
				throw new ExpressionException(expMessage);
			}
		}
		return sct;
	}
	private static Struct touch(Struct parent,String name) {
		name=name.trim();
		Object obj=parent.get(name, null); 
		if(obj instanceof Struct) return (Struct) obj;
		Struct sct=new StructImpl();
		parent.setEL(name, sct);
		return sct;
	}
	
}