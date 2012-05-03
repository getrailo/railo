/**
 * Implements the Cold Fusion Function structkeylist
 */
package railo.runtime.functions.struct;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.util.CollectionUtil;

public final class StructKeyList implements Function {
	public static String call(PageContext pc , Struct struct) {
		return call(pc,struct,",");//KeyImpl.toUpperCaseList(struct.keys(), ",");
	}
	public static String call(PageContext pc , Struct struct, String delimiter) {
		return KeyImpl.toList(CollectionUtil.keys(struct), delimiter);
		
	}
}