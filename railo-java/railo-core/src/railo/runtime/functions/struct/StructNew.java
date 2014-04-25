/**
 * Implements the CFML Function structnew
 */
package railo.runtime.functions.struct;

import railo.runtime.PageContext;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;

public final class StructNew extends BIF {

	private static final long serialVersionUID = 2439168907287957648L;

	public static Struct call(PageContext pc ) {
        return new StructImpl();
    }
    public static Struct call(PageContext pc ,String type) throws ApplicationException {
    	return new StructImpl(toType(type));
    }
    
    public static int toType(String type) throws ApplicationException {
    	type=type.toLowerCase();
    	if(type.equals("linked")) return Struct.TYPE_LINKED;
    	else if(type.equals("weaked")) return Struct.TYPE_WEAKED;
    	else if(type.equals("weak")) return Struct.TYPE_WEAKED;
        else if(type.equals("syncronized")) return Struct.TYPE_SYNC;
        else if(type.equals("synchronized")) return Struct.TYPE_SYNC;
        else if(type.equals("sync")) return Struct.TYPE_SYNC;
        else if(type.equals("soft")) return Struct.TYPE_SOFT;
        else if(type.equals("normal")) return Struct.TYPE_REGULAR;
        else if(type.equals("regular")) return Struct.TYPE_REGULAR;
        else throw new ApplicationException("valid struct types are [normal, weak, linked, soft, synchronized]");
    }
    
	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		if(args.length==1) return call(pc,Caster.toString(args[0]));
		return call(pc);
		
	}
}