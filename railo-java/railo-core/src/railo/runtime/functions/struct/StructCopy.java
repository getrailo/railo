/**
 * Implements the CFML Function structcopy
 */
package railo.runtime.functions.struct;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.op.Duplicator;
import railo.runtime.type.Array;
import railo.runtime.type.Collection;
import railo.runtime.type.Struct;
import railo.runtime.type.util.CollectionUtil;

public final class StructCopy extends BIF {
	
	private static final long serialVersionUID = 4395420120630859733L;

	public static Object call(PageContext pc , Struct src) throws PageException {
		
		Collection trg = (Collection) Duplicator.duplicate(src,false);
		Collection.Key[] keys=CollectionUtil.keys(trg);
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

	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		return call(pc, Caster.toStruct(args[0]));
	}
}