package railo.runtime.functions.query;

import railo.runtime.PageContext;
import railo.runtime.config.NullSupportHelper;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.Collection;
import railo.runtime.type.Query;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;

/**
 * implements BIF QueryRowData
 */
public class QueryRowData extends BIF {

	public static Struct call(PageContext pc, Query query, double rowNumber) throws PageException {

		int row = Caster.toInteger(rowNumber);

		if ( row < 1 || row > query.getRecordcount() )
			throw new FunctionException( pc, QueryRowData.class.getSimpleName(), 2, "rowNumber", "The argument rowNumber [" + row + "] must be between 1 and the query's record count [" + query.getRecordcount() + "]" );

		Collection.Key[] colNames = query.getColumnNames();

		Struct result = new StructImpl();

		for (int col=0; col<colNames.length; col++)
			result.setEL(colNames[ col ], query.getAt(colNames[ col ], row, NullSupportHelper.empty()));

		return result;
	}

	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {

		return call(pc, Caster.toQuery(args[0]), Caster.toInteger(args[1]));
	}
}
