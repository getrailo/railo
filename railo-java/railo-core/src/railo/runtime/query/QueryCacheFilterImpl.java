package railo.runtime.query;

import org.apache.oro.text.regex.MalformedPatternException;

import railo.commons.io.res.util.WildCardFilter;

public class QueryCacheFilterImpl implements QueryCacheFilter {


	private WildCardFilter filter;

	public QueryCacheFilterImpl(String wildcard, boolean ignoreCase)throws MalformedPatternException {
		filter=new WildCardFilter(wildcard,ignoreCase);
	}

	@Override
	public boolean accept(String name) {
		
		StringBuffer sb=new StringBuffer();
		char[] text = name.toCharArray();
		for(int i=0;i<text.length;i++) {
			if(text[i]=='\n' || text[i]=='\r') {
				sb.append(' ');
			}
			else sb.append(text[i]);
		}
		return filter.accept(sb.toString());
	}
}
