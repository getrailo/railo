package railo.runtime.interpreter.ref.cast;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.interpreter.ref.Ref;
import railo.runtime.interpreter.ref.RefSupport;
import railo.runtime.op.Caster;

/**
 * cast
 */
public final class Casting extends RefSupport implements Ref {
    
    private short type;
    private Ref ref;
    private PageContext pc;
    private String strType;
    
    /**
     * constructor of the class
     * @param pc 
     * @param strType 
     * @param type
     * @param ref
     */
    public Casting(PageContext pc,String strType,short type, Ref ref) {
        this.pc=pc;
        this.type=type;
        this.strType=strType;
        this.ref=ref;
    }
    
    /**
     * @see railo.runtime.interpreter.ref.Ref#getValue()
     */
    public Object getValue() throws PageException {
        return Caster.castTo(pc,type,strType,ref.getValue());
    }

    public String getTypeName() {
        return "operation";
    }
}
