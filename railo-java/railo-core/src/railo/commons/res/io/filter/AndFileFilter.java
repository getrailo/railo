

package railo.commons.res.io.filter;

import railo.commons.io.res.Resource;
import railo.commons.io.res.filter.ResourceFilter;

/**
 * A FileFilter providing conditional AND logic across a list of file filters. 
 * This filter returns true if all filters in the list return true. 
 * Otherwise, it returns false. Checking of the file filter list stops when the first filter returns false. 
 */
public final class AndFileFilter implements ResourceFilter {
    
    private ResourceFilter[] filters;

    /**
     * @param filters
     */
    public AndFileFilter(ResourceFilter[] filters) {
        this.filters=filters;
    }

    /**
     *
     * @see railo.commons.io.res.filter.ResourceFilter#accept(railo.commons.io.res.Resource)
     */
    public boolean accept(Resource res) {
        for(int i=0;i<filters.length;i++) {
            if(!filters[i].accept(res)) return false;
        }
        return true;
    }
}
