package railo.runtime.query.caster;

import java.io.IOException;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.TimeZone;

public class ArrayCast implements Cast {

	@Override
	public Object toCFType(TimeZone tz,int type, ResultSet rst, int columnIndex) throws SQLException, IOException {
		Array arr = rst.getArray(columnIndex);
		if(arr==null) return null;
		return arr.getArray();
	}

}
