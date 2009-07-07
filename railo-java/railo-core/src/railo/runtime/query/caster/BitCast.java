package railo.runtime.query.caster;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import railo.runtime.op.Caster;
import railo.runtime.op.Constants;

public class BitCast implements Cast {

	/**
	 * @see railo.runtime.query.caster.Cast#toCFType(int, java.sql.ResultSet, int)
	 */
	public Object toCFType(int type, ResultSet rst, int columnIndex) throws SQLException, IOException {
		Object o=rst.getObject(columnIndex);
		if(o==null)return null;
		return Caster.toDouble(o,Constants.DOUBLE_ZERO);
	}

}
