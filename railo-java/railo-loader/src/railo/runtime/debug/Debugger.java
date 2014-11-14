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

import java.io.IOException;
import java.util.List;

import railo.runtime.PageContext;
import railo.runtime.PageSource;
import railo.runtime.config.Config;
import railo.runtime.db.SQL;
import railo.runtime.exp.CatchBlock;
import railo.runtime.exp.PageException;
import railo.runtime.type.Query;
import railo.runtime.type.Struct;

/**
 * debugger interface
 */
public interface Debugger {

    /**
     * reset the debug object
     */
    public abstract void reset();

    /**
     * @param pc current PagContext
     * @param source Page Source for the entry
     * @return returns a single DebugEntry 
     */
    public DebugEntryTemplate getEntry(PageContext pc,PageSource source);

    /**
     * @param pc current PagContext
     * @param source Page Source for the entry
     * @param key 
     * @return returns a single DebugEntry with a key
     */
    public DebugEntryTemplate getEntry(PageContext pc,PageSource source, String key);
    
    /**
     * returns a single DebugEntry for a specific postion (startPos,endPos in the PageSource)
     * @param pc current PagContext
     * @param source Page Source for the entry
     * @param startPos start position in the file
     * @param endPos end position in the file
     * @return
     */
    public DebugEntryTemplatePart getEntry(PageContext pc,PageSource source, int startPos, int endPos);

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
    public void addQuery(Query query,String datasource,String name,SQL sql, int recordcount, PageSource src,int time); // FUTURE deprecated
    // FUTURE public void addQuery(Query query,DataSource datasource,String name,SQL sql, int recordcount, PageSource src,int time);
    
    // FUTURE add ans set method above to deprecated -> public void addQuery(Query query,String datasource,String name,SQL sql, int recordcount, PageSource src,long time);
    
    /**
     * sets if toHTML print html output info or not
     * @param output The output to set.
     */
    public abstract void setOutput(boolean output);

    /**
     * @return Returns the queries.
     */
    public List<QueryEntry> getQueries();

    /**
     * @param pc
     * @throws IOException 
     */
    public void writeOut(PageContext pc) throws IOException;
    
    /**
     * returns the Debugging Info
     * @return debugging Info
     */
    public Struct getDebuggingData(PageContext pc) throws PageException;
    

    public Struct getDebuggingData(PageContext pc, boolean addAddionalInfo) throws PageException;

	/**
	 * adds ne Timer info to debug
	 * @param label
	 * @param exe
	 */
	public DebugTimer addTimer(String label, long exe, String template);
 
	/**
	 * add new Trace to debug
	 * @param type
	 * @param category
	 * @param text
	 * @param page
	 * @param varName
	 * @param varValue
	 * @return debug trace object
	 */
	public DebugTrace addTrace(int type, String category, String text, PageSource page, String varName, String varValue);
	
	public DebugTrace addTrace(int type, String category, String text, String template,int line,String action,String varName,String varValue);
		

	public abstract DebugTrace[] getTraces();

	public abstract void addException(Config config,PageException pe);
	public CatchBlock[] getExceptions();
	
	public void addImplicitAccess(String scope, String name);

	public ImplicitAccess[] getImplicitAccesses(int scope, String name);
	
}