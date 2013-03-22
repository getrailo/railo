package railo.runtime.security;

import railo.runtime.converter.ScriptConvertable;
import railo.runtime.exp.PageException;

/**
 * Credential interface
 */
public interface Credential extends ScriptConvertable{

    /**
     * @return Returns the password.
     */
    public abstract String getPassword();

    /**
     * @return Returns the roles.
     */
    public abstract String[] getRoles();

    /**
     * @return Returns the username.
     */
    public abstract String getUsername();

    /**
     * encode rhe Credential to a Base64 String value
     * @return base64 encoded string
     * @throws PageException
     */
    public abstract String encode() throws PageException;

}