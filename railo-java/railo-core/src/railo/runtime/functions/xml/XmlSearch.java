
package railo.runtime.functions.xml;

import javax.xml.transform.TransformerException;

import org.apache.xpath.XPathAPI;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import railo.print;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;
import railo.runtime.text.xml.XMLCaster;
import railo.runtime.text.xml.XMLUtil;
import railo.runtime.text.xml.struct.XMLObject;
import railo.runtime.text.xml.struct.XMLStruct;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;

/**
 * Implements the Cold Fusion Function xmlsearch
 */
public final class XmlSearch implements Function {

	public static Array call(PageContext pc , Node node, String expr) throws PageException {
		boolean caseSensitive=true;
		if(node instanceof XMLObject) {
			caseSensitive=((XMLObject)node).getCaseSensitive();
		}
		if(node instanceof XMLStruct) {
			node=((XMLStruct)node).toNode();
		}
		return _call(node,expr,caseSensitive);
		
	}
	public static Array _call(Node node, String expr, boolean caseSensitive) throws PageException {
		
		try {
			NodeList list = XPathAPI.selectNodeList(node,expr);
			int len=list.getLength();
			ArrayImpl rtn=new ArrayImpl();
			for(int i=0;i<len;i++) {
				Node n=list.item(i);
				if(n !=null)
				rtn.append(XMLCaster.toXMLStruct(n,caseSensitive));
			}
			return rtn;
			
		} catch (TransformerException e) {
			if(StringUtil.endsWith(expr,'/')) return _call(node,expr.substring(0,expr.length()-1),caseSensitive);
			throw Caster.toPageException(e);
		} catch (Throwable e) {
			throw Caster.toPageException(e);
		}
	}
}