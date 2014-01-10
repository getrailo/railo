package railo.runtime.debug;

public class ActiveQuery {

	public final String sql;
	public final long startTime;

	public ActiveQuery(String sql, long startTime) {
		this.sql=sql;
		this.startTime=startTime;
	}
}
