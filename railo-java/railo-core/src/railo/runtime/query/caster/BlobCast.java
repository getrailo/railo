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
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.TimeZone;

import railo.commons.io.IOUtil;

public class BlobCast implements Cast {

	public Object toCFType(TimeZone tz, int type, ResultSet rst, int columnIndex) throws SQLException, IOException {
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
