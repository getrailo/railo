package railo.runtime.debug;

import java.io.IOException;
import java.util.List;

import railo.runtime.PageContext;
import railo.runtime.PageSource;
import railo.runtime.config.Config;
import railo.runtime.db.SQL;
import railo.runtime.dump.Dumpable;
import railo.runtime.exp.CatchBlock;
import railo.runtime.exp.PageException;
import railo.runtime.type.Query;
import railo.runtime.type.Struct;

/**
 * debugger interface
 */
public interface Debugger extends Dumpable {

    /**
     * reset the debug object
     */
    public abstract void reset();

    /**
     * @param source
     * @return returns a single DebugEntry without a key
     */
    public DebugEntry getEntry(PageContext pc,PageSource source);

    /**
     * @param source
     * @param key 
     * @return returns a single DebugEntry witho a key
     */
    public DebugEntry getEntry(PageContext pc,PageSource source, String key);

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
    public void addQuery(Query query,String datasource,String name,SQL sql, int recordcount, PageSource src,int time);
    
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
    public Struct getDebuggingData(PageContext pc);
    

    public Struct getDebuggingData(PageContext pc, boolean addAddionalInfo);

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