package railo.runtime.query.caster;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OtherCast implements Cast {


	/**
	 * @see railo.runtime.query.caster.Cast#toCFType(int, java.sql.ResultSet, int)
	 */
	public Object toCFType(int type, ResultSet rst, int columnIndex) throws SQLException {
		return rst.getObject(columnIndex);
	}

}
