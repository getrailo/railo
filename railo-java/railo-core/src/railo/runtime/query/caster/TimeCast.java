package railo.runtime.query.caster;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;

import railo.runtime.type.dt.DateTimeImpl;

public class TimeCast implements Cast{

	@Override
	public Object toCFType(int type, ResultSet rst, int columnIndex) throws SQLException, IOException {
		Time t = rst.getTime(columnIndex);
		if(t==null) return null;
		
		return new DateTimeImpl(t.getTime(),false);
		
	}

}
