package railo.runtime.functions.dynamicEvaluation;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;

public class Invoke implements Function {

	private static final long serialVersionUID = 3451409617437302246L;
	private static final Struct EMPTY = new StructImpl();
	
	public static Object call(PageContext pc , Object obj, String name) throws PageException {
		return call(pc, obj, name, null);
	}

	public static Object call(PageContext pc , Object obj, String name, Object arguments) throws PageException {
		if(arguments==null)arguments=EMPTY; 
		
		if(obj instanceof String) {
			obj=pc.loadComponent(Caster.toString(obj));
		}
		
		if(Decision.isStruct(arguments)) { 
			return pc.getVariableUtil().callFunctionWithNamedValues(pc,obj,KeyImpl.init(name),Caster.toStruct(arguments));
		}
		return pc.getVariableUtil().callFunctionWithoutNamedValues(pc,obj,KeyImpl.init(name),Caster.toNativeArray(arguments));
		
	}
		
}
