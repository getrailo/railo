package railo.runtime.interpreter.ref.var;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.interpreter.ref.Ref;
import railo.runtime.interpreter.ref.RefSupport;
import railo.runtime.op.Caster;

/**
 * 
 */
public final class DynAssign extends RefSupport implements Ref {
    
    private Ref value;
    private Ref key;
    private PageContext pc;


    /**
     * @param pc
     * @param key
     * @param value
     */
    public DynAssign(PageContext pc,Ref key, Ref value) {
        this.pc=pc;
        this.key=key;
        this.value=value;
    }
    
    
    /**
     * @see railo.runtime.interpreter.ref.Ref#getValue()
     */
    public Object getValue() throws PageException {
        return pc.setVariable(Caster.toString(key.getValue()),value.getValue());
    }

    /**
     * @see railo.runtime.interpreter.ref.Ref#getTypeName()
     */
    public String getTypeName() {
        return "operation";
    }
}
