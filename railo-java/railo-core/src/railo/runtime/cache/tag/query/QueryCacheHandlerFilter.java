package railo.runtime.cache.tag.query;

import org.apache.oro.text.regex.MalformedPatternException;

import railo.commons.io.res.util.WildCardFilter;
import railo.runtime.cache.tag.CacheHandlerFilter;
import railo.runtime.type.Query;

public class QueryCacheHandlerFilter implements CacheHandlerFilter {
	
	private WildCardFilter filter;

	public QueryCacheHandlerFilter(String wildcard, boolean ignoreCase)throws MalformedPatternException {
		filter=new WildCardFilter(wildcard,ignoreCase);
	}

	@Override
	public boolean accept(Object obj) {
		Query qry;
		if(!(obj instanceof Query)) {
			if(obj instanceof QueryCacheItem) {
				qry=((QueryCacheItem)obj).getQuery();
			}
			else return false;
		}
		else qry=(Query) obj;
		
		String sql = qry.getSql().toString();
		StringBuilder sb=new StringBuilder();
		char[] text = sql.toCharArray();
		for(int i=0;i<text.length;i++) {
			if(text[i]=='\n' || text[i]=='\r') {
				sb.append(' ');
			}
			else sb.append(text[i]);
		}
		return filter.accept(sb.toString());
	}
}
