package railo.runtime.interpreter.ref.op;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.interpreter.ref.Ref;
import railo.runtime.interpreter.ref.RefSupport;
import railo.runtime.op.Caster;


/**
 * Plus operation
 */
public final class Not extends RefSupport implements Ref {

    private Ref ref;

    /**
     * constructor of the class
     * @param ref
     */
    public Not(Ref ref) {
        this.ref=ref;
    }

    @Override
	public Object getValue(PageContext pc) throws PageException {
        return (Caster.toBooleanValue(ref.getValue(pc)))?Boolean.FALSE:Boolean.TRUE;
    }

    @Override
    public String getTypeName() {
        return "operation";
    }

}
