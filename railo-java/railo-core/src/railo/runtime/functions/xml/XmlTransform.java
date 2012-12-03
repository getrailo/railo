/**
 * Implements the CFML Function xmltransform
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
import railo.runtime.type.Struct;

public final class XmlTransform implements Function {


	public static String call( PageContext pc , Object oXml, String xsl ) throws PageException {
		return call( pc, oXml, xsl, null );
	}
	
	public static String call( PageContext pc , Object oXml, String xsl, Struct parameters ) throws PageException {
		try {
			Document doc;
			if(oXml instanceof String) {
				doc=XMLUtil.parse(XMLUtil.toInputSource(pc, oXml.toString()), null, false);
			}
			else if(oXml instanceof Node) doc=XMLUtil.getDocument((Node)oXml);
			else throw new XMLException("XML Object is of invalid type, must be a XML String or a XML Object","now it is "+Caster.toClassName(oXml));

			return XMLUtil.transform( doc, XMLUtil.toInputSource( pc, xsl ), parameters );
		}
		catch (Exception e) {
			throw Caster.toPageException(e);
		}
	}
}