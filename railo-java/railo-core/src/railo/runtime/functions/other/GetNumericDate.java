/**
 * Implements the CFML Function getnumericdate
 */
package railo.runtime.functions.other;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;
import railo.runtime.type.dt.DateTime;

public final class GetNumericDate implements Function {
	public static double call(PageContext pc , Object object) throws PageException {
        DateTime date = Caster.toDate(object,true,pc.getTimeZone(),null);
        if(date==null) date=Caster.toDate(object,pc.getTimeZone());
        
		return date.toDoubleValue();
	}
}