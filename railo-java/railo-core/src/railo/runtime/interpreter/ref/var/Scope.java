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
	
	private PageContext pc;
	private int scope;

	/**
     * contructor of the class
	 * @param pc
	 * @param scope
	 */
	public Scope(PageContext pc, int scope) {
		this.pc=pc;
		this.scope=scope;
	}
	
	/**
	 * @see railo.runtime.interpreter.ref.Ref#getValue()
	 */
	public Object getValue() throws PageException {
		return VariableInterpreter.scope(pc, scope, false);
	}

	/**
	 * @see railo.runtime.interpreter.ref.Ref#getTypeName()
	 */
	public String getTypeName() {
		return "scope";
	}

    /**
     * @see railo.runtime.interpreter.ref.Ref#touchValue()
     */
    public Object touchValue() throws PageException {
    	return VariableInterpreter.scope(pc, scope, true);
    }

    /**
     * @see railo.runtime.interpreter.ref.Set#setValue(java.lang.Object)
     */
    public Object setValue(Object obj) throws PageException {
        return pc.undefinedScope().set(getKeyAsString(),obj);
    }

    /**
     * @return scope
     */
    public int getScope() {
        return scope;
    }

    /**
     * @see railo.runtime.interpreter.ref.Set#getParent()
     */
    public Ref getParent() throws PageException {
        return null;
    }

    /**
     * @see railo.runtime.interpreter.ref.Set#getKey()
     */
    public Ref getKey() throws PageException {
        return new LString(getKeyAsString());
    }

    /**
     * @see railo.runtime.interpreter.ref.Set#getKey()
     */
    public String getKeyAsString() throws PageException {
        //return ScopeFactory.toStringScope(scope,null);
        return VariableInterpreter.scopeInt2String(scope);
    }
}
