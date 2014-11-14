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

public interface DataSourcePro extends DataSource {
	/**
     * @return Returns the connection string with NOT replaced placeholders.
     */
    public String getConnectionString();

    /**
     * @return Returns the connection string with replaced placeholders.
     */
    public String getConnectionStringTranslated();

    /**
     * @return unique id of the DataSource
     */
    public String id();
}
