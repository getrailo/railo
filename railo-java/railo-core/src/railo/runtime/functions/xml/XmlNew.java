/**
 * Implements the CFML Function xmlnew
 */
package railo.runtime.functions.xml;

import org.w3c.dom.Node;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;
import railo.runtime.text.xml.XMLCaster;
import railo.runtime.text.xml.XMLUtil;

public final class XmlNew implements Function {
	public static Node call(PageContext pc) throws PageException {
		return call(pc,false);
	}
	public static Node call(PageContext pc, boolean caseSensitive) throws PageException {
		try {
			return XMLCaster.toXMLStruct(XMLUtil.newDocument(),caseSensitive);
		} 
		catch (Exception e) {
			throw Caster.toPageException(e);
		}
	}
	
}