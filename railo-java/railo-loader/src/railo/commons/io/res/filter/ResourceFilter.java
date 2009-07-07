package railo.commons.io.res.filter;

import railo.commons.io.res.Resource;

public interface ResourceFilter {
	
	public boolean accept(Resource res);
}
