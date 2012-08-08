/**
 * Implements the CFML Function arrayavg
 */
package railo.runtime.functions.struct;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.functions.closure.Each;
import railo.runtime.type.Struct;
import railo.runtime.type.UDF;


public final class StructEach implements Function {

	private static final long serialVersionUID = 5795152568391831373L;

	public static String call(PageContext pc , Struct sct, UDF udf) throws PageException {
		Each.invoke(pc, sct, udf);
		return null;
	}
}