package railo.runtime.functions.other;

import railo.runtime.Component;
import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.orm.EntityNew;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.type.FunctionValue;
import railo.runtime.type.Struct;
import railo.runtime.type.UDF;
import railo.runtime.type.util.KeyConstants;

public class _CreateComponent {
	
	private static final Object[] EMPTY = new Object[0]; 

	public static Object call(PageContext pc , Object[] objArr) throws PageException {
		String path = Caster.toString(objArr[objArr.length-1]);
		Component cfc = CreateObject.doComponent(pc, path);
		
		// no init method
		if(!(cfc.get(KeyConstants._init,null) instanceof UDF)){
			
			if(objArr.length>1) {
				Object arg1 = objArr[0];
				if(arg1 instanceof FunctionValue) {
					Struct args=Caster.toFunctionValues(objArr,0,objArr.length-1);
					EntityNew.setPropeties(pc, cfc, args,true);
				}
				else if(Decision.isStruct(arg1)){
					Struct args=Caster.toStruct(arg1);
					EntityNew.setPropeties(pc, cfc, args,true);
				}
			}
			
			return cfc;
		}
		
		Object rtn;
		// no arguments
		if(objArr.length==1) {
			rtn = cfc.call(pc, KeyConstants._init, EMPTY);
		}	
		// named arguments
		else if(objArr[0] instanceof FunctionValue) {
			Struct args=Caster.toFunctionValues(objArr,0,objArr.length-1);
			rtn = cfc.callWithNamedValues(pc, KeyConstants._init, args);
		}
		// no name arguments
		else {
			Object[] args = new Object[objArr.length-1];
			for(int i=0;i<objArr.length-1;i++) {
				args[i]=objArr[i];
				if(args[i] instanceof FunctionValue) 
					throw new ExpressionException("invalid argument defintion,when using named parameters to a function, every parameter must have a name.");
			}
			rtn = cfc.call(pc, KeyConstants._init, args);
		}
		if(rtn==null)return cfc;
		return rtn;
	}

}
