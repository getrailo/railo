package railo.runtime.debug;

import railo.runtime.db.SQL;
import railo.runtime.type.Query;

/**
 * 
 */
public final class QueryEntryImpl implements QueryEntryPro {

	private static final long serialVersionUID = 8655915268130645466L;
	
	private final String src;
	private final SQL sql;
	private final long exe;
    private final String name;
    private final int recordcount;
    private final String datasource;
	private final Query qry;
	private final long startTime;
	
	/**
	 * constructor of the class
	 * @param recordcount
	 * @param query
	 * @param src
	 * @param exe
	 */
	public QueryEntryImpl(Query qry,String datasource, String name,SQL sql,int recordcount, String src, long exe) {
		this.startTime=System.currentTimeMillis()-(exe/1000000);
		this.datasource=datasource;
        this.recordcount=recordcount;
        this.name=name;
	    this.src=src;
		this.sql=sql;
		this.exe=exe;
		this.qry=qry;
	}
	
	@Override
	public Query getQry() {
		return qry;
	}
	
	@Override
	public int getExe() {
		return (int)getExecutionTime();
	}
	
	@Override
	public long getExecutionTime() {
		return exe;
	}
	
	
	@Override
	public SQL getSQL() {
		return sql;
	}
	@Override
	public String getSrc() {
		return src;
	}
	@Override
	public String getName() {
        return name;
    }
	@Override
	public int getRecordcount() {
        return recordcount;
    }
	@Override
	public String getDatasource() {
        return datasource;
    }
	
	// FUTURE add to interface
	public long getStartTime() {
        return startTime;
    }
}