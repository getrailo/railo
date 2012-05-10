package railo.runtime.functions.other;

import railo.print;
import railo.runtime.Component;
import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.type.Collection;
import railo.runtime.type.FunctionValue;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.UDF;

public class _CreateComponent {
	
	private static final Collection.Key INIT = KeyImpl.intern("init");
	private static final Object[] EMPTY = new Object[0]; 

	public static Object call(PageContext pc , Object[] objArr) throws PageException {
		print.e(objArr);
		String path = Caster.toString(objArr[objArr.length-1]);
		Component cfc = CreateObject.doComponent(pc, path);
		
		
		// no init method
		if(!(cfc.get(INIT,null) instanceof UDF)){
			return cfc;
		}
		
		Object rtn;
		// no arguments
		if(objArr.length==1) {
			rtn = cfc.call(pc, INIT, EMPTY);
		}	
		// named arguments
		else if(objArr[0] instanceof FunctionValue) {
			Struct args=Caster.toFunctionValues(objArr,0,objArr.length-1);
			rtn = cfc.callWithNamedValues(pc, INIT, args);
		}
		// no name arguments
		else {
			Object[] args = new Object[objArr.length-1];
			for(int i=0;i<objArr.length-1;i++) {
				args[i]=objArr[i];
				if(args[i] instanceof FunctionValue) 
					throw new ExpressionException("invalid argument defintion,when using named parameters to a function, every parameter must have a name.");
			}
			rtn = cfc.call(pc, INIT, args);
		}
		if(rtn==null)return cfc;
		return rtn;
	}

}
