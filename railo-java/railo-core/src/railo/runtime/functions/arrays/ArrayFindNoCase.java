package railo.runtime.functions.arrays;


import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;

public final class ArrayFindNoCase extends BIF {

	private static final long serialVersionUID = 3636181944643536992L;

	public static double call(PageContext pc , Array array, Object value) {
        return ArrayFind.find(array,value,false);
    }
	
	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		return call(pc,Caster.toArray(args[0]),args[1]);
	}
}