

package railo.runtime.interpreter.ref.var;

import railo.runtime.exp.PageException;
import railo.runtime.interpreter.ref.Ref;
import railo.runtime.interpreter.ref.RefSupport;
import railo.runtime.interpreter.ref.Set;
import railo.runtime.type.scope.Argument;

public final class ArgumentBind extends RefSupport implements Set {
    
    private Scope scope;

    public ArgumentBind(Scope scope) {
        this.scope=scope;
    }

    /**
     * @return
     * @throws PageException
     */
    public Object touchValue() throws PageException {
        Object obj = scope.touchValue();
        if(obj instanceof Argument) ((Argument)obj).setBind(true);
        return obj;
    }

    /**
     * @see railo.runtime.interpreter.ref.Ref#getValue()
     */
    public Object getValue() throws PageException {
        Object obj = scope.getValue();
        if(obj instanceof Argument) ((Argument)obj).setBind(true);
        return obj;
    }

    /**
     * @see railo.runtime.interpreter.ref.Ref#getTypeName()
     */
    public String getTypeName() {
        return "argument bind";
    }

    /**
     * @see railo.runtime.interpreter.ref.Set#setValue(java.lang.Object)
     */
    public Object setValue(Object obj) throws PageException {
        return scope.setValue(obj);
    }

    /**
     * @see railo.runtime.interpreter.ref.Set#getParent()
     */
    public Ref getParent() throws PageException {
        return scope.getParent();
    }

    /**
     * @see railo.runtime.interpreter.ref.Set#getKey()
     */
    public Ref getKey() throws PageException {
        return scope.getKey();
    }

    /**
     * @see railo.runtime.interpreter.ref.Set#getKeyAsString()
     */
    public String getKeyAsString() throws PageException {
        return scope.getKeyAsString();
    }



}
