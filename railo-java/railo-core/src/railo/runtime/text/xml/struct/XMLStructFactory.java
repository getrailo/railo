package railo.runtime.text.xml.struct;

import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;


/**
 * 
 */
public final class XMLStructFactory {
	/**
	 * @param node
	 * @param caseSensitive
	 * @return XMLStruct instance
	 */
	public static XMLStruct newInstance(Node node, boolean caseSensitive) {
		// TODO set Case Sensitive
		if(node instanceof XMLStruct) return ((XMLStruct)node);
		
		if(node instanceof Document) return new XMLDocumentStruct((Document)node,caseSensitive);
        else if(node instanceof Text) return new XMLTextStruct((Text)node,caseSensitive);
        else if(node instanceof CDATASection) return new XMLCDATASectionStruct((CDATASection)node,caseSensitive);
        else if(node instanceof Element) return new XMLElementStruct((Element)node,caseSensitive);
        else if(node instanceof Attr) return new XMLAttrStruct((Attr)node,caseSensitive);
        
        else return new XMLNodeStruct(node,caseSensitive);
	}
}