package railo.runtime.query.caster;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.TimeZone;

import railo.runtime.op.Caster;
import railo.runtime.op.Constants;

public class BitCast implements Cast {

	@Override
	public Object toCFType(TimeZone tz,int type, ResultSet rst, int columnIndex) throws SQLException, IOException {
		Object o=rst.getObject(columnIndex);
		if(o==null)return null;
		return Caster.toDouble(o,Constants.DOUBLE_ZERO);
	}

}
