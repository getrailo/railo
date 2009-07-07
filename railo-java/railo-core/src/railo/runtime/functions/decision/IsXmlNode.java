package railo.runtime.functions.decision;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.text.xml.struct.XMLStruct;

/**
 * Check if a value is a XML Node 
 */
public final class IsXmlNode implements Function {

	public static boolean call(PageContext pc, Object value) {
	    if(value instanceof Node)return true;
	    else if(value instanceof NodeList) return ((NodeList)value).getLength()>0;
	    else if(value instanceof XMLStruct) return true;
	    return false;
	}
}