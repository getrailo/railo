package railo.runtime.interpreter.ref.var;

import railo.runtime.PageContext;
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

    @Override
	public Object touchValue(PageContext pc) throws PageException {
        Object obj = scope.touchValue(pc);
        if(obj instanceof Argument) ((Argument)obj).setBind(true);
        return obj;
    }

    @Override
	public Object getValue(PageContext pc) throws PageException {
        Object obj = scope.getValue(pc);
        if(obj instanceof Argument) ((Argument)obj).setBind(true);
        return obj;
    }

    @Override
    public String getTypeName() {
        return "argument bind";
    }

    @Override
    public Object setValue(PageContext pc,Object obj) throws PageException {
        return scope.setValue(pc,obj);
    }

    @Override
    public Ref getParent(PageContext pc) throws PageException {
        return scope.getParent(pc);
    }

    @Override
    public Ref getKey(PageContext pc) throws PageException {
        return scope.getKey(pc);
    }

    @Override
    public String getKeyAsString(PageContext pc) throws PageException {
        return scope.getKeyAsString(pc);
    }
}
