

package railo.runtime.interpreter.ref.literal;

import railo.runtime.exp.PageException;
import railo.runtime.interpreter.ref.Ref;
import railo.runtime.interpreter.ref.RefSupport;
import railo.runtime.interpreter.ref.Set;
import railo.runtime.type.FunctionValueImpl;

/**
 * ref for a functionValue
 */
public final class LFunctionValue extends RefSupport implements Ref {


    private Ref name;
    private Ref value;

    /**
     * constructor of the class
     * @param name
     * @param value
     */
    public LFunctionValue(Ref name, Ref value) {
        this.name=name;
        this.value=value;
    }

    public Object getValue() throws PageException {
        
        String key=null;
        Ref ref=name;
        Set set;
        
        while(ref instanceof Set) {
            set=(Set) ref;
            if(key==null) key=set.getKeyAsString();
            else if(set.getKeyAsString()!=null)key=set.getKeyAsString()+'.'+key;
            ref=set.getParent();
        }
        if(ref instanceof Literal) {
            if(key==null) key=((Literal)name).getString();
            else key=((Literal)name).getString()+'.'+key;
        }
        
        return new FunctionValueImpl(key,value.getValue());
    }

    /**
     * @see railo.runtime.interpreter.ref.Ref#getTypeName()
     */
    public String getTypeName() {
        return "function value";
    }

}
