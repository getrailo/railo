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
package railo.runtime.cache;

import java.io.IOException;

import railo.commons.io.cache.Cache;
import railo.runtime.config.Config;
import railo.runtime.type.Struct;

public interface CacheConnection {

	/**
	 * @return the readOnly
	 */
	public abstract boolean isReadOnly();

	public abstract Cache getInstance(Config config) throws IOException;

	/**
	 * @return the name
	 */
	public abstract String getName();

	/**
	 * @return the clazz
	 */
	public abstract Class getClazz();

	/**
	 * @return the custom
	 */
	public abstract Struct getCustom();

	public CacheConnection duplicate(Config config) throws IOException;

	public boolean isStorage();

}