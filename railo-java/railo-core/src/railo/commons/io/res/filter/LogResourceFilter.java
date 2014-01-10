package railo.commons.io.res.filter;

import railo.aprint;
import railo.commons.io.res.Resource;

public class LogResourceFilter implements ResourceFilter {
	
	private ResourceFilter filter;

	public LogResourceFilter(ResourceFilter filter){
		this.filter=filter;
	}

	@Override
	public boolean accept(Resource res) {
		boolean rtn = filter.accept(res);
		aprint.o("accept:"+res+"->"+rtn);
		return rtn;
	}

}
