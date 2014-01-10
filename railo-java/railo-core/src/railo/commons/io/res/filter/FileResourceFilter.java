package railo.commons.io.res.filter;

import railo.commons.io.res.Resource;

/**
 * accept only directories
 */
public final class FileResourceFilter implements ResourceFilter {

    public static final FileResourceFilter FILTER = new FileResourceFilter();

	@Override
    public boolean accept(Resource pathname) {
        return pathname.isFile();
    }

}