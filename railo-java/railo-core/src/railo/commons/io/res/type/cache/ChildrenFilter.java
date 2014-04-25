package railo.commons.io.res.type.cache;

import railo.commons.io.cache.CacheKeyFilter;
import railo.commons.lang.StringUtil;

public class ChildrenFilter implements CacheKeyFilter {

	private String path;

	public ChildrenFilter(String path) {
		this.path=(StringUtil.endsWith(path, '/'))?path+":":path+"/:";
	}

	@Override
	public boolean accept(String key) {
		return key.startsWith(path);
	}

	@Override
	public String toPattern() {
		return null;
	}

}
