/**
 * Implements the CFML Function querysetcell
 */
package railo.runtime.functions.query;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionNotSupported;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.Query;
import railo.runtime.type.Struct;

public final class QueryConvertForGrid extends BIF {

	private static final long serialVersionUID = 871091293736619034L;

	public static Struct call(PageContext pc , Query query, double page,double pageSize) throws PageException {
		throw new FunctionNotSupported("QueryConvertForGrid");		
	}
	
	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		return call(pc,Caster.toQuery(args[0]),Caster.toDoubleValue(args[1]),Caster.toDoubleValue(args[2]));
	}
}