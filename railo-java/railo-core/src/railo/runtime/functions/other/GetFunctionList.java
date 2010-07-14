/**
 * Implements the Cold Fusion Function getfunctionlist
 */
package railo.runtime.functions.other;

import java.util.Iterator;
import java.util.Map;

import railo.runtime.PageContext;
import railo.runtime.config.ConfigImpl;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.transformer.library.function.FunctionLib;

public final class GetFunctionList implements Function {
	
	private static Struct functions;
	
	public synchronized static railo.runtime.type.Struct call(PageContext pc) throws PageException {
		
		
		if(functions==null) {
			Struct sct=new StructImpl();
			//synchronized(sct) {
				//hasSet=true;
			FunctionLib[] flds;
			flds = ((ConfigImpl)pc.getConfig()).getFLDs();
			
			for(int i=0;i<flds.length;i++) {
				Map functions = flds[i].getFunctions();
				Iterator it = functions.keySet().iterator();
				
				while(it.hasNext()){
					Object n=it.next();
					sct.set(n.toString(),"");
				}
			}
			functions=sct;
			//}
		}
		return functions;
	}
}