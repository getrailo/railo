/**
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library.  If not, see <http://www.gnu.org/licenses/>.
 * 
 **/
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

    @Override
    public boolean accept(Resource res) {
        for(int i=0;i<filters.length;i++) {
            if(!filters[i].accept(res)) return false;
        }
        return true;
    }
}
