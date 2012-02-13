package railo.runtime.exp;


/**
 * 
 */
public class AbortException extends ApplicationException {

	/**
	 * constructor of the class
	 * @param showError error message from abort Tag
	 */
	public AbortException(String showError) {
		super(showError);
	}
	
}