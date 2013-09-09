/**
 * Implements the CFML Function structappend
 */
package railo.runtime.functions.struct;

import java.util.Iterator;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;

public final class StructAppend extends BIF {

	private static final long serialVersionUID = 6131382324325758447L;

	public static boolean call(PageContext pc , Struct struct1, Struct struct2) throws PageException {
		return call(pc , struct1, struct2, true);
	}
    public static boolean call(PageContext pc , Struct struct1, Struct struct2, boolean overwrite) throws PageException {
        Iterator<Key> it = struct2.keyIterator();
        Key key;
        while(it.hasNext()) {
            key=KeyImpl.toKey(it.next());
            if(overwrite || struct1.get(key,null)==null)
            	struct1.setEL(key,struct2.get(key,null));
        }
        return true;
    }
	
	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		if(args.length==3) return call(pc,Caster.toStruct(args[0]),Caster.toStruct(args[1]),Caster.toBooleanValue(args[2]));
		return call(pc,Caster.toStruct(args[0]),Caster.toStruct(args[1]));
	}
    
}