package railo.runtime.query.caster;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OracleOpaqueCast implements Cast {

	@Override
	public Object toCFType(int type, ResultSet rst, int columnIndex) throws SQLException, IOException {
		return _OracleOpaqueCast.toCFType(rst, columnIndex);
	}
}
