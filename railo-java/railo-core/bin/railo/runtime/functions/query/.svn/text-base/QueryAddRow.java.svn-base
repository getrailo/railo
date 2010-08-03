/**
 * Implements the Cold Fusion Function queryaddrow
 */
package railo.runtime.functions.query;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Query;

public final class QueryAddRow implements Function {
	public static double call(PageContext pc , Query query) {
		query.addRow(1);
		return query.getRecordcount();
	}
	public static double call(PageContext pc , Query query, double number) {
		query.addRow((int) number);
		return query.getRecordcount();
	}
}