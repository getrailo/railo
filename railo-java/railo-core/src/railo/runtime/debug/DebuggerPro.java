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
    
    public void setOutputLog(DebugOutputLog outputLog);
    
    public void init(Config config);
}
