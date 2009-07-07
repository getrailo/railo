/**
 * Implements the Cold Fusion Function xmlelemnew
 */
package railo.runtime.functions.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.text.xml.XMLUtil;
import railo.runtime.text.xml.struct.XMLStructFactory;

public final class XmlElemNew implements Function {
	public static Element call(PageContext pc , Node node, String string) {
		Document doc=XMLUtil.getDocument(node);
		Element el = doc.createElement(string);
		
		return (Element)XMLStructFactory.newInstance(el,false);

	}
}