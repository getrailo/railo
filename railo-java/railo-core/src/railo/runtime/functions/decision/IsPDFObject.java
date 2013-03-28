/**
 * Implements the CFML Function isdate
 */
package railo.runtime.functions.decision;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.text.pdf.PDFUtil;

public final class IsPDFObject implements Function {
	public static boolean call(PageContext pc , Object value) {
		try {
			PDFUtil.toPdfReader(pc,value,null);
		} 
		catch (Exception e) {
			return false;
		}
		return true;
	}
	
	
	
}