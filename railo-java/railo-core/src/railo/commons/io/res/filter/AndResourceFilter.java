package railo.commons.io.res.filter;

import railo.commons.io.res.Resource;


/**
 * A FileFilter providing conditional OR logic across a list of file filters. 
 * This filter returns true if any filters in the list return true. Otherwise, it returns false. 
 * Checking of the file filter list stops when the first filter returns true. 
 */
public final class AndResourceFilter implements ResourceFilter {
    
    private final ResourceFilter[] filters;

    /**
     * @param filters
     */
    public AndResourceFilter(ResourceFilter[] filters) {
        this.filters=filters;
    }

    @Override
    public boolean accept(Resource f) {
        for(int i=0;i<filters.length;i++) {
            if(!filters[i].accept(f)) return false;
        }
        return true;
    }
}
