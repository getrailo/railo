package railo.runtime.query.caster;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.TimeZone;

import railo.commons.date.JREDateTimeUtil;
import railo.runtime.type.dt.DateTimeImpl;

public class TimestampCast implements Cast{

	@Override
	public Object toCFType(TimeZone tz,int type, ResultSet rst, int columnIndex) throws SQLException, IOException {
		Timestamp ts = rst.getTimestamp(columnIndex,JREDateTimeUtil.getThreadCalendar(tz));
		if(ts==null) return null;
		return new DateTimeImpl(ts.getTime(),false);
	}
}
