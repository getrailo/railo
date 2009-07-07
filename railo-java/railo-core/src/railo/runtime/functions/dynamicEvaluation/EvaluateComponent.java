package railo.runtime.functions.dynamicEvaluation;

import railo.runtime.Component;
import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.type.Collection;
import railo.runtime.type.Struct;
import railo.runtime.type.util.ComponentUtil;

public final class EvaluateComponent {
	public static Object call(PageContext pc, String name, String md5, Struct arguments) throws PageException {
		
		// Load comp
		Component comp=null;
		try {
			comp = pc.loadComponent(name);
			if(!ComponentUtil.md5(comp).equals(md5)){
				throw new ExpressionException("component ["+name+"] in this enviroment has not the same interface as the component to load");
			}
		}
		catch (Exception e) {
			throw Caster.toPageException(e);
		}
		
		// overwrite arguments
		Collection.Key[] keys = arguments.keys();
		for(int i=0;i<keys.length;i++) {
            comp.set(keys[i],arguments.get(keys[i]));
		}
        return comp;
	}
}
