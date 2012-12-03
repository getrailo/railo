package railo.runtime.functions.system;

import railo.commons.lang.SizeAndCount;
import railo.commons.lang.SizeAndCount.Size;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.util.KeyConstants;

public final class SizeOf implements Function {
	public static Object call(PageContext pc , Object object) throws PageException {
		return call(pc, object,false);
	}
	public static Object call(PageContext pc , Object object, boolean complex) throws PageException {
		Size size = SizeAndCount.sizeOf(object);
		if(!complex)
			return Caster.toDouble(size.size);
		
		Struct sct=new StructImpl();
		sct.set(KeyConstants._size, Caster.toDouble(size.size));
		sct.set(KeyConstants._count, Caster.toDouble(size.count));
		return sct;
	}
}