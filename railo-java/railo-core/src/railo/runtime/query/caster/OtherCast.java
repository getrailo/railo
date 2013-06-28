package railo.runtime.query.caster;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.TimeZone;

public class OtherCast implements Cast {


	@Override
	public Object toCFType(TimeZone tz, int type, ResultSet rst, int columnIndex) throws SQLException {
		if(type!=Types.SMALLINT) return rst.getObject(columnIndex);
		
		
		try{
			return rst.getObject(columnIndex);
		}
		// workaround for MSSQL Driver, in some situation getObject throws a cast exception using getString avoids this
		catch(SQLException e){
			try{
				return rst.getString(columnIndex);
			}
			catch(SQLException e2){
				throw e;
			}
		}
	}

}
