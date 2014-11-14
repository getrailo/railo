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
package railo.runtime.dump;

import java.io.Serializable;

import railo.runtime.PageContext;


/**
 * this interface make a object printable, also to a simple object
 */
public interface Dumpable extends Serializable {
    
	/**
	 * method to print out information to a object as HTML
	 * @param pageContext
	 * @return HTML print out
	 */
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties properties);
}