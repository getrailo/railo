/**
 * Implements the Cold Fusion Function getmetadata
 */
package railo.runtime.functions.other;

import java.util.HashMap;

import railo.runtime.Component;
import railo.runtime.InterfaceImpl;
import railo.runtime.PageContext;
import railo.runtime.PageSource;
import railo.runtime.component.ComponentLoader;
import railo.runtime.config.ConfigImpl;
import railo.runtime.config.ConfigWebImpl;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.util.ComponentUtil;

public final class GetComponentMetaData implements Function {
	
	
	private static final Collection.Key FUNCTIONS = KeyImpl.init("functions");

	public static Struct call(PageContext pc , Object obj) throws PageException {
		if(obj instanceof Component){
			return ((Component)obj).getMetaData(pc);
		}
		
		try{
			Component cfc = CreateObject.doComponent(pc, Caster.toString(obj));
			return cfc.getMetaData(pc);
		}
		// TODO better solution
		catch(ApplicationException ae){
			InterfaceImpl inter = ComponentLoader.loadInterface(pc, Caster.toString(obj), new HashMap());
			return inter.getMetaData(pc);
		}
	}
}