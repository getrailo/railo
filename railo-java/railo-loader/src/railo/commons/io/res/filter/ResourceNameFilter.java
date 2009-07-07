package railo.commons.io.res.filter;

import railo.commons.io.res.Resource;


public interface ResourceNameFilter {
	public boolean accept(Resource parent, String name);
}
