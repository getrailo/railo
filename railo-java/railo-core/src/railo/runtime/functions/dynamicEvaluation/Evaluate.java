package railo.runtime.functions.dynamicEvaluation;

import railo.runtime.PageContext;
import railo.runtime.PageContextImpl;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.interpreter.CFMLExpressionInterpreter;
import railo.runtime.op.Caster;
import railo.runtime.type.Scope;
import railo.runtime.type.scope.Argument;
import railo.runtime.type.scope.CallerImpl;
import railo.runtime.type.scope.Local;
import railo.runtime.type.scope.UndefinedImpl;
import railo.runtime.type.scope.Variables;

/**
 * Implements the Cold Fusion Function evaluate
 */
public final class Evaluate implements Function {
	

	public static Object call(PageContext pc , Object[] objs) throws PageException {
		return call(pc, objs, false);
	}
	public static Object call(PageContext pc , Object[] objs, boolean preciseMath) throws PageException {
		// define a ohter enviroment for the function
		if(objs.length>1 && objs[objs.length-1] instanceof Scope){
			
			// Variables Scope
			Variables var=null;
			if(objs[objs.length-1] instanceof Variables){
				var=(Variables) objs[objs.length-1];
			}
			else if(objs[objs.length-1] instanceof CallerImpl){
				var=((CallerImpl) objs[objs.length-1]).getVariablesScope();
			}
			if(var!=null){
				Variables current=pc.variablesScope();
				pc.setVariablesScope(var);
		        try{
		        	return _call(pc, objs,objs.length-1,preciseMath);
		        }
		        finally{
		        	pc.setVariablesScope(current);
		        }
			}
			
			// Undefined Scope
			else if(objs[objs.length-1] instanceof UndefinedImpl) {
				PageContextImpl pci=(PageContextImpl) pc;
				UndefinedImpl undefined=(UndefinedImpl) objs[objs.length-1];
				
				boolean check=undefined.getCheckArguments();
				Variables orgVar=pc.variablesScope();
				Argument orgArgs=pc.argumentsScope();
		        Local orgLocal=pc.localScope();
				
				pci.setVariablesScope(undefined.variablesScope());
				if(check)pci.setFunctionScopes(undefined.localScope(), undefined.argumentsScope());
		        try{
		        	return _call(pc, objs,objs.length-1,preciseMath);
		        }
		        finally{
		        	pc.setVariablesScope(orgVar);
		        	if(check)pci.setFunctionScopes(orgLocal,orgArgs);
		        }
				
			}
		}
		return _call(pc,objs,objs.length,preciseMath);
	}

	private static Object _call(PageContext pc , Object[] objs,int len, boolean preciseMath) throws PageException {
		Object rst=null;
		for(int i=0;i<len;i++) {
			if(objs[i] instanceof Number) rst= objs[i];
			else rst= new CFMLExpressionInterpreter().interpret(pc,Caster.toString(objs[i]), preciseMath);
		}
		return rst;
	}
}