package railo.runtime.text.feed;

import org.xml.sax.DTDHandler;
import org.xml.sax.SAXException;

public class DummyDTDHandler implements DTDHandler {

	public void notationDecl(String arg0, String arg1, String arg2)
			throws SAXException {
	}

	public void unparsedEntityDecl(String arg0, String arg1, String arg2,
			String arg3) throws SAXException {
	}

}
