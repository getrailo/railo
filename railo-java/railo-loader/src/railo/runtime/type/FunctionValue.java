package railo.runtime.type;

import java.io.Serializable;

import railo.runtime.dump.Dumpable;
import railo.runtime.op.Castable;

/**
 * represent a named function value for a functions
 */
public interface FunctionValue extends Castable,Serializable,Dumpable {

    /**
     * @return Returns the name.
     * @deprecated use instead <code>getNameAsString();</code>
     */
    public abstract String getName();
    
    /**
     * @return Returns the name as string
     */
    public String getNameAsString();
    
    /**
     * @return Returns the name as key
     */
    public Collection.Key getNameAsKey();

    /**
     * @return Returns the value.
     */
    public abstract Object getValue();

}