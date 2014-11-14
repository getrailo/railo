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
package railo.runtime.util;

import java.util.Enumeration;
import java.util.Iterator;

/**
 * 
 */
public final class IteratorWrapper implements Iterator {
    
    private Enumeration e;

    /**
     * constructor of the class
     * @param enum
     */
    public IteratorWrapper(Enumeration e) {
        this.e=e;
    }

    /**
     * @see java.util.Iterator#remove()
     */
    public void remove() {
    }

    /**
     * @see java.util.Iterator#hasNext()
     */
    public boolean hasNext() {
        return e.hasMoreElements();
    }

    /**
     * @see java.util.Iterator#next()
     */
    public Object next() {
        return e.nextElement();
    }
}