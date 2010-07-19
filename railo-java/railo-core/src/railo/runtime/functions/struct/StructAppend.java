/**
 * Implements the Cold Fusion Function structappend
 */
package railo.runtime.functions.struct;

import java.util.Iterator;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;

public final class StructAppend implements Function {
	public static boolean call(PageContext pc , Struct struct1, Struct struct2) throws PageException {
		return call(pc , struct1, struct2, true);
	}
    public static boolean call(PageContext pc , Struct struct1, Struct struct2, boolean overwrite) throws PageException {
        Iterator it = struct2.keyIterator();
        Key key;
        while(it.hasNext()) {
            key=KeyImpl.toKey(it.next());
            if(overwrite || struct1.get(key,null)==null)struct1.set(key,struct2.get(key));
        }
        return true;
    }
    
}