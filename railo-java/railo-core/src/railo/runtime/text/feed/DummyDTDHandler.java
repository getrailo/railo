package railo.runtime.text.feed;

import org.xml.sax.DTDHandler;
import org.xml.sax.SAXException;

public class DummyDTDHandler implements DTDHandler {

	@Override
	public void notationDecl(String arg0, String arg1, String arg2)
			throws SAXException {
	}

	@Override
	public void unparsedEntityDecl(String arg0, String arg1, String arg2,
			String arg3) throws SAXException {
	}

}
