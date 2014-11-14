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

    @Override
    public boolean accept(Resource f) {
        return !filter.accept(f);
    }
}
