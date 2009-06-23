

package railo.runtime.interpreter.ref.op;

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

    /**
     * @see railo.runtime.interpreter.ref.Ref#getValue()
     */
    public Object getValue() throws PageException {
        return (Caster.toBooleanValue(ref.getValue()))?Boolean.FALSE:Boolean.TRUE;
    }

    /**
     * @see railo.runtime.interpreter.ref.Ref#getTypeName()
     */
    public String getTypeName() {
        return "operation";
    }

}
