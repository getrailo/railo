

package railo.runtime.interpreter.ref;

import railo.runtime.exp.PageException;

public interface Set extends Ref {
    /**
     * @return sets a value 
     * @throws PageException 
     */
    public Object setValue(Object obj) throws PageException;

    public Ref getParent() throws PageException;

    public Ref getKey() throws PageException;
    
    public String getKeyAsString() throws PageException;

}
