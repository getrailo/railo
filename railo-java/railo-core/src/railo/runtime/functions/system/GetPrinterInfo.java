/**
 * Implements the CFML Function getprofilestring
 */
package railo.runtime.functions.system;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionNotSupported;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Struct;

public final class GetPrinterInfo implements Function {
	public static Struct call(PageContext pc , String printer) throws PageException {
        throw new FunctionNotSupported("GetPrinterInfo");
	}
}