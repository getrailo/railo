package railo.runtime.debug;

import railo.runtime.db.SQL;

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
	
	/**
	 * constructor of the class
	 * @param recordcount
	 * @param query
	 * @param src
	 * @param exe
	 */
	public QueryEntryImpl(String datasource, String name,SQL sql,int recordcount, String src, int exe) {
        this.datasource=datasource;
        this.recordcount=recordcount;
        this.name=name;
	    this.src=src;
		this.sql=sql;
		this.exe=exe;
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