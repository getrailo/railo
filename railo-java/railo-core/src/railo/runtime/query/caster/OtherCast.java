package railo.runtime.query.caster;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OtherCast implements Cast {


	@Override
	public Object toCFType(int type, ResultSet rst, int columnIndex) throws SQLException {
		return rst.getObject(columnIndex);
	}

}
