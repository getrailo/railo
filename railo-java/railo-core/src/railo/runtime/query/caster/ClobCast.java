package railo.runtime.query.caster;

import java.io.IOException;
import java.io.Reader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.TimeZone;

import railo.commons.io.IOUtil;

public class ClobCast implements Cast {

	public Object toCFType(TimeZone tz, int type, ResultSet rst, int columnIndex) throws SQLException, IOException {
		//IOUtil.toString(rst.getClob(columnIndex).getCharacterStream())
		
		Reader reader = null;
		try {
			reader = rst.getCharacterStream( columnIndex );
			if(reader==null) return null;
			return IOUtil.toString(reader);
		}
		finally {
			IOUtil.closeEL(reader);
		}
	}

}
