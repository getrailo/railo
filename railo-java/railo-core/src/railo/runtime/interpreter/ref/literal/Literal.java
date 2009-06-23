

package railo.runtime.interpreter.ref.literal;

import railo.runtime.exp.PageException;
import railo.runtime.interpreter.ref.Ref;

/**
 * a literal value
 */
public interface Literal extends Ref {

    /**
     * cast literal to a string
     * @return casted string
     * @throws PageException
     */
    String getString() throws PageException;

}
