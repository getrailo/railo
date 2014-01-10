package railo.commons.io.res.filter;

import railo.commons.io.res.Resource;

public class IgnoreSystemFiles implements ResourceNameFilter {

	public static final ResourceNameFilter INSTANCE=new IgnoreSystemFiles();
	
	@Override
	public boolean accept(Resource parent, String name) {
		return !".DS_Store".equals(name);
	}

}
