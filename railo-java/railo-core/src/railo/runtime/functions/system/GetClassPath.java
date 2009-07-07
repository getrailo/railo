package railo.runtime.functions.system;

import railo.commons.lang.ClassUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;

public final class GetClassPath {

	public static Array call(PageContext pc ) throws PageException {
		return Caster.toArray(ClassUtil.getClassPath(pc.getConfig())); 
    }
    
}
