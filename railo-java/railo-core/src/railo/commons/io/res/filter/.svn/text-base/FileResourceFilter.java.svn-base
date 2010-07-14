package railo.commons.io.res.filter;

import railo.commons.io.res.Resource;

/**
 * accept only directories
 */
public final class FileResourceFilter implements ResourceFilter {

    public static final FileResourceFilter FILTER = new FileResourceFilter();

	/**
     *
     * @see railo.commons.io.res.filter.ResourceFilter#accept(railo.commons.io.res.Resource)
     */
    public boolean accept(Resource pathname) {
        return pathname.isFile();
    }

}