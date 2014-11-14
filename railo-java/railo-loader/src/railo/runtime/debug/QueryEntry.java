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
package railo.runtime.debug;

import java.io.Serializable;

import railo.runtime.db.SQL;
import railo.runtime.type.Query;

/**
 * a single query entry
 */
public interface QueryEntry extends Serializable {

    /**
     * @return Returns the exe.
     */
    public abstract int getExe();
    // FUTURE add the following method ans set method above to deprecated -> public abstract long getExeutionTime();

    /**
     * @return Returns the query.
     */
    public abstract SQL getSQL();
    
    /**
     * return the query of this entry (can be null, if the quer has not produced a resultset)
     * @return
     */
    public Query getQry();

    /**
     * @return Returns the src.
     */
    public abstract String getSrc();

    /**
     * @return Returns the name.
     */
    public abstract String getName();

    /**
     * @return Returns the recordcount.
     */
    public abstract int getRecordcount();

    /**
     * @return Returns the datasource.
     */
    public abstract String getDatasource();

}