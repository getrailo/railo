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

import java.util.List;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.type.Collection;
import railo.runtime.type.Query;
import railo.runtime.type.Struct;
import railo.runtime.util.QueryStack;

/**
 * interface of the cope undefined
 */
public interface Undefined extends Scope {

	public static final int MODE_NO_LOCAL_AND_ARGUMENTS=0;
	public static final int MODE_LOCAL_OR_ARGUMENTS_ONLY_WHEN_EXISTS=1;
	public static final int MODE_LOCAL_OR_ARGUMENTS_ALWAYS=2;
	

    /**
     * @return returns the current local scope defined in the undefined scope
     */
    public abstract Local localScope();
    
    public Argument argumentsScope();
    
    public Variables variablesScope();

    /**
     * sets mode of scope
     * @param mode new mode
     * @return old mode
     */
    public abstract int setMode(int mode);
    
    public boolean getLocalAlways();
    

    /**
     * sets the functions scopes 
     * @param local local scope
     * @param argument argument scope
     */
    public abstract void setFunctionScopes(Local local, Argument argument);

    /**
     * @return returns actuell collection stack
     */
    public abstract QueryStack getQueryStack();

    /**
     * sets a individual query stack to the undefined scope
     * @param qryStack Query stack
     */
    public abstract void setQueryStack(QueryStack qryStack);

    /**
     * add a collection to the undefined scope
     * @param qry  Query to add to undefined scope
     */
    public abstract void addQuery(Query qry);

    /**
     * remove a collection from the undefined scope
     */
    public abstract void removeQuery();

    /**
     * return value matching key, if value is from Query return a QueryColumn
     * @param key
     * @return return matched value
     * @throws PageException
     * @deprecated use instead <code>{@link #getCollection(railo.runtime.type.Collection.Key)}</code>
	 */
    public abstract Object getCollection(String key) throws PageException;


	public List<String> getScopeNames();
	
    /**
     * return value matching key, if value is from Query return a QueryColumn
     * @param key
     * @return return matched value
     * @throws PageException
     */
    public abstract Object getCollection(Collection.Key key) throws PageException;

    /**
     * gets a key from all cascaded scopes, but not from variables scope 
     * @param key key to get
     * @return matching value or null
     * @deprecated use instead <code>{@link #getCascading(railo.runtime.type.Collection.Key)}</code>
	 */
    public abstract Object getCascading(String key);

    /**
     * gets a key from all cascaded scopes, but not from variables scope 
     * @param key key to get
     * @return matching value or null
     */
    public abstract Object getCascading(Collection.Key key);

    /**
     * change the variable scope
     * @param scope
     */
    public abstract void setVariableScope(Variables scope);

	/**
	 * @return if check for arguments and local scope values
	 */
	public boolean getCheckArguments();
	
	public Struct getScope(Collection.Key key);
	
	public boolean setAllowImplicidQueryCall(boolean allowImplicidQueryCall);
	
	public void reinitialize(PageContext pc) ;
}