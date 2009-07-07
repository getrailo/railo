package railo.runtime.exp;


/**
 * exception to throw invalid attributes of tags
 */
public final class TagAttributeException extends ApplicationException {
	
	/**
	 * constructor of the class
	 * @param attributeName 
	 * @param message
	 */
	public TagAttributeException(String attributeName,String message) {
		super("attribute "+attributeName+" is invalid",message);
	}
}