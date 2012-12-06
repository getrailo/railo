package railo.runtime.debug;

import railo.runtime.db.SQL;
import railo.runtime.type.Query;

/**
 * 
 */
public final class QueryEntryImpl implements QueryEntry {

	private static final long serialVersionUID = 8655915268130645466L;
	
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
	
	@Override
	public Query getQry() {
		return qry;
	}
	
	@Override
	public int getExe() {
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
}