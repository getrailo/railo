package railo.runtime.cache.tag.query;

import railo.runtime.cache.tag.CacheHandlerFilter;
import railo.runtime.db.SQLImpl;
import railo.runtime.type.Query;

public class QueryCacheHandlerFilter implements CacheHandlerFilter {
	
	private String sql;

	public QueryCacheHandlerFilter(SQLImpl sql){
		this.sql=toString(sql.toString());
	}
	public QueryCacheHandlerFilter(String sql){
		this.sql=toString(sql);
	}

	@Override
	public boolean accept(Object obj) {
		if(!(obj instanceof Query)) return false;
		return sql.equals(toString(((Query) obj).getSql().toString()));
	}
	
	private String toString(String sql){
		char[] carr = sql.toCharArray();
		StringBuilder sb=new StringBuilder();
		for(int i=0;i<carr.length;i++) {
			if(carr[i]=='\n' || carr[i]=='\r') {
				sb.append(' ');
			}
			else sb.append(carr[i]);
		}
		return sb.toString().trim();
	}

}
