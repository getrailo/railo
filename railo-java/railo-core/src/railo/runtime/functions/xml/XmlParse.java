/**
 * Implements the CFML Function xmlparse
 */
package railo.runtime.functions.xml;

import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;
import railo.runtime.text.xml.XMLCaster;
import railo.runtime.text.xml.XMLUtil;

public final class XmlParse implements Function {
	public static Node call(PageContext pc , String string) throws PageException {
		return call(pc,string,false,null);
	}
	public static Node call(PageContext pc , String string, boolean caseSensitive) throws PageException {
		return call(pc, string, caseSensitive,null);
		
	}
	public static Node call(PageContext pc , String strXML, boolean caseSensitive, String strValidator) throws PageException {
		try {
			InputSource xml = XMLUtil.toInputSource(pc,strXML.trim());
			InputSource validator =StringUtil.isEmpty(strValidator)?null:XMLUtil.toInputSource(pc,strValidator.trim());
			return XMLCaster.toXMLStruct(XMLUtil.parse(xml,validator,false),caseSensitive);
		} 
		catch (Exception e) {
			throw Caster.toPageException(e);
		}
	}
}