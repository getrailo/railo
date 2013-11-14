package railo.runtime.functions.dynamicEvaluation;

import java.util.Iterator;
import java.util.Map.Entry;

import railo.commons.lang.SystemOut;
import railo.runtime.Component;
import railo.runtime.ComponentScope;
import railo.runtime.ComponentWrap;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.type.Collection;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.Struct;
import railo.runtime.type.UDF;
import railo.runtime.type.util.CollectionUtil;
import railo.runtime.type.util.ComponentUtil;
import railo.runtime.type.util.KeyConstants;

public final class EvaluateComponent {
	public static Object call(PageContext pc, String name, String md5, Struct sctThis) throws PageException {
		return invoke(pc, name, md5, sctThis, null);
		}
	public static Object call(PageContext pc, String name, String md5, Struct sctThis, Struct sctVariables) throws PageException {
		return invoke(pc, name, md5, sctThis, sctVariables);
	}
	public static Component invoke(PageContext pc, String name, String md5, Struct sctThis, Struct sctVariables) throws PageException {
		// Load comp
		Component comp=null;
		try {
			comp = pc.loadComponent(name);
			if(!ComponentUtil.md5(comp).equals(md5)){	
				SystemOut.printDate(pc.getConfig().getErrWriter(),"component ["+name+"] in this enviroment has not the same interface as the component to load, it is possible that one off the components has Functions added dynamicly.");
				//throw new ExpressionException("component ["+name+"] in this enviroment has not the same interface as the component to load");
			}
		}
		catch (Exception e) {
			throw Caster.toPageException(e);
		}
		setInternalState(comp,sctThis,sctVariables);
		return comp;
	}
	public static void setInternalState(Component comp, Struct sctThis, Struct sctVariables) throws PageException {
		
		// this	
		// delete this scope data members
		ComponentWrap cw = ComponentWrap.toComponentWrap(Component.ACCESS_PRIVATE,comp);
		Collection.Key[] cwKeys = CollectionUtil.keys(cw);
		Object member;
		for(int i=0;i<cwKeys.length;i++) {
			member = cw.get(cwKeys[i]);
			if(member instanceof UDF) continue;
            cw.removeEL(cwKeys[i]);
		}
		
		// set this scope data members
		Iterator<Entry<Key, Object>> it = sctThis.entryIterator();
		Entry<Key, Object> e;
		//keys = sctThis.keys();
		while(it.hasNext()) {
			e=it.next();
            comp.set(e.getKey(),e.getValue());
		}
		
	// Variables
        
        	ComponentScope scope = comp.getComponentScope();
        	
        	// delete variables scope data members
        	Key[] sKeys = CollectionUtil.keys(scope);
    		for(int i=0;i<sKeys.length;i++) {
    			if(KeyConstants._this.equals(sKeys[i])) continue;
    			if(scope.get(sKeys[i]) instanceof UDF) continue;
                scope.removeEL(sKeys[i]);
    		}
        	
        	
        	// set variables scope data members
    		it=sctVariables.entryIterator();
        	//keys = sctVariables.keys();
			while(it.hasNext()) {
				e=it.next();
				scope.set(e.getKey(),e.getValue());
			}
        
	}
}
