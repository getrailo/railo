package railo.runtime.query.caster;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.TimeZone;

import railo.runtime.op.Caster;

public class BigIntCast implements Cast {

	public Object toCFType(TimeZone tz, int type, ResultSet rst, int columnIndex) throws SQLException, IOException {
		String str=rst.getString(columnIndex);
		if(str!=null && str.length()>9) return str;
		
		Double dbl = Caster.toDouble(str,null);
		if(dbl!=null) return dbl;
		return str;
	}

}
