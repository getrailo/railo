
package railo.runtime.functions.xml;

import org.w3c.dom.Node;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.text.xml.XMLNodeList;
import railo.runtime.text.xml.XMLUtil;

/**
 * Implements the CFML Function xmlchildpos
 */
public final class XmlChildPos implements Function {
	public static double call(PageContext pc , Node node, String name, double index) {
		XMLNodeList xmlNodeList = new XMLNodeList(node,false);
		int len=xmlNodeList.getLength();
		// if(index<1)throw new FunctionException(pc,"XmlChildPos","second","index","attribute must be 1 or greater");
		int count=1;
		for(int i=0;i<len;i++) {
			Node n=xmlNodeList.item(i);
			if(XMLUtil.nameEqual(n,name,XMLUtil.isCaseSensitve(n)) && count++==index) return i+1;
		}
		return -1;
	}
}