package railo.runtime.query.caster;

import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;

import railo.commons.io.IOUtil;

public class BlobCast implements Cast {

	public Object toCFType(int type, ResultSet rst, int columnIndex) throws SQLException, IOException {
		InputStream is = null;
		try{
			is = rst.getBinaryStream(columnIndex);
			if(is==null) return null;
			return IOUtil.toBytes(is);
		}
		finally {
			IOUtil.closeEL(is);
		}
	}

}
