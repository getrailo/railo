package railo.runtime.functions.dynamicEvaluation;

import railo.commons.lang.SystemOut;
import railo.runtime.Component;
import railo.runtime.ComponentImpl;
import railo.runtime.ComponentScope;
import railo.runtime.ComponentWrap;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.type.Collection;
import railo.runtime.type.Struct;
import railo.runtime.type.UDF;
import railo.runtime.type.util.ComponentUtil;

public final class EvaluateComponent {
	public static Object call(PageContext pc, String name, String md5, Struct sctThis) throws PageException {
		return call(pc, name, md5, sctThis, null);
	}
	public static Object call(PageContext pc, String name, String md5, Struct sctThis, Struct sctVariables) throws PageException {
		
		// Load comp
		Component comp=null;
		try {
			comp = pc.loadComponent(name);
			if(!ComponentUtil.md5(comp).equals(md5)){	
				SystemOut.printDate(pc.getConfig().getErrWriter(),"component ["+name+"] in this enviroment has not the same interface as the component to load");
				//throw new ExpressionException("component ["+name+"] in this enviroment has not the same interface as the component to load");
			}
		}
		catch (Exception e) {
			throw Caster.toPageException(e);
		}
		
		ComponentImpl ci = ComponentUtil.toComponentImpl(comp);
		
	// this	
		// delete this scope data members
		ComponentWrap cw = new ComponentWrap(Component.ACCESS_PRIVATE,ci);
		Collection.Key[] keys = cw.keys();
		Object member;
		for(int i=0;i<keys.length;i++) {
			member = cw.get(keys[i]);
			if(member instanceof UDF) continue;
            cw.removeEL(keys[i]);
		}
		
		// set this scope data members
		keys = sctThis.keys();
		for(int i=0;i<keys.length;i++) {
            comp.set(keys[i],sctThis.get(keys[i]));
		}
		
	// Variables
		boolean isWrap=comp instanceof ComponentWrap;
        if(isWrap || comp instanceof ComponentImpl){
        	ComponentScope scope = ci.getComponentScope();
        	
        	// delete variables scope data members
        	keys = scope.keys();
    		for(int i=0;i<keys.length;i++) {
    			if("this".equalsIgnoreCase(keys[i].getString())) continue;
    			if(scope.get(keys[i]) instanceof UDF) continue;
                scope.removeEL(keys[i]);
    		}
        	
        	
        	// set variables scope data members
        	keys = sctVariables.keys();
			for(int i=0;i<keys.length;i++) {
				scope.set(keys[i],sctVariables.get(keys[i]));
			}
        }
        return comp;
	}
}
