package railo.runtime.cache.tag;

import java.io.IOException;
import java.util.Date;

import railo.runtime.PageContext;
import railo.runtime.db.SQL;
import railo.runtime.query.QueryCache;
import railo.runtime.query.QueryCacheFilter;
import railo.runtime.type.Query;

public class LegacyInterfaceProxy implements QueryCache {
	
	public LegacyInterfaceProxy(){
		
	}

	@Override
	public void clearUnused(PageContext pc) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public Query getQuery(PageContext pc, SQL sql, String datasource, String username, String password, Date cacheAfter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void set(PageContext pc, SQL sql, String datasource, String username, String password, Object value, Date cacheBefore) {
		// TODO Auto-generated method stub

	}

	@Override
	public void clear(PageContext pc) {
		// TODO Auto-generated method stub

	}

	@Override
	public void clear(PageContext pc, QueryCacheFilter filter) {
		// TODO Auto-generated method stub

	}

	@Override
	public void remove(PageContext pc, SQL sql, String datasource, String username, String password) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object get(PageContext pc, SQL sql, String datasource, String username, String password, Date cachedafter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int size(PageContext pc) {
		// TODO Auto-generated method stub
		return 0;
	}

}
