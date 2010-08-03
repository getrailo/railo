package railo.runtime.functions.string;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

public final class Metaphone implements Function {
	public static String call(PageContext pc, String str) {
		return new org.apache.commons.codec.language.Metaphone().metaphone(str);
	}
	
}