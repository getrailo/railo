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
package railo.runtime.functions.struct;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Struct;

public class JsonStruct implements Function {

	private static final long serialVersionUID = 3030769464899375329L;

	public static Struct call(PageContext pc , Object[] objArr) throws PageException {
		return Struct_._call(objArr, "invalid argument for JSON struct, only named arguments are allowed like {name:\"value\",name2:\"value2\"}",Struct.TYPE_LINKED);
    	
	}
}
