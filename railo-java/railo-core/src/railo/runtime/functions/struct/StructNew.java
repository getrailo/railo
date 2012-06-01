/**
 * Implements the CFML Function structnew
 */
package railo.runtime.functions.struct;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;

public final class StructNew implements Function {
    public static Struct call(PageContext pc ) {
        return new StructImpl();
    }
    public static Struct call(PageContext pc ,String type) throws FunctionException {
        type=type.toLowerCase();
        if(type.equals("linked")) return new StructImpl(Struct.TYPE_LINKED);
        else if(type.equals("weak")) return new StructImpl(Struct.TYPE_WEAKED);
        else if(type.equals("syncronized")) return new StructImpl(Struct.TYPE_SYNC);
        else if(type.equals("soft")) return new StructImpl(StructImpl.TYPE_SOFT);
        else if(type.equals("normal")) return new StructImpl();
        
        else throw new FunctionException(pc,"structNew",1,"type","valid values are [normal, weak, linked,syncronized]");
    }
}