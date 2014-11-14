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

// FUTURE add to extended interface and delete this interface

import railo.runtime.PageContext;
import railo.runtime.PageSource;
import railo.runtime.config.Config;
import railo.runtime.db.SQL;
import railo.runtime.type.Query;

public interface DebuggerPro extends Debugger {
	
	
	

    /**
     * add new query execution time
     * @param query 
     * @param datasource 
     * @param name
     * @param sql
     * @param recordcount
     * @param src
     * @param time 
     * @deprecated use instead <code>addQuery(Query query,String datasource,String name,SQL sql, int recordcount, PageSource src,long time)</code>
     */
    public void addQuery(Query query,String datasource,String name,SQL sql, int recordcount, PageSource src,int time);
    
    /**
     * add new query execution time
     * @param query 
     * @param datasource 
     * @param name
     * @param sql
     * @param recordcount
     * @param src
     * @param time 
     */
    public void addQuery(Query query,String datasource,String name,SQL sql, int recordcount, PageSource src,long time);
    
    public DebugTrace[] getTraces(PageContext pc);
    
    public DebugDump addDump(PageSource ps,String dump);
	
    
    
    
    
    
    public void setOutputLog(DebugOutputLog outputLog);
    
    public void init(Config config);
}
