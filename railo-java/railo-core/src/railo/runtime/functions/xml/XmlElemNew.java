/**
 * Implements the CFML Function xmlelemnew
 */
package railo.runtime.functions.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.ext.function.Function;
import railo.runtime.text.xml.XMLUtil;
import railo.runtime.text.xml.struct.XMLStructFactory;

public final class XmlElemNew implements Function {
	
	private static final long serialVersionUID = -2601887739406776466L;

	public static Element call(PageContext pc , Node node, String childname) throws FunctionException {
		return call(pc, node, null, childname);
	}
	
	public static Element call(PageContext pc , Node node, String namespace, String childname) throws FunctionException {
		Document doc=XMLUtil.getDocument(node);
		
		if(StringUtil.isEmpty(childname)) {
			if(!StringUtil.isEmpty(namespace)) {
				childname=namespace;
				namespace=null;
			}
			else throw new FunctionException(pc, "XmlElemNew", 3, "childname", "argument is required");
		}
		
		Element el = null;
		
		// without namespace
		if(StringUtil.isEmpty(namespace)){
			el=doc.createElement(childname);
		}
		// with namespace
		else {
			el=doc.createElementNS(namespace, childname);
		}
		return (Element)XMLStructFactory.newInstance(el,false);
	}
	
	
	
}