

package railo.runtime.interpreter.ref.op;

import railo.runtime.exp.PageException;
import railo.runtime.interpreter.ref.Ref;
import railo.runtime.interpreter.ref.RefSupport;
import railo.runtime.op.Caster;


/**
 * Plus operation
 */
public final class Negate extends RefSupport implements Ref {

    private Ref ref;

    /**
     * constructor of the class
     * @param ref
     */
    public Negate(Ref ref) {
        this.ref=ref;
    }

    /**
     * @see railo.runtime.interpreter.ref.Ref#getValue()
     */
    public Object getValue() throws PageException {
        return new Double(-Caster.toDoubleValue(ref.getValue()));
    }

    /**
     * @see railo.runtime.interpreter.ref.Ref#getTypeName()
     */
    public String getTypeName() {
        return "operation";
    }

}
