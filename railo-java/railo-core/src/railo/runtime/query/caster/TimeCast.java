package railo.runtime.query.caster;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;

import railo.runtime.functions.dateTime.DateUtil;
import railo.runtime.type.dt.DateTimeImpl;

public class TimeCast implements Cast{

	/**
	 * @see railo.runtime.query.caster.Cast#toCFType(int, java.sql.ResultSet, int)
	 */
	public Object toCFType(int type, ResultSet rst, int columnIndex) throws SQLException, IOException {
		Time t = rst.getTime(columnIndex);
		if(t==null) return null;
		
		return new DateTimeImpl(
				DateUtil.fromSystemToRailo(t.getTime()),
				false);
		
	}

}
