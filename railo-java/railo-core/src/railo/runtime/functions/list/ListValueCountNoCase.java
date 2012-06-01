/**
 * Implements the CFML Function listvaluecountnocase
 */
package railo.runtime.functions.list;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;

public final class ListValueCountNoCase implements Function {

	private static final long serialVersionUID = 2648222056209118284L;

	public static double call(PageContext pc , String list, String value) throws PageException {
		return ListValueCount.call(pc,list.toLowerCase(),value.toLowerCase(),",");
	}
	public static double call(PageContext pc , String list, String value, String delimiter) throws PageException {
		return ListValueCount.call(pc,list.toLowerCase(),value.toLowerCase(),delimiter);
		
	}
	public static double call(PageContext pc , String list, String value, String delimiter,boolean includeEmptyFields) throws PageException {
		return ListValueCount.call(pc,list.toLowerCase(),value.toLowerCase(),delimiter,includeEmptyFields);
	}
}