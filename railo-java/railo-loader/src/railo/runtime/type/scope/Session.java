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
package railo.runtime.type.scope;

import railo.runtime.type.Collection;


/**
 * 
 */
public interface Session extends Scope,UserScope {
    /**
     * @return returns the last acces to this session scope
     * @deprecated 
     */
    public abstract long getLastAccess();

    /**
     * @return returns the actuell timespan of the session
     * @deprecated 
     */
    public abstract long getTimeSpan();
    

	public long getCreated();


    /**
     * @return is the scope expired or not
     */
    public abstract boolean isExpired();

	/**
	 * sets the last access timestamp to now
	 */
	public abstract void touch();
	
	public int _getId();

	/**
	 * @return all keys except the readpnly ones (cfid,cftoken,hitcount,lastvisit ...)
	 */
	public abstract Collection.Key[] pureKeys();

}