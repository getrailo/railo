package railo.runtime.functions.dynamicEvaluation;

import railo.runtime.PageContext;
import railo.runtime.PageContextImpl;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.interpreter.CFMLExpressionInterpreter;
import railo.runtime.op.Caster;
import railo.runtime.type.scope.Argument;
import railo.runtime.type.scope.CallerImpl;
import railo.runtime.type.scope.Local;
import railo.runtime.type.scope.LocalNotSupportedScope;
import railo.runtime.type.scope.Scope;
import railo.runtime.type.scope.Undefined;
import railo.runtime.type.scope.Variables;

/**
 * Implements the CFML Function evaluate
 */
public final class Evaluate implements Function {

	private static final long serialVersionUID = 2259041678381553989L;

	public static Object call(PageContext pc , Object[] objs) throws PageException {
		return call(pc, objs, false);
	}
	public static Object call(PageContext pc , Object[] objs, boolean preciseMath) throws PageException {
		// define a ohter enviroment for the function
		if(objs.length>1 && objs[objs.length-1] instanceof Scope){
			
			// Variables Scope
			Variables var=null;
			Local lcl=null,cLcl=null;
			Argument arg=null,cArg=null;
			if(objs[objs.length-1] instanceof Variables){
				var=(Variables) objs[objs.length-1];
			}
			else if(objs[objs.length-1] instanceof CallerImpl){
				CallerImpl ci = ((CallerImpl) objs[objs.length-1]);
				var=ci.getVariablesScope();
				lcl = ci.getLocalScope();
				arg = ci.getArgumentsScope();
			}
			
			if(var!=null){
				Variables cVar=pc.variablesScope();
				pc.setVariablesScope(var);
				if(lcl!=null && !(lcl instanceof LocalNotSupportedScope)) {
					cLcl = pc.localScope();
					cArg=pc.argumentsScope();
					pc.setFunctionScopes(lcl, arg);
				}
		        try{
		        	return _call(pc, objs,objs.length-1,preciseMath);
		        }
		        finally{
		        	pc.setVariablesScope(cVar);
		        	if(cLcl!=null) pc.setFunctionScopes(cLcl, cArg);
		        }
			}
			
			// Undefined Scope
			else if(objs[objs.length-1] instanceof Undefined) {
				PageContextImpl pci=(PageContextImpl) pc;
				Undefined undefined=(Undefined) objs[objs.length-1];
				
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