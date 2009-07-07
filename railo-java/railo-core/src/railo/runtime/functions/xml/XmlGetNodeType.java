package railo.runtime.functions.xml;

import org.w3c.dom.Node;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.text.xml.XMLUtil;

/**
 * 
 */
public final class XmlGetNodeType implements Function {

	public static String call(PageContext pc, Node node) {
	    return XMLUtil.getTypeAsString(node,false);
	}
}