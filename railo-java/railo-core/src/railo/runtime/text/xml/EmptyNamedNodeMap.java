package railo.runtime.text.xml;

import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class EmptyNamedNodeMap implements NamedNodeMap {

	@Override
	public int getLength() {
		return 0;
	}

	@Override
	public Node getNamedItem(String name) {
		return null;
	}

	@Override
	public Node getNamedItemNS(String namespaceURI, String name) {
		return null;
	}

	@Override
	public Node item(int arg0) {
		return null;
	}

	@Override
	public Node removeNamedItem(String key) throws DOMException {
		throw new DOMException(DOMException.NOT_FOUND_ERR, "NodeMap is empty");
	}

	@Override
	public Node removeNamedItemNS(String arg0, String arg1) throws DOMException {
		throw new DOMException(DOMException.NOT_FOUND_ERR, "NodeMap is empty");
	}

	@Override
	public Node setNamedItem(Node arg0) throws DOMException {
		throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, "NodeMap is read-only");
	}

	@Override
	public Node setNamedItemNS(Node arg0) throws DOMException {
		throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, "NodeMap is read-only");
	}

}
