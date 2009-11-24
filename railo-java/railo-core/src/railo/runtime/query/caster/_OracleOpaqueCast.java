package railo.runtime.query.caster;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import oracle.sql.OPAQUE;

public class _OracleOpaqueCast {
	public static Object toCFType(ResultSet rst, int columnIndex) throws SQLException, IOException {
		validateClasses();
		
		Object o = rst.getObject(columnIndex);
		if(o==null) return null;
		
		OPAQUE opaque = ((oracle.sql.OPAQUE)o);
		if(opaque.getSQLTypeName().equals("SYS.XMLTYPE")){
			return oracle.xdb.XMLType.createXML(opaque).getStringVal();
		}
		return o;
	}

	private static void validateClasses() throws IOException {
		try {
			if(oracle.xdb.XMLType.class==null);
			if(oracle.xml.parser.v2.XMLParseException.class==null);
		}
		catch(Throwable t) {
			throw new IOException("the xdb.jar/xmlparserv2.jar is missing, please download at " +
					"http://www.oracle.com/technology/tech/xml/xdk/xdk_java.html and copy it into the railo lib directory");
		}
	}
}
