package railo.runtime.query.caster;

import java.io.IOException;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.TimeZone;

import railo.commons.date.TimeZoneUtil;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.type.dt.DateTimeImpl;

public class DateCast implements Cast{

	@Override
	public Object toCFType(TimeZone tz,int type, ResultSet rst, int columnIndex) throws SQLException, IOException {
		Calendar c=TimeZoneUtil.getCalendar(ThreadLocalPageContext.getTimeZone(tz));
		Date d = rst.getDate(columnIndex,c);
		if(d==null) return null; 
		
		return new DateTimeImpl(d.getTime(),false);
		
	}

}
