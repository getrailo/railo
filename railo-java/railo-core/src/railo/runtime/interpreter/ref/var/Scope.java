package railo.runtime.interpreter.ref.var;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.interpreter.VariableInterpreter;
import railo.runtime.interpreter.ref.Ref;
import railo.runtime.interpreter.ref.RefSupport;
import railo.runtime.interpreter.ref.Set;
import railo.runtime.interpreter.ref.literal.LString;

/**
 * 
 */
public final class Scope extends RefSupport implements Set {
	
	private int scope;

	/**
     * contructor of the class
	 * @param pc
	 * @param scope
	 */
	public Scope(int scope) {
		this.scope=scope;
	}
	
	@Override
	public Object getValue(PageContext pc) throws PageException {
		return VariableInterpreter.scope(pc, scope, false);
	}

	@Override
    public String getTypeName() {
		return "scope";
	}

	@Override
	public Object touchValue(PageContext pc) throws PageException {
    	return VariableInterpreter.scope(pc, scope, true);
    }

    @Override
    public Object setValue(PageContext pc,Object obj) throws PageException {
        return pc.undefinedScope().set(getKeyAsString(pc),obj);
    }

    /**
     * @return scope
     */
    public int getScope() {
        return scope;
    }

    @Override
    public Ref getParent(PageContext pc) throws PageException {
        return null;
    }
    
    @Override
    public Ref getKey(PageContext pc) throws PageException {
        return new LString(getKeyAsString(pc));
    }

    @Override
    public String getKeyAsString(PageContext pc) throws PageException {
        //return ScopeFactory.toStringScope(scope,null);
        return VariableInterpreter.scopeInt2String(scope);
    }
}
