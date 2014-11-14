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
package railo.runtime.functions.international;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.ext.function.Function;

/**
 * Implements the CFML Function getEncoding
 */
public final class GetEncoding implements Function {
	public static String call(PageContext pc , String scope) throws FunctionException {
        scope=scope.trim().toLowerCase();
        
        if(scope.equals("url"))return (pc.urlScope()).getEncoding();
        if(scope.equals("form"))return (pc.formScope()).getEncoding();
        throw new FunctionException(pc,"getEncoding",1,"scope","scope must have the one of the following values [url,form] not ["+scope+"]");
        
	}
}