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

import java.io.UnsupportedEncodingException;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;

/**
 * Implements the CFML Function SetEncoding
 */
public final class SetEncoding implements Function {
      
    public static String call(PageContext pc , String scope, String charset) throws PageException {
        scope=scope.trim().toLowerCase();
        try {
            if(scope.equals("url"))(pc.urlScope()).setEncoding(pc.getApplicationContext(),charset);
            else if(scope.equals("form"))(pc.formScope()).setEncoding(pc.getApplicationContext(),charset);
            else throw new FunctionException(pc,"setEncoding",1,"scope","scope must have the one of the following values [url,form] not ["+scope+"]");
            
        } catch (UnsupportedEncodingException e) {
            throw Caster.toPageException(e);
        }
        return "";  
    }
	
	
}