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

import java.util.Set;

import railo.runtime.exp.PageException;
import railo.runtime.type.Array;
import railo.runtime.type.Collection;

/**
 * interface for Argument scope
 */
public interface Argument extends Scope,Array,BindScope {

    /** 
     * sets if scope is binded to a other variable for using outside of a udf 
     * @param bind 
     */
    public abstract void setBind(boolean bind);

    /** 
     * @return returns if scope is binded to a other variable for using outside of a udf 
     */
    public abstract boolean isBind();

    /**
     * insert a key in argument scope at defined position
     * @param index
     * @param key
     * @param value
     * @return boolean
     * @throws PageException
     */
    public abstract boolean insert(int index, String key, Object value)
            throws PageException;
    

	
	public Object setArgument(Object obj) throws PageException;

	public static final Object NULL = null;
	
	public Object getFunctionArgument(String key, Object defaultValue);

	public Object getFunctionArgument(Collection.Key key, Object defaultValue);
	
	public void setFunctionArgumentNames(Set functionArgumentNames);

	public boolean containsFunctionArgumentKey(Key key);

}