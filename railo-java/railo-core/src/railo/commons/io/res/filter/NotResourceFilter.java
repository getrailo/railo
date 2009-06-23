

package railo.commons.io.res.filter;


import railo.commons.io.res.Resource;


/**
 * This filter produces a logical NOT of the filters specified. 
 */
public final class NotResourceFilter implements ResourceFilter {
    
    private final ResourceFilter filter;

    /**
     * @param filter
     */
    public NotResourceFilter(ResourceFilter filter) {
        this.filter=filter;
    }

    /**
     * @see railo.commons.io.res.filter.ResourceFilter#accept(railo.commons.io.res.Resource)
     */
    public boolean accept(Resource f) {
        return !filter.accept(f);
    }
}
