/**
 * Implements the Cold Fusion Function htmleditformat
 */
package railo.runtime.functions.displayFormatting;

import railo.commons.lang.HTMLEntities;
import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

public final class HTMLEditFormat implements Function {
	public static String call(PageContext pc , String html) {
		return HTMLEntities.escapeHTML(html,HTMLEntities.HTMLV20);
	}
	public static String call(PageContext pc , String html, double version) {
		short v=HTMLEntities.HTMLV40;
		if(version==3.2D)v=HTMLEntities.HTMLV32;
		else if(version==4.0D)v=HTMLEntities.HTMLV40;
		else if(version==-1)v=HTMLEntities.HTMLV40;
		
		return HTMLEntities.escapeHTML(html,v);
	}

}