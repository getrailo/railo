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
package railo.runtime.type;

import railo.runtime.ComponentImpl;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;

// FUTURE add to interface UDF

public interface UDFPlus extends UDF {
	

	public static final int RETURN_FORMAT_JAVA=5;
	

    /**
     * call user defined Funcion with a struct
     * @param pageContext
     * @param values named values
     * @param doIncludePath 
     * @return return value of the function
     * @throws PageException
     */
    public abstract Object callWithNamedValues(PageContext pageContext,Collection.Key calledName,Struct values, boolean doIncludePath) throws PageException;

    /**
     * call user defined Funcion with parameters as Object Array
     * @param pageContext
     * @param args parameters for the function
     * @param doIncludePath 
     * @return return value of the function
     * @throws PageException
     */
    public abstract Object call(PageContext pageContext, Collection.Key calledName, Object[] args, boolean doIncludePath) throws PageException;
	
	 public Object getDefaultValue(PageContext pc, int index, Object defaultValue) throws PageException;
	 public int getIndex();
	 
	 
	 // !!!!!! do not move to public interface, make for example a interface calle UDFMod
	 public void setOwnerComponent(ComponentImpl component);
	 public void setAccess(int access);
	 
	 public abstract int getReturnFormat(int defaultFormat);
	    
}
