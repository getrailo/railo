package railo.runtime.functions.dynamicEvaluation;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;
import railo.runtime.type.Scope;
import railo.runtime.type.scope.CallerImpl;
import railo.runtime.type.scope.Variables;

/**
 * Implements the Cold Fusion Function evaluate
 */
public final class Evaluate implements Function {

	
	public static Object call(PageContext pc , Object[] objs) throws PageException {
		// define a ohter enviroment for the function
		if(objs.length>1 && objs[objs.length-1] instanceof Scope){
			Variables var=null;
			if(objs[objs.length-1] instanceof Variables){
				var=(Variables) objs[objs.length-1];
			}
			else if(objs[objs.length-1] instanceof CallerImpl){
				var=((CallerImpl) objs[objs.length-1]).getVariablesScope();
			}
			else if(objs[objs.length-1] instanceof CallerImpl){
				var=((CallerImpl) objs[objs.length-1]).getVariablesScope();
			}
			if(var!=null){
				Variables current=pc.variablesScope();
				pc.setVariablesScope(var);
		        try{
		        	return _call(pc, objs,objs.length-1);
		        }
		        finally{
		        	pc.setVariablesScope(current);
		        }
			}
		}
		return _call(pc,objs,objs.length);
	}

	private static Object _call(PageContext pc , Object[] objs,int len) throws PageException {
		Object rst=null;
		for(int i=0;i<len;i++) {
			if(objs[i] instanceof Number) rst= objs[i];
			else rst= pc.evaluate(Caster.toString(objs[i]));
		}
		return rst;
	}
}