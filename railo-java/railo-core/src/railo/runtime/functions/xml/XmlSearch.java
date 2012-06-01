
package railo.runtime.functions.xml;

import javax.xml.transform.TransformerException;

import org.apache.xpath.XPathAPI;
import org.apache.xpath.objects.XObject;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;
import railo.runtime.text.xml.XMLCaster;
import railo.runtime.text.xml.struct.XMLObject;
import railo.runtime.text.xml.struct.XMLStruct;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;

/**
 * Implements the CFML Function xmlsearch
 */
public final class XmlSearch implements Function {

	public static Object call(PageContext pc , Node node, String expr) throws PageException {
		boolean caseSensitive=true;
		if(node instanceof XMLObject) {
			caseSensitive=((XMLObject)node).getCaseSensitive();
		}
		if(node instanceof XMLStruct) {
			node=((XMLStruct)node).toNode();
		}
		return _call(node,expr,caseSensitive);
		
	}
	public static Object _call(Node node, String expr, boolean caseSensitive) throws PageException {
		if(StringUtil.endsWith(expr,'/')) 
			expr = expr.substring(0,expr.length()-1);
		try {
			XObject rs = XPathAPI.eval(node,expr);
			
			switch(rs.getType()){
			case XObject.CLASS_NODESET:
				return nodelist(rs,caseSensitive);
			case XObject.CLASS_BOOLEAN:
				return Caster.toBoolean(rs.bool());
			case XObject.CLASS_NULL:
				return "";
			case XObject.CLASS_NUMBER:
				return Caster.toDouble(rs.num());
			case XObject.CLASS_STRING:
				return rs.str();
			default:
				return rs.object();
			}
		} catch (Throwable e) {
			throw Caster.toPageException(e);
		}
		
		
		
	}
	private static Array nodelist(XObject rs, boolean caseSensitive) throws TransformerException, PageException {
		
		NodeList list = rs.nodelist();
		int len=list.getLength();
		Array rtn=new ArrayImpl();
		for(int i=0;i<len;i++) {
			Node n=list.item(i);
			if(n !=null)
			rtn.append(XMLCaster.toXMLStruct(n,caseSensitive));
		}
		return rtn;
	}
}