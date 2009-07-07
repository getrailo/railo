package railo.runtime.type.ref;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.interpreter.VariableInterpreter;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Scope;

/**
 * reference to a scope
 */
public final class ScopeReference implements Reference {
	
	//private String scopeName;
	private Scope scope;
	private PageContext pc;
	
	/**
	 * constructor of the class
	 * @param pc
	 * @param scopeName
	 * @throws PageException 
	 */
	public ScopeReference(PageContext pc, String scopeName) throws PageException {
		this.pc=pc;
		//this.scopeName=scopeName;
		this.scope=pc.scope(VariableInterpreter.scopeString2Int(scopeName));
	}

	/**
     * constructor of the class
	 * @param pc
	 * @param scope
	 * @throws PageException 
     */
    public ScopeReference(PageContext pc, int scope) throws PageException {
		this.pc=pc;
		//this.scopeName=pc.getVariableStringUtil().scopeInt2String(scope);
		this.scope=pc.scope(scope);
    }

    /**
     * @see railo.runtime.type.ref.Reference#get(railo.runtime.PageContext)
     */
    public Object get(PageContext pc) {
        return scope;
    }
    
    /**
     * @see railo.runtime.type.ref.Reference#get(railo.runtime.PageContext, java.lang.Object)
     */
    public Object get(PageContext pc, Object defaultValue) {
        return scope;
    }

    /**
     * @see railo.runtime.type.ref.Reference#touch(railo.runtime.PageContext)
     */
    public Object touch(PageContext pc) {
        return scope;
    }
    
    public Object touchEL(PageContext pc) {
        return scope;
    }

	/**
	 * @see railo.runtime.type.ref.Reference#set(railo.runtime.PageContext, java.lang.Object)
	 */
	public Object set(PageContext pc,Object value) throws PageException {
		pc.undefinedScope().set(scope.getTypeAsString(),value);
		return null;
	}
	
	/**
	 * @see railo.runtime.type.ref.Reference#setEL(railo.runtime.PageContext, java.lang.Object)
	 */
	public Object setEL(PageContext pc,Object value) {
		pc.undefinedScope().setEL(scope.getTypeAsString(),value);
		return null;
	}

	/**
	 * @see railo.runtime.type.ref.Reference#remove(PageContext pc)
	 */
	public Object remove(PageContext pc) {
		scope.clear();
		return scope;
	}
	
	/**
	 * @see railo.runtime.type.ref.Reference#removeEL(railo.runtime.PageContext)
	 */
	public Object removeEL(PageContext pc) {
		scope.clear();
		return scope;
	}

	/**
	 * @see railo.runtime.type.ref.Reference#getParent()
	 */
	public Object getParent() {
		return pc.undefinedScope();
	}

	/**
	 *
	 * @see railo.runtime.type.ref.Reference#getKeyAsString()
	 */
	public String getKeyAsString() throws PageException {
		return scope.getTypeAsString();
	}

	/**
	 *
	 * @see railo.runtime.type.ref.Reference#getKey()
	 */
	public Collection.Key getKey() throws PageException {
		return KeyImpl.init(scope.getTypeAsString());
	}
	
}