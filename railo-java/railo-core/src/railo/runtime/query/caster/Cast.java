package railo.runtime.query.caster;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.TimeZone;

public interface Cast {

	public static final Cast ARRAY=new ArrayCast();
	public static final Cast BIT=new BitCast();
	public static final Cast BLOB=new BlobCast();
	public static final Cast CLOB=new ClobCast();
	public static final Cast DATE=new DateCast();
	public static final Cast ORACLE_OPAQUE=new OracleOpaqueCast();
	public static final Cast OTHER=new OtherCast();
	public static final Cast TIME=new TimeCast();
	public static final Cast TIMESTAMP=new TimestampCast();
	public static final Cast BIGINT=new BigIntCast();
	
	public Object toCFType(TimeZone tz,int type,ResultSet rst, int columnIndex) throws SQLException, IOException;
}
