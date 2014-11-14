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
package railo.runtime.debug;

import java.util.Comparator;



/**
 * 
 */
public final class QueryEntryComparator implements Comparator<QueryEntry> {

    @Override
    public int compare(QueryEntry o1, QueryEntry o2) {
        return compare((QueryEntryPro)o1,(QueryEntryPro)o2);
    }
    
    public int compare(QueryEntryPro qe1,QueryEntryPro qe2) {
        return (int)((qe2.getExecutionTime())-(qe1.getExecutionTime()));
    }
}