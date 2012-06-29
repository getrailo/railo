/**
 * Implements the CFML Function xmlparse
 */
package railo.runtime.functions.xml;

import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;
import railo.runtime.text.xml.XMLCaster;
import railo.runtime.text.xml.XMLUtil;

public final class HtmlParse implements Function {
    public static Node call(PageContext pc , String string) throws PageException {
        return call(pc,string,false);
    }
    public static Node call(PageContext pc , String strHTML, boolean caseSensitive) throws PageException {
		try {
	    	InputSource xml = XMLUtil.toInputSource(pc,strHTML,false);
			return XMLCaster.toXMLStruct(XMLUtil.parse(xml,null,true),caseSensitive);
		} 
		catch (Exception e) {
			throw Caster.toPageException(e);
		}
    	
    	
        /*try {
            return XMLCaster.toXMLStruct(XMLUtil.parse(string,true),caseSensitive);//new XMLNodeStruct(XMLUtil.parse(string),caseSensitive);
        } 
        catch (Exception e) {
            throw Caster.toPageException(e);
        }*/
    }
}