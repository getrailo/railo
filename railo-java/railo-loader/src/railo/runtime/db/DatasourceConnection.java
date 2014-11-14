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
package railo.runtime.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * a datasource and connection pair
 */
public interface DatasourceConnection {

    /**
     * @return Returns the connection.
     */
    public abstract Connection getConnection();

    /**
     * @return Returns the datasource.
     */
    public abstract DataSource getDatasource();

    /**
     * @return is timeout or not
     */
    public abstract boolean isTimeout();
    


	/**
	 * @return the password
	 */
	public String getPassword();

	/**
	 * @return the username
	 */
	public String getUsername() ;

	public boolean supportsGetGeneratedKeys();
	
	public PreparedStatement getPreparedStatement(SQL sql, boolean createGeneratedKeys, boolean allowCaching) throws SQLException;
	public PreparedStatement getPreparedStatement(SQL sql, int resultSetType,int resultSetConcurrency) throws SQLException;
	
	public void close() throws SQLException;
	
	

	
}