package railo.runtime.functions.decision;

import java.io.StringReader;

import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;
import railo.runtime.text.xml.XMLUtil;

/**
 * Check if a String is a well-formed XML
 */
public final class IsXML implements Function {

	public static boolean call(PageContext pc, Object xml) {
	    if(xml instanceof Node) return true;
	    
	    try {
	        XMLUtil.parse(new InputSource(new StringReader(Caster.toString(xml))),null,false);
	        return true;
	    }
	    catch(Exception e) {
		    return false;
	    }
	}
}