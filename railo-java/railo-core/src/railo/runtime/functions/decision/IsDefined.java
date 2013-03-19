/**
 * Implements the CFML Function isdefined
 */
package railo.runtime.functions.decision;

import railo.runtime.PageContext;
import railo.runtime.config.NullSupportHelper;
import railo.runtime.ext.function.Function;
import railo.runtime.interpreter.VariableInterpreter;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Null;
import railo.runtime.type.scope.Scope;
import railo.runtime.util.VariableUtilImpl;

public final class IsDefined implements Function {
	
	private static final long serialVersionUID = -6477602189364145523L;

	public static boolean call(PageContext pc , String varName) {
		return VariableInterpreter.isDefined(pc,varName);
		//return pc.isDefined(varName);
	}
	
	public static boolean call(PageContext pc , double scope,Collection.Key key) {
		try {
			Object coll = VariableInterpreter.scope(pc, (int)scope, false);
			if(coll==null) return false;
			coll=((VariableUtilImpl)pc.getVariableUtil()).get(pc,coll,key,NullSupportHelper.NULL());
			if(coll==NullSupportHelper.NULL())return false;
			//return pc.scope((int)scope).get(key,null)!=null; 
		} catch (Throwable t) {
	        return false;
	    }
		return true;
	}

	public static boolean call(PageContext pc , double scope,Collection.Key[] varNames) {
		Object defVal=NullSupportHelper.NULL();
		try {
			Object coll =VariableInterpreter.scope(pc, (int)scope, false); 
			//Object coll =pc.scope((int)scope);
			VariableUtilImpl vu = ((VariableUtilImpl)pc.getVariableUtil());
			for(int i=0;i<varNames.length;i++) {
				coll=vu.getCollection(pc,coll,varNames[i],defVal);
				if(coll==defVal)return false;
			}
		} catch (Throwable t) {
	        return false;
	    }
		return true; 
	}
	
	// used for older compiled code in ra files
	public static boolean invoke(PageContext pc , String[] varNames, boolean allowNull) {
		int scope = VariableInterpreter.scopeString2Int(varNames[0]);
		
		
		Object defVal=allowNull?Null.NULL:null;
		try {
			Object coll =VariableInterpreter.scope(pc, scope, false); 
			//Object coll =pc.scope((int)scope); 
			for(int i=scope==Scope.SCOPE_UNDEFINED?0:1;i<varNames.length;i++) {
				coll=pc.getVariableUtil().getCollection(pc,coll,varNames[i],defVal);
				if(coll==defVal)return false;
			}
		} catch (Throwable t) {
	        return false;
	    }
		return true; 
	}
		
	// used for older compiled code in ra files
	public static boolean call(PageContext pc , double scope,String key) {
		return call(pc, scope, KeyImpl.getInstance(key));
	}

	// used for older compiled code in ra files
	public static boolean call(PageContext pc , double scope,String[] varNames) {
		return call(pc, scope, KeyImpl.toKeyArray(varNames));
	}
}