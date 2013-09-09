package railo.runtime.query.caster;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.Calendar;
import java.util.TimeZone;

import railo.commons.date.TimeZoneUtil;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.type.dt.DateTimeImpl;

public class TimeCast implements Cast{

	@Override
	public Object toCFType(TimeZone tz, int type, ResultSet rst, int columnIndex) throws SQLException, IOException {
		Calendar c=TimeZoneUtil.getCalendar(ThreadLocalPageContext.getTimeZone(tz));
		Time t = rst.getTime(columnIndex,c);
		if(t==null) return null;
		
		return new DateTimeImpl(t.getTime(),false);
		
	}

}
