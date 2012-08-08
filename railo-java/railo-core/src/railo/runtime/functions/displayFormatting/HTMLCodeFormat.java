/**
 * Implements the CFML Function htmlcodeformat
 */
package railo.runtime.functions.displayFormatting;

import railo.commons.lang.HTMLEntities;
import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

public final class HTMLCodeFormat implements Function {
	public static String call(PageContext pc , String html) {
		return "<pre>"+HTMLEntities.escapeHTML(html,HTMLEntities.HTMLV40)+"</pre>";
	}
	public static String call(PageContext pc , String html, double version) {
		short v=HTMLEntities.HTMLV40;
		if(version==3.2D)v=HTMLEntities.HTMLV32;
		else if(version==4.0D)v=HTMLEntities.HTMLV40;
		
		return "<pre>"+HTMLEntities.escapeHTML(html,v)+"</pre>";
	}
}