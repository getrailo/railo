package railo.runtime.type.scope;

import railo.runtime.type.Scope;

/**
 * interface for the caller scope
 */
public interface Caller extends Scope {

    /**
     * sets the scopes
     * @param variablesScope
     * @param localScope
     * @param argumentsScope
     * @param checkArgs
     */
    public abstract void setScope(Scope variablesScope, Scope localScope,
            Scope argumentsScope, boolean checkArgs);

}