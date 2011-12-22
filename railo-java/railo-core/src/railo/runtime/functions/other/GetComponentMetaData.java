/**
 * Implements the Cold Fusion Function getmetadata
 */
package railo.runtime.functions.other;

import java.util.HashMap;

import railo.runtime.Component;
import railo.runtime.InterfaceImpl;
import railo.runtime.PageContext;
import railo.runtime.PagePlus;
import railo.runtime.component.ComponentLoader;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;
import railo.runtime.type.Struct;

public final class GetComponentMetaData implements Function {

	public static Struct call(PageContext pc , Object obj) throws PageException {
		if(obj instanceof Component){
			return ((Component)obj).getMetaData(pc);
		}
		// load existing meta without loading the cfc
		try{
			PagePlus page = ComponentLoader.loadPage(pc, Caster.toString(obj), null,null);
			if(page.metaData!=null) return page.metaData;
			
		}
		catch(Throwable t){}
		
		// load the cfc when metadata was not defined before
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