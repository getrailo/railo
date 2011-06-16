package railo.runtime.query;

public interface QueryCacheFilter {
	public boolean accept(String sql);
}