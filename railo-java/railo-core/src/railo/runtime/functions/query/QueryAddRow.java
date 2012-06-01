/**
 * Implements the CFML Function queryaddrow
 */
package railo.runtime.functions.query;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.type.Query;

public final class QueryAddRow implements Function {
	public static double call(PageContext pc , Query query) {
		query.addRow(1);
		return query.getRecordcount();
	}
	public static double call(PageContext pc , Query query, Object numberOrData) throws PageException {
		if(numberOrData==null) return call(pc, query);
		else if(Decision.isNumeric(numberOrData)) {
			query.addRow(Caster.toIntValue(numberOrData));
		}
		else if(Decision.isStruct(numberOrData)) {
			query.addRow();
			QueryNew.populateRow(query, Caster.toStruct(numberOrData));
		}
		else if(Decision.isArray(numberOrData)) {
			query.addRow();
			QueryNew.populateRow(query, Caster.toArray(numberOrData));
		}
		else
			throw new FunctionException(pc, "QueryAddRow", 2, "data", "you must define a array, a struct or a number");
		
		
		return query.getRecordcount();
	}
}