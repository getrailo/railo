package railo.runtime.query.caster;

import java.io.IOException;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

import railo.runtime.type.dt.DateTimeImpl;

public class DateCast implements Cast{

	/**
	 * @see railo.runtime.query.caster.Cast#toCFType(int, java.sql.ResultSet, int)
	 */
	public Object toCFType(int type, ResultSet rst, int columnIndex) throws SQLException, IOException {
		Date d = rst.getDate(columnIndex);
		if(d==null) return null; 
		
		return new DateTimeImpl(d.getTime(),false);
		
	}

}
