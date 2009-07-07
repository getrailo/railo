/**
 * Implements the Cold Fusion Function getmetadata
 */
package railo.runtime.functions.other;

import java.util.HashMap;

import railo.runtime.Component;
import railo.runtime.PageContext;
import railo.runtime.component.ComponentLoader;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.functions.displayFormatting.HTMLEditFormat;
import railo.runtime.op.Caster;
import railo.runtime.type.Struct;

public final class GetComponentMetaData implements Function {
	
	public static Struct call(PageContext pc , Object obj) throws PageException {
		if(obj instanceof Component)
			return ((Component)obj).getMetaData(pc);
		
		
		try{
			return CreateObject.doComponent(pc, Caster.toString(obj)).getMetaData(pc);
		}
		// TODO better solution
		catch(ApplicationException ae){
			return ComponentLoader.loadInterface(pc, Caster.toString(obj), true, new HashMap()).getMetaData(pc);
		}
	}
}