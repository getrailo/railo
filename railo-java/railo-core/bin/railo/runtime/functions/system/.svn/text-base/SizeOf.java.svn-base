package railo.runtime.functions.system;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;

public final class SizeOf implements Function {
	public static double call(PageContext pc , Object object) {
		return Caster.toDoubleValue(railo.commons.lang.SizeOf.size(object));
	}
}