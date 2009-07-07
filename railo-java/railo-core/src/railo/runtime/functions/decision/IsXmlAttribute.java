package railo.runtime.functions.decision;

import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.text.xml.struct.XMLStruct;

/**
 * Check if a value is a XML Attribut (XML Attr Node)
 */
public final class IsXmlAttribute implements Function {

	public static boolean call(PageContext pc, Object value) {
	    
	    if(value instanceof Attr)return true;
	    else if(value instanceof NodeList) return ((NodeList)value).item(0).getNodeType()==Node.ATTRIBUTE_NODE;
	    else if(value instanceof XMLStruct) return ((XMLStruct)value).getNodeType()==Node.ATTRIBUTE_NODE;
	    return false;
	}
}