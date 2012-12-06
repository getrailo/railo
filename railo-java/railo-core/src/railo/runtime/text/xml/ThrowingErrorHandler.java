package railo.runtime.text.xml;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


public class ThrowingErrorHandler implements ErrorHandler {

	private boolean throwFatalError;
	private boolean throwError;
	private boolean throwWarning;

	public ThrowingErrorHandler(boolean throwFatalError,boolean throwError,boolean throwWarning) {
		this.throwFatalError=throwFatalError;
		this.throwError=throwError;
		this.throwWarning=throwWarning;
	}
	
	@Override
	public void error(SAXParseException e) throws SAXException {
		if(throwError)throw new SAXException(e);
	}

	@Override
	public void fatalError(SAXParseException e) throws SAXException {
		if(throwFatalError)throw new SAXException(e);
	}

	@Override
	public void warning(SAXParseException e) throws SAXException {
		if(throwWarning)throw new SAXException(e);
	}

}
