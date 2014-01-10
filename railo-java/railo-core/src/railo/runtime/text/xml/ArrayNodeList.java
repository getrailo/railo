package railo.runtime.text.xml;

import java.util.ArrayList;

import org.w3c.dom.Node;


/**
 * 
 */
public final class ArrayNodeList extends ArrayList<Node> implements org.w3c.dom.NodeList {

	private static final long serialVersionUID = 8355573954254967533L;

	public ArrayNodeList() {
	}
	
	@Override
	public int getLength() {
		return size();
	}
	
	@Override
	public Node item(int index) {
		return get(index);
	}
}