package railo.runtime.cache.tag.smart;

import railo.runtime.PageContext;
import railo.runtime.type.Query;

public class QuerySmartEntry extends StandardSmartEntry {


	private String name;
	private int payLoad;
	private String meta;
	private long executionTime;

	public QuerySmartEntry(PageContext pc, Query qry, String id, int cacheType) { 
		super(pc, id,toResultHash(qry), cacheType);
		
		this.name=qry.getName();
		this.payLoad=qry.getRecordcount();
		this.meta=qry.getSql().getSQLString();
		this.executionTime=qry.getExecutionTime();
	}
	

	public String getName() {
		return name;
	}

	public int getPayLoad() {
		return payLoad;
	}

	public String getMeta() {
		return meta;
	}

	public long getExecutionTime() {
		return executionTime;
	}

}
