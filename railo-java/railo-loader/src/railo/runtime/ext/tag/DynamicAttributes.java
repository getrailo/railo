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
package railo.runtime.ext.tag;

import railo.runtime.type.Collection;


/**
 * Interface for Dynmaic Attributes for tags (in j2ee at version 1.4.x)
 */
public interface DynamicAttributes {
	
	/**
	 * @param uri the namespace of the attribute, or null if in the default namespace.
	 * @param localName the name of the attribute being set.
	 * @param value the value of the attribute
	 * @deprecated use instead <code>setDynamicAttribute(String uri, Collection.Key localName, Object value)</code>
	 */
	public void setDynamicAttribute(String uri, String localName, Object value);
	
	/**
	 * @param uri the namespace of the attribute, or null if in the default namespace.
	 * @param localName the name of the attribute being set.
	 * @param value the value of the attribute
	 */
	public void setDynamicAttribute(String uri, Collection.Key localName, Object value);

}