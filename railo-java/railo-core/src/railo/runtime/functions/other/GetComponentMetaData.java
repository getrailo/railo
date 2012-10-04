/**
 * Implements the CFML Function getmetadata
 */
package railo.runtime.functions.other;

import java.util.HashMap;

import railo.runtime.Component;
import railo.runtime.InterfaceImpl;
import railo.runtime.Page;
import railo.runtime.PageContext;
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
			Page page = ComponentLoader.loadPage(pc, Caster.toString(obj), null,null);
			if(page.metaData!=null && page.metaData.get()!=null) return page.metaData.get();
		}
		catch(Throwable t){}
		
		/*try{
			PagePlus page = ComponentLoader.loadPage(pc, Caster.toString(obj), null,null);
			Struct meta=null;
			if(page!=null)meta=GetMetaData.getMetaData(page.getPageSource());
			if(meta!=null) return meta;
			
		}
		catch(Throwable t){}*/
		
		
		// load the cfc when metadata was not defined before
		try{
			Component cfc = CreateObject.doComponent(pc, Caster.toString(obj));
			return GetMetaData.getMetaData(cfc, pc);
		}
		// TODO better solution
		catch(ApplicationException ae){
			try{
				InterfaceImpl inter = ComponentLoader.loadInterface(pc, Caster.toString(obj), new HashMap());
				return inter.getMetaData(pc);
			}
			catch(PageException pe){
				throw ae;
			}
		}
	}
}