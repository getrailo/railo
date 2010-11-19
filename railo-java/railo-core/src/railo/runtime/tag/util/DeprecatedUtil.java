package railo.runtime.tag.util;

import railo.commons.lang.SystemOut;
import railo.runtime.PageContext;
import railo.runtime.engine.ThreadLocalPageContext;

public class DeprecatedUtil {

	public static void tagAttribute(String tagName,String attrName) {
		tagAttribute(null, tagName,attrName);
	}
	
	public static void tagAttribute(PageContext pc,String tagName,String attrName) {
		pc = ThreadLocalPageContext.get(pc);
		if(pc==null) return;
		SystemOut.printDate(pc.getConfig().getErrWriter(), "attribute "+attrName+" of the tag "+tagName+" is deprecated and will be ignored");
	}

	public static void function(PageContext pc, String old) {
		pc = ThreadLocalPageContext.get(pc);
		if(pc==null) return;
		SystemOut.printDate(pc.getConfig().getErrWriter(), "function "+old+" is deprecated");
	}
	public static void function(PageContext pc, String old, String replacement) {
		pc = ThreadLocalPageContext.get(pc);
		if(pc==null) return;
		SystemOut.printDate(pc.getConfig().getErrWriter(), "function "+old+" is deprecated, please use instead function "+replacement);
	}

}
