/**
 * Implements the Cold Fusion Function structcopy
 */
package railo.runtime.functions.struct;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Duplicator;
import railo.runtime.type.Array;
import railo.runtime.type.Collection;
import railo.runtime.type.Struct;

public final class StructCopy implements Function {
	public static Object call(PageContext pc , Struct src) throws PageException {
		
		Collection trg = (Collection) Duplicator.duplicate(src,false);
		
		
		Collection.Key[] keys=trg.keys();
		Collection.Key key;
		Object o;
		for(int i=0;i<keys.length;i++) {
			key=keys[i];
			o=src.get(key,null);
			if(o instanceof Array)
				trg.set(key,Duplicator.duplicate(o,false));
		}
		return trg;
	}
}