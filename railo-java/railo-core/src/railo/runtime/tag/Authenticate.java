package railo.runtime.tag;

import railo.runtime.exp.ExpressionException;
import railo.runtime.ext.tag.TagImpl;

/**
* Authenticates a user, setting a security context for the application. 
* 			For more information, see the descriptions of IsAuthenticated and AuthenticatedContext.
*
*
*
**/
public final class Authenticate extends TagImpl {

	/**
	* constructor for the tag class
	 * @throws ExpressionException
	**/
	public Authenticate() throws ExpressionException {
		throw new ExpressionException("tag cfauthenticate is deprecated");
	}
}