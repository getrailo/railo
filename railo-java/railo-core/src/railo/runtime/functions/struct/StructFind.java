/**
 * Implements the CFML Function structfind
 */
package railo.runtime.functions.struct;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;

public final class StructFind implements Function {
	public static Object call(PageContext pc , Struct struct, String key) throws PageException {
		return struct.get(KeyImpl.init(key));
	}
	public static Object call(PageContext pc , Struct struct, Collection.Key key) throws PageException {
		return struct.get(key);
	}
}