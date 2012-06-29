package railo.runtime.interpreter.ref;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;

public interface Set extends Ref {
    /**
     * @return sets a value 
     * @throws PageException 
     */
    public Object setValue(PageContext pc,Object obj) throws PageException;

    public Ref getParent(PageContext pc) throws PageException;

    public Ref getKey(PageContext pc) throws PageException;
    
    public String getKeyAsString(PageContext pc) throws PageException;

}
