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
    

    /**
     * @param pc
     * @param key
     * @param value
     */
    public DynAssign(Ref key, Ref value) {
        this.key=key;
        this.value=value;
    }
    
    
    @Override
	public Object getValue(PageContext pc) throws PageException {
        return pc.setVariable(Caster.toString(key.getValue(pc)),value.getValue(pc));
    }

    @Override
    public String getTypeName() {
        return "operation";
    }
}
