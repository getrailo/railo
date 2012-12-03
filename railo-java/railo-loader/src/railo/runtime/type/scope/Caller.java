package railo.runtime.type.scope;


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
    public abstract void setScope(Variables variablesScope, Local localScope,
            Argument argumentsScope, boolean checkArgs);
    

    /**
	 * @return the variablesScope
	 */
	public Variables getVariablesScope();

	/**
	 * @return the localScope
	 */
	public Local getLocalScope();

	/**
	 * @return the argumentsScope
	 */
	public Argument getArgumentsScope();

}