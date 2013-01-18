package railo.runtime.db.debug;

public class DebugQuery {

	public final String sql;
	public final long startTime;

	public DebugQuery(String sql, long startTime) {
		this.sql=sql;
		this.startTime=startTime;
	}

}
