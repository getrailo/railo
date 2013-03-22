package railo.runtime.query.caster;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import railo.runtime.type.dt.DateTimeImpl;

public class TimestampCast implements Cast{

	/**
	 * @see railo.runtime.query.caster.Cast#toCFType(int, java.sql.ResultSet, int)
	 */
	public Object toCFType(int type, ResultSet rst, int columnIndex) throws SQLException, IOException {
		Timestamp ts = rst.getTimestamp(columnIndex);
		if(ts==null) return null;
		return new DateTimeImpl(ts.getTime(),false);
	}
}
