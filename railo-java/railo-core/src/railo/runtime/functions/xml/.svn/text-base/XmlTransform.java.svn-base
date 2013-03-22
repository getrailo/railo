/**
 * Implements the Cold Fusion Function xmltransform
 */
package railo.runtime.functions.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.exp.XMLException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;
import railo.runtime.text.xml.XMLUtil;
import railo.runtime.type.Array;

public final class XmlTransform implements Function {
	public static String call(PageContext pc , Object oXml, String xsl) throws PageException {
		try {
			Document doc;
			if(oXml instanceof String) {
				doc=XMLUtil.parse(XMLUtil.toInputSource(pc, oXml.toString()), null, false);
			}
			else if(oXml instanceof Node) doc=XMLUtil.getDocument((Node)oXml);
			else throw new XMLException("XML Object is of invalid type, must be a XML String or a XML Object","now it is "+Caster.toClassName(oXml));
		
			return XMLUtil.transform(doc,XMLUtil.toInputSource(pc, xsl));
		}   
		catch (Exception e) {
			throw Caster.toPageException(e);
		} 
	}
	public static String call(PageContext pc , Object oXml, String xsl,Array parameters) throws PageException {
		// TODO impl. parameters support
		return call(pc, oXml, xsl);
	}
}