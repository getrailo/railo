package railo.runtime.text.xml;

import java.util.ArrayList;

import org.w3c.dom.Node;


/**
 * 
 */
public final class ArrayNodeList extends ArrayList implements org.w3c.dom.NodeList {
	
	public ArrayNodeList() {
	}
	
	/**
	 * @see org.w3c.dom.NodeList#getLength()
	 */
	public int getLength() {
		return size();
	}
	/**
	 * @see org.w3c.dom.NodeList#item(int)
	 */
	public Node item(int index) {
		//synchronized (o) {
			return (Node)get(index);
		//}
	}
}