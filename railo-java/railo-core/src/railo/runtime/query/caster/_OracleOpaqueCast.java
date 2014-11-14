/**
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library.  If not, see <http://www.gnu.org/licenses/>.
 * 
 **/
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
