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
package railo.runtime.util.pool;

/**
 * Interface for a Pool
 */
public interface Pool {
	
	/**
	 * adds a new object to the pool, if object is already in the Pool, it will be overwritten
	 * @param key key for the Objects
	 * @param handler pool handler object
	 */
	public void set(Object key, PoolHandler handler);
	
	/**
	 * gets a Object from the pool
	 * @param key key for the Objects
	 * @return
	 */
	public PoolHandler get(Object key);

	/**
	 * checks if Object exists in Pool
	 * @param key key for the Objects
	 * @return object exists or not
	 */
	public boolean exists(Object key);
	
	/**
	 * remove a Object from the pool
	 * @param key key for the Objects
	 * @return
	 */
	public boolean remove(Object key);
	
}