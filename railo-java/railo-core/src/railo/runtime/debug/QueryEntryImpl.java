package railo.runtime.debug;

import railo.runtime.db.SQL;
import railo.runtime.type.Query;

/**
 * 
 */
public final class QueryEntryImpl implements QueryEntry {
	private String src;
	private SQL sql;
	private int exe;
    private String name;
    private int recordcount;
    private String datasource;
	private Query qry;
	
	/**
	 * constructor of the class
	 * @param recordcount
	 * @param query
	 * @param src
	 * @param exe
	 */
	public QueryEntryImpl(Query qry,String datasource, String name,SQL sql,int recordcount, String src, int exe) {
        this.datasource=datasource;
        this.recordcount=recordcount;
        this.name=name;
	    this.src=src;
		this.sql=sql;
		this.exe=exe;
		this.qry=qry;
	}
	/** FUTURE add to interface
	 * @return the qry
	 */
	public Query getQry() {
		return qry;
	}
	/**
     * @see railo.runtime.debug.QueryEntry#getExe()
     */
	public int getExe() {
		return exe;
	}
	/**
     * @see railo.runtime.debug.QueryEntry#getSQL()
     */
	public SQL getSQL() {
		return sql;
	}
	/**
     * @see railo.runtime.debug.QueryEntry#getSrc()
     */
	public String getSrc() {
		return src;
	}
    /**
     * @see railo.runtime.debug.QueryEntry#getName()
     */
    public String getName() {
        return name;
    }
    /**
     * @see railo.runtime.debug.QueryEntry#getRecordcount()
     */
    public int getRecordcount() {
        return recordcount;
    }
    /**
     * @see railo.runtime.debug.QueryEntry#getDatasource()
     */
    public String getDatasource() {
        return datasource;
    }
}