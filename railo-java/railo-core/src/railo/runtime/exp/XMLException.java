package railo.runtime.exp;

import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;


/**
 * XmL Exception
 */
public final class XMLException extends ExpressionException {

	/**
	 * constructor of the class
	 * @param message
	 */
	public XMLException(String message) {
		super(message);
	}

	/**
	 * constructor of the class
	 * @param message
	 * @param detail
	 */
	public XMLException(String message, String detail) {
		super(message, detail);
		
	}
	
    /**
     * @param e
     */
    public XMLException(SAXException e) {
		super(e.getMessage());
		this.setStackTrace(e.getStackTrace());
    }

    /**
     * @param e
     */
    public XMLException(DOMException e) {
		super(e.getMessage());
		this.setStackTrace(e.getStackTrace());
    }
}