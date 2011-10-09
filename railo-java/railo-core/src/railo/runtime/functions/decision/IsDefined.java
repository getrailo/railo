/**
 * Implements the Cold Fusion Function isdefined
 */
package railo.runtime.functions.decision;


import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.interpreter.VariableInterpreter;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.util.VariableUtilImpl;

public final class IsDefined implements Function {
	public static boolean call(PageContext pc , String varName) {
		
        return VariableInterpreter.isDefined(pc,varName);
		//return pc.isDefined(varName);
	}

	public static boolean call(PageContext pc , double scope,Collection.Key key) {
		try {
			Object coll = VariableInterpreter.scope(pc, (int)scope, false);
			if(coll==null) return false;
			coll=((VariableUtilImpl)pc.getVariableUtil()).get(pc,coll,key,null);
			if(coll==null)return false;
			//return pc.scope((int)scope).get(key,null)!=null; 
		} catch (Throwable t) {
	        return false;
	    }
		return true;
	}
	public static boolean call(PageContext pc , double scope,String key) {
		return call(pc, scope, KeyImpl.getInstance(key));
	}
	
	public static boolean call(PageContext pc , double scope,String[] varNames) {
		try {
			Object coll =VariableInterpreter.scope(pc, (int)scope, false); 
			//Object coll =pc.scope((int)scope); 
			for(int i=0;i<varNames.length;i++) {
				coll=pc.getVariableUtil().getCollection(pc,coll,varNames[i],null);
				if(coll==null)return false;
			}
		} catch (Throwable t) {
	        return false;
	    }
		return true; 
	}
}