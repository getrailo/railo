package railo.runtime.debug;

import java.io.Serializable;

import railo.runtime.db.SQL;
import railo.runtime.type.Query;

/**
 * a single query entry
 */
public interface QueryEntry extends Serializable {

    /**
     * @return return the query executionn time in mili seconds
	 * @deprecated use instead <code>getExecutionTime()</code>
     */
    public abstract int getExe();
    
    /**
	 * @return return the query executionn time in nanoseconds
	 */
	public long getExecutionTime();
    
    
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
    
    public long getStartTime();
	
	public String getCacheType();

}